package com.exam.controller;

import com.exam.Result.ApiResult;
import com.exam.Result.ApiResultHandler;
import com.exam.entity.SubjectQuestion;
import com.exam.service.serviceimpl.SubjectQuestionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主观题控制器
 */
@RestController
public class SubjectQuestionController {

    @Autowired
    private SubjectQuestionServiceImpl subjectQuestionService;

    @GetMapping("/subjectQuestionId")
    public ApiResult findOnlyQuestion() {
        SubjectQuestion res = subjectQuestionService.findOnlyQuestionId();
        return ApiResultHandler.buildApiResult(200, "查询成功", res);
    }

    @PostMapping("/subjectQuestion")
    public ApiResult add(@RequestBody SubjectQuestion subjectQuestion) {
        int res = subjectQuestionService.add(subjectQuestion);
        if (res != 0) {
            return ApiResultHandler.buildApiResult(200, "添加成功", res);
        }
        return ApiResultHandler.buildApiResult(400, "添加失败", res);
    }
}
