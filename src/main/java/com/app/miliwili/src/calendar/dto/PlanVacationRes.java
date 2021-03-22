package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PlanVacationRes {
    private final Long planVacationId;
    private final Integer count;
    private final Long vacationId;
}