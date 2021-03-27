package com.app.miliwili.src.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class PatchUserReq {
    private String name;
    private String birthday;
    private String profileImg;
    private String serveType;
    private String startDate;
    private String endDate;
    private String strPrivate;
    private String strCorporal;
    private String strSergeant;
    private String proDate;
    private String goal;
}