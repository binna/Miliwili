package com.app.miliwili.src.emotionRecord.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostEmotionRecordReq {
    private String date;
    private String content;
    private Integer emotion;
}