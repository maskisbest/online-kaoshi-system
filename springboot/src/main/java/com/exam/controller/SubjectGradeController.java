package com.exam.controller;

import com.exam.Result.ApiResult;
import com.exam.Result.ApiResultHandler;
import com.exam.dto.SubjectGradeRequest;
import com.exam.dto.SubjectGradeResponse;
import com.exam.service.DashScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubjectGradeController {

    @Autowired
    private DashScopeService dashScopeService;

    @PostMapping("/subject/grade")
    public ApiResult grade(@RequestBody SubjectGradeRequest request) {
        SubjectGradeResponse res = dashScopeService.grade(request);
        return ApiResultHandler.buildApiResult(200, "评分完成", res);
    }
}
