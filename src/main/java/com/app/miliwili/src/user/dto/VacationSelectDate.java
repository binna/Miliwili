package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class VacationSelectDate {
    private final Long id;
    private final String title;
    private final Integer useDays;
    private final Integer totalDays;
    private final Integer count;
}