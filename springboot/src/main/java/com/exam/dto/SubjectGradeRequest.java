package com.exam.dto;

import lombok.Data;

@Data
public class SubjectGradeRequest {
    private String question;
    private String referenceAnswer;
    private String studentAnswer;
    private Integer maxScore;
}
