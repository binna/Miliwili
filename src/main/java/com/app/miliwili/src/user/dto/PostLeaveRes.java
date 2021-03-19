package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PostLeaveRes {
    private final Long leaveId;
    private final String title;
    private final Integer total;
}