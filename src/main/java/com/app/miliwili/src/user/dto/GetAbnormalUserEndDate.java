package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class GetAbnormalUserEndDate {
    private final Long id;
    private final String profileImg;
    private final String name;
    private final LocalDate endDate;
    private final LocalDate startDate;
    private final LocalDate proDate;
    private final String goal;
}