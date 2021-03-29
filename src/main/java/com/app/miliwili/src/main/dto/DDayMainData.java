package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Data
public class DDayMainData {
    private final Long id;
    private final String ddayType;
    private final String title;
    private final String choiceCalendar;
    private final LocalDate date;
}