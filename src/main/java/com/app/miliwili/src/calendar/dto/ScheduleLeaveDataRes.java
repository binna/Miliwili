package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ScheduleLeaveDataRes {
    private final Long leaveId;
    private final Integer count;
}