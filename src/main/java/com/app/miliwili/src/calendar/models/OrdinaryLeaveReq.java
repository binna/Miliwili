package com.app.miliwili.src.calendar.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class OrdinaryLeaveReq {
    private String startDate;
    private String endDate;
}