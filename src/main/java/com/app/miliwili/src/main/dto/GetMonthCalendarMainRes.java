package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GetMonthCalendarMainRes {
    private final List<PlanCalendarData> planCalendar;
    private final List<String> ddayCalendar;
}