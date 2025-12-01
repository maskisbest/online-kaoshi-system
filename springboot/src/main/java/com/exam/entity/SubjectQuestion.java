package com.exam.entity;

import lombok.Data;

// 主观题实体
@Data
public class SubjectQuestion {
    private Integer questionId;

    private String subject;

    private String question;

    /** 参考答案/要点描述 */
    private String answer;

    private String analysis;

    private String level;

    private String section;

    private Integer score;
}
