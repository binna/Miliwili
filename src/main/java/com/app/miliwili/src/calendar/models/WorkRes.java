package com.app.miliwili.src.calendar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WorkRes {
    private final String content;
    private final String processingStatus;
}