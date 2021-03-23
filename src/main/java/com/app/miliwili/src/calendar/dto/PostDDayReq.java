package com.app.miliwili.src.calendar.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostDDayReq {
    private String ddayType;
    private String title;
    private String subTitle;
    private String date;
    private String link;
    private String choiceCalendar;
    private BigDecimal placeLat;
    private BigDecimal placeLon;
    private List<WorkReq> work;
}