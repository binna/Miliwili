package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class VacationRes {
    private final Long vacationId;
    private final String title;
    private final Integer useDays;
    private final Integer totalDays;
}