package com.app.miliwili.src.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PatchUserReq {
    private String name;
    private Integer stateIdx;
    private String serveType;
    private String startDate;
    private String endDate;
    private String strPrivate;
    private String strCorporal;
    private String strSergeant;
    private String proDate;
    private String goal;
}