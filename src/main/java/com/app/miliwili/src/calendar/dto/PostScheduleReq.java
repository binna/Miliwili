package com.app.miliwili.src.calendar.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostScheduleReq {
    private String color;
    private String scheduleType;
    private String title;
    private String startDate;
    private String endDate;
    private String push;
    private String pushDeviceToken;
    private List<ScheduleVacationReq> scheduleVacation;
    private List<WorkReq> toDoList;
}