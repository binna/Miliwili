package com.app.miliwili.src.calendar.dto;

import com.google.common.base.Suppliers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostDDayReq {
    private String distinction;
    private String title;
    private String subTitle;
    private String startDay;
    private String endDay;
    private String link;
    private String choiceCalendar;
    private BigDecimal placeLat;
    private BigDecimal placeLon;
    private List<Suppliers> suppliers;
}