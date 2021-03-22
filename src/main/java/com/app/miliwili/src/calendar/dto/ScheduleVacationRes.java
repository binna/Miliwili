package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ScheduleVacationRes {
    private final Long vacationId;
    private final Integer count;

    @AllArgsConstructor
    @Builder
    @Getter
    public static class ScheduleDate {
        private final String date;
    }
}