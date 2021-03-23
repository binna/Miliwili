package com.app.miliwili.src.calendar.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostPlanReq {
    private String color;
    private String planType;
    private String title;
    private String startDate;
    private String endDate;
    private String push;
    private String pushDeviceToken;
    private List<PlanVacationReq> planVacation;
    private List<WorkReq> work;
}