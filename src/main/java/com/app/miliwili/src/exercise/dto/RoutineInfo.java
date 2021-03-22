package com.app.miliwili.src.exercise.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineInfo {
    private String routineName;
    private String routineRepeatDay;
    private Long routineId;
    private Boolean isDoneRoutine;
}
