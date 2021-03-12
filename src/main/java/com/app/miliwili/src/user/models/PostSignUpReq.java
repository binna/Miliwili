package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSignUpReq {
    private final String name;
    private final Integer stateIdx;
    private final String serveType;
    private final String startDate;
    private final String endDate;
    private final String strPrivate;
    private final String strCorporal;
    private final String strSergeant;
    private final String proDate;
    private final String goal;
}