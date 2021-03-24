package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserRes {
    private final Long userId;
    private final String name;
    private final String profileImg;
    private final String birthday;
    private final Integer stateIdx;
    private final String serveType;
    private final String startDate;
    private final String endDate;
    private final String strPrivate;
    private final String strCorporal;
    private final String strSergeant;
    private final Integer hobong;
    private final Integer normalPromotionStateIdx;
    private final String proDate;
    private final String goal;
}