package com.app.miliwili.src.emotionRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MonthEmotionRecordRes {
    private final String date;
    private final Integer emotion;
}