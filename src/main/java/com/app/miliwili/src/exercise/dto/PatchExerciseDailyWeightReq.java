package com.app.miliwili.src.exercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PatchExerciseDailyWeightReq {
    private Double dayWeight;
    private String dayDate;
}
