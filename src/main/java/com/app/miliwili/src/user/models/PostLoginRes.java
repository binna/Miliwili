package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLoginRes {
    private boolean isMember;
    private String jwt;
}
