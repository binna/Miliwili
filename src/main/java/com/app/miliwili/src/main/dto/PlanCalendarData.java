package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Data
public class PlanCalendarData {
    private final String color;
    private final LocalDate startDate;
    private final LocalDate endDate;
}