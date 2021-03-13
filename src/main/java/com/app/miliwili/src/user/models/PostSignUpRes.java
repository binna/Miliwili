package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PostSignUpRes {
    private final Long userId;
    private final String jwt;
}