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
public class ReportExercise {
    private String exerciseName;
    private String exerciseStatus;
    private Integer doneSet;
    private Boolean isDone;
    private List<GetStartExerciseDetailSetRes> setList;
}
