package com.app.miliwili.src.emotionRecord.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PatchEmotionRecordReq {
    private String content;
    private Integer emotion;
}