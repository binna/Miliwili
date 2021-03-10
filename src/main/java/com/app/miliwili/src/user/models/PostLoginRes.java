package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostLoginRes {
    private Boolean isMember;
    private String jwt;
}
