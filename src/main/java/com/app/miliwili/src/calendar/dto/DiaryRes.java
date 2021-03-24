package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DiaryRes {
    private final Long diaryId;
    private final String date;
    private final String title;
    private final String content;
}