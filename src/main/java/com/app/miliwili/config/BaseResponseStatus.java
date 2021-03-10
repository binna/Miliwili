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



    //vivi: 2500~

    INVALID_TOKEN(false, 2500, "유효하지 않은 토큰입니다."),

    INVALID_START_DATE(false, 2510, "입대일 형식을 확인해주세요."),
    INVALID_END_DATE(false, 2511, "전역일 형식을 확인해주세요."),
    INVALID_FIRST_DATE(false, 2512, "일병 진급일 형식을 확인해주세요."),
    INVALID_SECOND_DATE(false, 2513, "상병 진급일 형식을 확인해주세요."),
    INVALID_THIRD_DATE(false, 2514, "병장 진급 형식을 확인해주세요."),
    INVALID_PRO_DATE(false, 2515, "진급 심사일 형식을 확인해주세요."),
    INVALID_STATEIDX(false, 2516, "복무형태는 1~4로 나타내야합니다."),


    EMPTY_START_DATE(false, 2700, "입대일을 입력해주세요."),
    EMPTY_END_DATE(false, 2701, "전역일을 입력해주세요."),
    EMPTY_FIRST_DATE(false, 2702, "일병 진급일을 입력해주세요."),
    EMPTY_SECOND_DATE(false, 2703, "상병 진급일을 입력해주세요."),
    EMPTY_THIRD_DATE(false, 2704, "병장 진급일을 입력해주세요."),
    EMPTY_PRO_DATE(false, 2705, "진급 심사일을 입력해주세요."),
    EMPTY_NAME(false, 2706, "이름을 입력해주세요."),
    EMPTY_STATEIDX(false, 2707, "복무형태를 입력해주세요.(일반병사, 부사관, 준사관, 장교"),
    EMPTY_SERVE_TYPE(false, 2708, "하위 복무형태를 입력해주세요(육군, 해군 ..등)"),






    // 3000 : Response 오류


    //vivi: 3500 ~
    FAILED_TO_GET_USER(false, 3500, "회원 정보 조회에 실패하였습니다."),
    FAILED_TO_SIGNUP_USER(false, 3501, "회원 가입에 실패하였습니다."),
    FAILED_TO_SIGNUP_USER_NORMAL_STATE(false, 3502, "회원 가입(일반병사 table)에 실패하였습니다."),
    FAILED_TO_SIGNUP_USER_ABNORMAL_STATE(false, 3503, "회원 가입(일반병사 외 table)에 실패하였습니다."),

    FAILED_TO_GET_USER_STATE_IDX(false, 3510, "회원 복무 형태 조회에 실패하였습니다."),
    FAILED_TO_GET_ABNORMAL_END(false, 3511, "부사관, 준사관, 장교 전역일 조회에 실패하였습니다."),

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
