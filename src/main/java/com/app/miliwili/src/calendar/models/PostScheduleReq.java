package com.app.miliwili.src.calendar.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostScheduleReq {
    private String color;
    private String distinction;
    private String title;
    private String startDate;
    private String endDate;
    private String repetition;
    private String push;
    private String pushDeviceToken;
    private List<OrdinaryLeaveReq> ordinaryLeave;
    private List<WorkReq> toDoList;
}