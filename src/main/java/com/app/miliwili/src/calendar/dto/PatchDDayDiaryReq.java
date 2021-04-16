package com.app.miliwili.src.calendar.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PatchDDayDiaryReq {
    private String content;
    private List<TargetAmountReq> targetAmount;
}