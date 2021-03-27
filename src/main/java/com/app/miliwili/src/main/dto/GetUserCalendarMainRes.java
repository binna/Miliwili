package com.app.miliwili.src.main.dto;

import com.app.miliwili.src.calendar.dto.GetDDayRes;
import com.app.miliwili.src.calendar.dto.GetPlanRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GetUserCalendarMainRes {
    // 회원정보
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
    // 휴가정보
    private final Integer vacationTotalDays;
    private final Integer vacationUseDays;
    // 디데이
    private List<GetDDayRes> dday;
    // 일정
    private List<GetPlanRes> plan;
}