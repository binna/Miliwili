package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostSignUpRes {
    private long userId;
    private String jwt;

}
