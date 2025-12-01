package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.Result.ApiResult;
import com.exam.service.serviceimpl.AnswerServiceImpl;
import com.exam.Result.ApiResultHandler;
import com.exam.vo.AnswerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * 查询所有题库
 */
@RestController
public class AnswerController {
    @Autowired
    private AnswerServiceImpl answerService;

    @GetMapping("/answers/{page}/{size}")
    public ApiResult findAllQuestion(@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        try {
            Page<AnswerVO> answerVOPage = new Page<>(page, size);
            IPage<AnswerVO> answerVOIPage = answerService.findAll(answerVOPage);
            return ApiResultHandler.buildApiResult(200, "查询所有题库", answerVOIPage);
        } catch (Exception e) {
            // 返回简洁可读的错误提示，避免将栈信息直接暴露给前端
            return ApiResultHandler.buildApiResult(500, "题库查询失败：请检查数据库表是否存在（含 subject_question）且结构一致", null);
        }

    }
}
