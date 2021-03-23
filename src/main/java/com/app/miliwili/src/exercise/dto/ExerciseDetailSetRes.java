package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseDetailSetRes {
    private String setStr;
    private Double weight;
    private Integer count;
    private Integer time;

}
