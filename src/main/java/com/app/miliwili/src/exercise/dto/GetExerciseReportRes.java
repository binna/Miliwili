package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetExerciseReportRes {
    private String totalTime;
    private String reportDate;
    private List<ReportExercise> exerciseList;
}
