package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PatchLeaveRes {
    private final Long leaveId;
    private final String distinction;
    private final String title;
    private final Integer useDays;
    private final Integer totalDays;
}