package com.app.miliwili.src.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostSignUpReq {
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
    private String socialType;


    public void setStrPrivate(String strPrivate) {
        this.strPrivate = strPrivate;
    }
    public void setStrCorporal(String strCorporal) {
        this.strCorporal = strCorporal;
    }
    public void setStrSergeant(String strSergeant) {
        this.strSergeant = strSergeant;
    }
    public void setProDate(String proDate) {
        this.proDate = proDate;
    }
}