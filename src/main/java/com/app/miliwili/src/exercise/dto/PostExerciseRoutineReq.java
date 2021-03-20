package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PostExerciseRoutineReq {
    private String routineName;
    private String bodyPart;
    private String repeatDay;
    private List<String> detailName;
    private List<Integer> detailType;
    private List<String> detailTypeContext;
    private List<Boolean> detailSetEqual;
    private List<Integer> detailSet;
}
