package com.app.miliwili.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    // 1000 : 요청 성공
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    // 2000 : Request 오류
    EMPTY_JWT(false, 2000, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2001, "유효하지 않은 JWT입니다."),

    // 3000 : Response 오류

    // 4000 : Database 오류
    DATABASE_ERROR(false, 4000, "데이터베이스 오류입니다.");

    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
