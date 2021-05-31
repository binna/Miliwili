package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
public class DDayDiaryRes {
    private final Long diaryId;
    private final String date;
    private final String title;
    private final String content;
    private final Set<TargetAmountRes> targetAmount;
}