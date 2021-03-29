package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class GetMyInfo {
    private final String profileImg;
    private final String name;
    private final LocalDate birthday;
}
