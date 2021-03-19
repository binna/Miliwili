package com.app.miliwili.src.calendar.dto;

import com.app.miliwili.src.user.dto.PostLeaveRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class PostScheduleRes {
    private final Long scheduleId;
    private final String color;
    private final String distinction;
    private final String title;
    private final String startDate;
    private final String endDate;
    private final String repetition;
    private final String push;
    private final List<PostLeaveRes> ordinaryLeave;
    private final List<WorkRes> toDoList;
}