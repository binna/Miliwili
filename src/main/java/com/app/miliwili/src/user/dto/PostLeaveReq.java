package com.app.miliwili.src.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostLeaveReq {
    private String title;
    private Integer total;
}