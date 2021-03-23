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
public class GetExerciseRoutineRes {
    private String routineName;
    private String bodyPart;
    private List<Integer> repeatDay;
    private List<ExerciseDetailRes> detailResList;
}
