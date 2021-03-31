package com.app.miliwili.src.emotionRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DayEmotionRecordRes {
    private final Long emotionRecordId;
    private final String date;
    private final String content;
    private final Integer emotion;
    private final String emotionText;
}