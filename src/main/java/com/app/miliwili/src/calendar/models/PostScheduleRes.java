package com.app.miliwili.src.calendar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostScheduleRes {
    private final Long scheduleId;
    private final String color;
    private final String distinction;
    private final String title;
    private final String startDate;
    private final String endDate;
    private final String repetition;
    private final String push;
    private final List<OrdinaryLeaveRes> ordinaryLeave;
    private final List<WorkRes> toDoList;
}