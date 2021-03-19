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
    EMPTY_TITLE(false, 2009, "제목을 입력해주세요."),
    EMPTY_TOTAL(false, 2010, "총 휴가개수를 입력해주세요."),
    EMPTY_SOCIAL_TYPE(false, 2010, "가입 요청하는 소셜타입을 입력해주세요."),
    EMPTY_PRIMARY(false, 2000, "PK를 입력해주세요."),
    INVALID_ORDINARY_LEAVE_START_DATE(false, 2011, "휴가시작일 형식을 확인해주세요."),
    INVALID_ORDINARY_LEAVE_END_DATE(false, 2012, "휴가종료일 형식을 확인해주세요."),
    CHOOSE_BETWEEN_ABNORMAL_OR_NORMAL(false, 2002, "진급일이나 일병과 상병과 병장진급일 중 하나는 꼭 입력해주셔야 합니다."),
    FASTER_THAN_START_DATE(false, 2003, "전역일은 입대일보다 빠를 수 없습니다."),
    FASTER_THAN_FIRST_DATE(false, 2004, "입대일은 상병진급일보다 빠를 수 없습니다."),
    FASTER_THAN_SECOND_DATE(false, 2005, "상병진급일은 병장진급일보다  빠를 수 없습니다."),
    FASTER_THAN_THIRD_DATE(false, 2006, "병장진급일은 상병진급일보다 빠를 수 없습니다."),
    FASTER_THAN_END_DATE(false, 2007, "상병진급일은 전역일보다 빠를 수 없습니다."),
    FASTER_THAN_PRO_DATE(false, 2008, "진급심사일은 입대일보다 빠를 수 없습니다."),
    FASTER_THAN_ORDINARY_LEAVE_START_DATE(false, 2003, "휴가종료일은 휴가시작일보다 빠를 수 없습니다."),







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
    KAKAO_CONNECTION(false, 3000, "카카오 연결에 실패하였습니다."),
    KAKAO_CONNECTION_1(false, 3001, "카카오 Response 에러 : 플랫폼 서비스의 일시적 내부 장애가 발생했습니다."),
    KAKAO_CONNECTION_2(false, 3002, "카카오 Response 에러 : 액세스 토큰 정보가 올바른 형식으로 요청했는지 확인해주세요."),
    KAKAO_CONNECTION_401(false, 3003, "카카오 Response 에러 : 유효하지 않은 앱키나 액세스 토큰으로 요청하였습니다."),
    KAKAO_CONNECTION_ETC(false, 3004, "카카오 Response 에러가 발생하였습니다."),
    NOT_FOUND_USER(false, 3005, "존재하지 않는 회원입니다."),
    DUPLICATED_USER(false, 3006, "이미 존재하는 회원입니다."),
    DO_NOT_AUTH_USER(false, 3120, "권한이 없는 사용자입니다."),
    FAILED_TO_PATCH_USER(false, 3007, "회원정보 변경에 실패하였습니다."),
    FAILED_TO_DELETE_USER(false, 3007, "회원 탈퇴에 실패하였습니다."),
    FAILED_TO_SET_DAILY_HOBONG_STATUSIDX(false, 3007, "스케줄러, 호봉과 상위계급 업데이트에 실패하였습니다."),
    NOT_FOUND_LEAVE(false, 3005, "존재하지 않는 휴가입니다."),
    FAILED_TO_POST_LEAVE(false, 3007, "휴가 등록에 실패하였습니다."),
    FAILED_TO_PATCH_LEAVE(false, 3007, "휴가 변경에 실패하였습니다."),
    FAILED_TO_DELETE_LEAVE(false, 3007, "휴가 삭제에 실패하였습니다."),
    FAILED_TO_GET_LEAVE(false, 3007, "휴가 조회에 실패하였습니다."),
    FAILED_TO_POST_SCHEDULE(false, 3007, "일정 등록에 실패하였습니다."),
    FAILED_TO_GET_SCHEDULE(false, 3007, "일정 조회에 실패하였습니다."),
    FAILED_TO_POST_D_DAY(false, 3007, "D-Day 등록에 실패하였습니다."),







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
