package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GetLeaveRes {
    private final String title;
    private final Integer use;
    private final Integer total;
}