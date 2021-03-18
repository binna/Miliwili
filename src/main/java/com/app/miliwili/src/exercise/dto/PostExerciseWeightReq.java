package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostExerciseWeightReq {
    private Integer dayWeight;
    private String noMean;
}
