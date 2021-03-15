package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class WorkRes {
    private final String content;
    private final String processingStatus;
}