package com.app.miliwili.src.exercise.dto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseDetailRes {
    private String exerciseName;
    private Integer exerciseType;
    private Integer setCount;
    private Boolean isSetSame;
    private List<ExerciseDetailSetRes> setDetailList;
}
