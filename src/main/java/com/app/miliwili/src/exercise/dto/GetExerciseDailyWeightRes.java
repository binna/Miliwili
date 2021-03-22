package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class GetExerciseDailyWeightRes {
    private Double goalWeight;
    private List<String> dailyWeightList;
    private List<String> weightDayList;
}
