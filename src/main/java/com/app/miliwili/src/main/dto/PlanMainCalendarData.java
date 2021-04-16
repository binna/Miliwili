package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PlanMainCalendarData {
    private final Long planId;
    private final String color;
    private final String title;
}