package com.app.miliwili.src.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSignUpReq {
    private String name;
    private int stateIdx;
    private String serveType;
    private String startDate;
    private String endDate;
    private String strPrivate;
    private String strCorporal;
    private String strSergeant;
    private String proDate;
    private String goal;
}
