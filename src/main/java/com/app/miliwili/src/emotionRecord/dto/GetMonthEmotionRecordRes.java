package com.app.miliwili.src.emotionRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GetMonthEmotionRecordRes {
    private final List<MonthEmotionRecordRes> month;
    private final DayEmotionRecordRes today;
}