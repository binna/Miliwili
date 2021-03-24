package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GetPlanRes {
    private final Long planId;
    private final String startDate;
    private final String endDate;
    private final String dateInfo;
    private final String planType;
    private final List<WorkRes> work;
    private final List<DiaryRes> diary;
}