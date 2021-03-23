package com.app.miliwili.src.exercise.dto;

import com.app.miliwili.src.exercise.model.ExerciseRoutineDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchExerciseRoutineReq {
    private String routineName;
    private String bodyPart;
    private List<String> repeatDay;
//    private List<ExerciseDetail>
}
