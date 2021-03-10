package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetNormalUserEndDate {
    private final Long id;
    private final String profileImg;
    private final String name;
    private final String endDate;
    private final String firstDate;
    private final String secondDate;
    private final String thirdDate;
    private final String goal;
}
