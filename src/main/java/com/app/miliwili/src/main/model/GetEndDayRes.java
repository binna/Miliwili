package com.app.miliwili.src.main.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetEndDayRes {
    private final long id;
    private final String profileImg;
    private final String name;
    private final String state;
    private final String endDday;
    private final String hobongStr;
    private final String hobongDday;
    private final String normalProStr;
    private final String normalProDday;
    private final String abnormalProStr;
    private final String abnormalProDday;
    private final int endRate;
    private final String endDate;
    private final String goal;

}
