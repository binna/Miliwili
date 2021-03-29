package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GetUserCalendarMainRes {
    private final String name;
    private final String profileImg;
    private final String birthday;
    private final Integer stateIdx;
    private final String serveType;
    private final String startDate;
    private final String endDate;
    private final String strPrivate;
    private final String strCorporal;
    private final String strSergeant;
    private final Integer hobong;
    private final Integer normalPromotionStateIdx;
    private final String proDate;
    private final String goal;
    private final Integer vacationTotalDays;
    private final Integer vacationUseDays;
    private final List<DDayMainDataRes> dday;
    private final List<PlanMainData> plan;
}