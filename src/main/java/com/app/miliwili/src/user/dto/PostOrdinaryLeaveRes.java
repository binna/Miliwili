package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PostOrdinaryLeaveRes {
    private final String startDate;
    private final String endDate;
}