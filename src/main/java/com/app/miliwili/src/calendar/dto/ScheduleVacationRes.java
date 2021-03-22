package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ScheduleVacationRes {
    private final Long scheduleVacationId;
    private final Integer count;
    private final Long vacationId;
}