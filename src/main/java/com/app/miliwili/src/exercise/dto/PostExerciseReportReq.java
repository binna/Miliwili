package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostExerciseReportReq {
    private String totalTime;
    private String exerciseStatus;
}
