package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GetDDayRes {
    private final Long ddayId;
    private final String date;
    private final String dateInfo;
    private final String ddayType;
    private final String choiceCalendarText;
    private final String convertLunarToSolar;
    private final List<WorkRes> work;
    private final List<DiaryRes> diary;
}