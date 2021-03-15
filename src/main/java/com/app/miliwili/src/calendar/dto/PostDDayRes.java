package com.app.miliwili.src.calendar.dto;

import com.app.miliwili.src.calendar.models.Supplies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class PostDDayRes {
    private final Long dDayId;
    private final String distinction;
    private final String title;
    private final String subtitle;
    private final String startDay;
    private final String endDay;
    private final String link;
    private final String choiceCalendar;
    private final BigDecimal placeLat;
    private final BigDecimal placeLon;
    private final List<Supplies> supplies;
}