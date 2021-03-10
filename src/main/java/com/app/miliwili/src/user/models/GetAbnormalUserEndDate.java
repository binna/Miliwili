package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetAbnormalUserEndDate {
    private final Long id;
    private final String profileImg;
    private final String name;
    private final String endDate;
    private final String proDate;
    private final String goal;

}
