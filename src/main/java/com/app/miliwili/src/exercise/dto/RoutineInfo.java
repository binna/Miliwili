package com.app.miliwili.src.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutineInfo {
    private String routineName;
    private String routineRepeatDay;
    private Long routineId;
    private Boolean isDoneRoutine;
}
