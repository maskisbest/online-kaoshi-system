package com.exam.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SubjectGradeResponse {

    private Double score;
    private String justification;

    @JsonProperty("missedPoints")
    @JsonAlias("missed_points")
    private List<String> missedPoints = new ArrayList<>();
}
