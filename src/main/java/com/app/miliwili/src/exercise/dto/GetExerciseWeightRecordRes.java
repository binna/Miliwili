package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetExerciseWeightRecordRes {
    private Double goalWeight;
    private List<Double> monthWeight;
    private List<String> monthWeightMonth;
    private List<String> dayWeightDay;
    private List<String> dayWeight;
    private List<Double> dayDif;
}
