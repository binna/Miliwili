package com.app.miliwili.src.calendar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrdinaryLeaveRes {
    String startDate;
    String endDate;
}