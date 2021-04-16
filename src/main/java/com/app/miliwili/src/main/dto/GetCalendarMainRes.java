package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GetCalendarMainRes {
    private final List<PlanCalendarDataRes> planCalendar;
    private final List<String> ddayCalendar;
    private final List<DDayMainDataRes> dday;
    private final List<PlanMainCalendarData> plan;
}