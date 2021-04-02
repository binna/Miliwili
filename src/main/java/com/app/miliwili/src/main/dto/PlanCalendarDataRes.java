package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PlanCalendarDataRes {
    private final String color;
    private final String startDate;
    private final String endDate;
}