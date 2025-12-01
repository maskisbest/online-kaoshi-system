package com.exam.service;

import com.exam.dto.SubjectGradeRequest;
import com.exam.dto.SubjectGradeResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 调用达摩盘大模型接口，对主观题进行评分。
 */
@Service
public class DashScopeService {

    @Value("${dashscope.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    public SubjectGradeResponse grade(SubjectGradeRequest req) {
        SubjectGradeResponse resp = new SubjectGradeResponse();
        resp.setScore(0.0);
        resp.setMissedPoints(Collections.emptyList());
        if (!StringUtils.hasText(apiKey)) {
            resp.setJustification("未配置 dashscope.api-key，已返回 0 分");
            return resp;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = buildPrompt(req);
            Map<String, Object> body = Map.of(
                    "model", "qwen-plus",
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are a strict but fair grader."),
                            Map.of("role", "user", "content", prompt)
                    )
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(API_URL, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return parseResponse(responseEntity.getBody(), resp, req);
            }
            resp.setJustification("评分接口响应异常：" + responseEntity.getStatusCode());
            return resp;
        } catch (Exception ex) {
            resp.setJustification("评分接口调用失败：" + ex.getMessage());
            return resp;
        }
    }

    private String buildPrompt(SubjectGradeRequest req) {
        return "请作为严谨、公正的阅卷老师，按照以下规则对主观题打分并给出简明反馈：\n" +
                "- 题目：" + nullSafe(req.getQuestion()) + "\n" +
                "- 参考答案要点：" + nullSafe(req.getReferenceAnswer()) + "\n" +
                "- 学生作答：" + nullSafe(req.getStudentAnswer()) + "\n" +
                "- 本题满分：" + (req.getMaxScore() == null ? 0 : req.getMaxScore()) + " 分\n\n" +
                "评分要求：\n" +
                "1. 逐条比对参考答案要点，按照覆盖程度给部分分；如果学生答案完全偏题或与题目无关，给 0 分。\n" +
                "2. 若学生答案表述不同但意义一致，也视为命中要点。\n" +
                "3. 若有明显错误或与要点相反的描述，适当扣分。\n" +
                "4. 不苛求格式或措辞，只关注内容正确性和完整度。\n" +
                "5. 输出时请保持客观、公正、简洁。\n\n" +
                "请严格按以下 JSON 输出（无需其他文字）：\n" +
                "{\n" +
                "  \"score\": <该题得分，0~" + (req.getMaxScore() == null ? 0 : req.getMaxScore()) + " 的数字，允许小数>,\n" +
                "  \"justification\": \"一句话说明给分依据，指出命中的要点或缺失的要点\",\n" +
                "  \"missed_points\": [\"未命中的要点1\", \"未命中的要点2\"]\n" +
                "}";
    }

    private SubjectGradeResponse parseResponse(String body, SubjectGradeResponse fallback, SubjectGradeRequest req) {
        try {
            JsonNode root = mapper.readTree(body);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode content = choices.get(0).path("message").path("content");
                if (content.isTextual()) {
                    String text = content.asText();
                    // 尝试将返回文本解析为 JSON
                    SubjectGradeResponse resp = mapper.readValue(text, SubjectGradeResponse.class);
                    if (resp.getScore() == null) {
                        resp.setScore(0.0);
                    }
                    // 边界处理
                    double max = req.getMaxScore() == null ? 0 : req.getMaxScore();
                    if (resp.getScore() < 0) resp.setScore(0.0);
                    if (resp.getScore() > max) resp.setScore(max);
                    return resp;
                }
            }
            fallback.setJustification("评分接口未返回有效内容");
            return fallback;
        } catch (Exception ex) {
            fallback.setJustification("解析评分结果失败：" + ex.getMessage());
            return fallback;
        }
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
