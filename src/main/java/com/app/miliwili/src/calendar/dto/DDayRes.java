package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class DDayRes {
    private final Long ddayId;
    private final String ddayType;
    private final String title;
    private final String subtitle;
    private final String date;
    private final String link;
    private final String choiceCalendar;
    private final BigDecimal placeLat;
    private final BigDecimal placeLon;
    private final List<WorkRes> work;
}