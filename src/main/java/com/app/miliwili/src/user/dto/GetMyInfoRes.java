package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetMyInfoRes {
    private final String profileImg;
    private final String name;
    private final String birthday;
}
