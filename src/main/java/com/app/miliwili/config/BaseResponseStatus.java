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
    EMPTY_TITLE(false, 2002, "제목을 입력해주세요."),
    EMPTY_TOTAL_DAYS(false, 2003, "총 휴가개수를 입력해주세요."),
    EMPTY_SOCIAL_TYPE(false, 2004, "가입 요청하는 소셜타입을 입력해주세요."),
    EMPTY_PRIMARY(false, 2005, "PK를 입력해주세요."),
    EMPTY_VACATION_TYPE(false, 2006, "휴가구분을 입력해주세요."),
    EMPTY_BIRTHDAY(false, 2007, "생일을 입력해주세요."),
    EMPTY_GOAL(false, 2008, "목표를 입력해주세요."),
    EMPTY_ALL(false, 2009, "입력된 값이 없습니다. 값을 입력해주세요."),
    EMPTY_COLOR(false, 2010, "색상을 입력해주세요."),
    EMPTY_PLAN_TYPE(false, 2011, "일정 타입을 입력해주세요."),
    EMPTY_CALENDAR_START_DATE(false, 2012, "시작날짜을 입력해주세요."),
    EMPTY_CALENDAR_END_DATE(false, 2013, "종료날짜을 입력해주세요."),
    EMPTY_PUSH_DEVICE_TOKEN(false, 2014, "PUSH 디바이스 토큰을 입력해주세요."),
    INVALID_ORDINARY_LEAVE_START_DATE(false, 2015, "휴가시작일 형식을 확인해주세요."),
    INVALID_ORDINARY_LEAVE_END_DATE(false, 2016, "휴가종료일 형식을 확인해주세요."),
    INVALID_BIRTHDAY(false, 2017, "생일 형식을 확인해주세요."),
    INVALID_CALENDAR_START_DATE(false, 2018, "시작날짜 형식을 확인해주세요."),
    INVALID_CALENDAR_END_DATE(false, 2019, "종료날짜 형식을 확인해주세요."),
    EXCEED_MAX20(false, 2020, "입력할 수 있는 최대 20글자의 수를 초과하였습니다."),
    NOT_BE_GREATER_THAN_TOTAL_DAYS(false, 2021, "사용일은 총 휴가일수를 초과할 수 없다."),
    CHOOSE_BETWEEN_ABNORMAL_OR_NORMAL(false, 2022, "진급일이나 일병과 상병과 병장진급일 중 하나는 꼭 입력해주셔야 합니다."),
    FASTER_THAN_START_DATE(false, 2023, "전역일은 입대일보다 빠를 수 없습니다."),
    FASTER_THAN_FIRST_DATE(false, 2024, "입대일은 상병진급일보다 빠를 수 없습니다."),
    FASTER_THAN_SECOND_DATE(false, 2025, "상병진급일은 병장진급일보다  빠를 수 없습니다."),
    FASTER_THAN_THIRD_DATE(false, 2026, "병장진급일은 상병진급일보다 빠를 수 없습니다."),
    FASTER_THAN_END_DATE(false, 2027, "상병진급일은 전역일보다 빠를 수 없습니다."),
    FASTER_THAN_PRO_DATE(false, 2028, "진급심사일은 입대일보다 빠를 수 없습니다."),
    FASTER_THAN_ORDINARY_LEAVE_START_DATE(false, 2029, "휴가종료일은 휴가시작일보다 빠를 수 없습니다."),
    ONLY_ON_THE_SAME_DAY(false, 2030, "날짜 범위 등록 불가, 당일만 등록 가능합니다."),
    FASTER_THAN_CALENDAR_START_DATE(false, 2031, "종료일은 시작일보다 빠를 수 없습니다."),
    INVALID_PLAN_TYPE(false, 2032, "유효하지 않은 일정 타입입니다."),
    EMPTY_PLAN_VACATION(false, 2033, "휴가 정보를 입력해주세요."),
    EXCEED_MAX10(false, 2034, "입력할 수 있는 최대 10글자의 수를 초과하였습니다."),
    EMPTY_DATE(false, 2035, "날짜을 입력해주세요."),
    EMPTY_CONTENT(false, 2036, "내용을 입력해주세요."),
    INVALID_DATE(false, 2037, "날짜 형식을 확인해주세요."),






    //vivi: 2500~

    INVALID_TOKEN(false, 2500, "유효하지 않은 토큰입니다."),
    INVALID_USER(false, 2501, "권한이 없는 사용자입니다."),

    INVALID_START_DATE(false, 2510, "입대일 형식을 확인해주세요."),
    INVALID_END_DATE(false, 2511, "전역일 형식을 확인해주세요."),
    INVALID_FIRST_DATE(false, 2512, "일병 진급일 형식을 확인해주세요."),
    INVALID_SECOND_DATE(false, 2513, "상병 진급일 형식을 확인해주세요."),
    INVALID_THIRD_DATE(false, 2514, "병장 진급 형식을 확인해주세요."),
    INVALID_PRO_DATE(false, 2515, "진급 심사일 형식을 확인해주세요."),
    INVALID_STATEIDX(false, 2516, "복무형태는 1~4로 나타내야합니다."),
    INVALID_VIEW_DATE(false, 2517, "조회하고자 하는 날짜는 이번달 이전만 가능합니다"),
    INVALID_MODIFY_DATE(false, 2518, "수정하고자 하는 날짜 형식을 확인해주세요."),

    INVALID_WEIGHT(false, 2520, "몸무게는 Double형으로 설정해주세요."),
    INVALID_SETCOUNT(false, 2521, "운동정보에 입력하신 세트의 개수와 실제 세트들의 개수가 일치하지 않습니다."),

    EMPTY_START_DATE(false, 2700, "입대일을 입력해주세요."),
    EMPTY_END_DATE(false, 2701, "전역일을 입력해주세요."),
    EMPTY_FIRST_DATE(false, 2702, "일병 진급일을 입력해주세요."),
    EMPTY_SECOND_DATE(false, 2703, "상병 진급일을 입력해주세요."),
    EMPTY_THIRD_DATE(false, 2704, "병장 진급일을 입력해주세요."),
    EMPTY_PRO_DATE(false, 2705, "진급 심사일을 입력해주세요."),
    EMPTY_NAME(false, 2706, "이름을 입력해주세요."),
    EMPTY_STATEIDX(false, 2707, "복무형태를 입력해주세요.(일반병사, 부사관, 준사관, 장교"),
    EMPTY_SERVE_TYPE(false, 2708, "하위 복무형태를 입력해주세요(육군, 해군 ..등)"),

    EMPTY_WEIGHT(false, 2800, "몸무게를 입력해주세요"),

    EMPTY_TOTALTIME(false, 2810, "총 운동시간을 입력해주세요"),
    EMPTY_EXERCISESTATUS(false, 2811, "운동 완료한 세트 개수 정보를 입력해주세요."),
    EMPTY_ROUTINE_ID(false, 2812, "루틴 id를 입력해주세요!!!!!!!!!!!!!!!!!!!"),

    FULL_REPORT_TEXT(false, 2813, "글은 최대 300자입니다."),

    INVALIED_VIEW_MONTH(false, 2820, "viewMonth는 1월부터 12월까지 입력해야합니다."),



    // 3000 : Response 오류
    KAKAO_CONNECTION(false, 3000, "카카오 연결에 실패하였습니다."),
    KAKAO_CONNECTION_1(false, 3001, "카카오 Response 에러 : 플랫폼 서비스의 일시적 내부 장애가 발생했습니다."),
    KAKAO_CONNECTION_2(false, 3002, "카카오 Response 에러 : 액세스 토큰 정보가 올바른 형식으로 요청했는지 확인해주세요."),
    KAKAO_CONNECTION_401(false, 3003, "카카오 Response 에러 : 유효하지 않은 앱키나 액세스 토큰으로 요청하였습니다."),
    KAKAO_CONNECTION_ETC(false, 3004, "카카오 Response 에러가 발생하였습니다."),
    NOT_FOUND_USER(false, 3005, "존재하지 않는 회원입니다."),
    DUPLICATED_USER(false, 3006, "이미 존재하는 회원입니다."),
    DO_NOT_AUTH_USER(false, 3007, "권한이 없는 사용자입니다."),
    FAILED_TO_PATCH_USER(false, 3008, "회원정보 변경에 실패하였습니다."),
    FAILED_TO_DELETE_USER(false, 3009, "회원 탈퇴에 실패하였습니다."),
    FAILED_TO_SET_DAILY_HOBONG_STATUSIDX(false, 3010, "스케줄러, 호봉과 상위계급 업데이트에 실패하였습니다."),
    NOT_FOUND_VACATION(false, 3011, "존재하지 않는 휴가입니다."),
    FAILED_TO_POST_VACATION(false, 3012, "휴가 등록에 실패하였습니다."),
    FAILED_TO_PATCH_VACATION(false, 3013, "휴가 변경에 실패하였습니다."),
    FAILED_TO_DELETE_VACATION(false, 2014, "휴가 삭제에 실패하였습니다."),
    FAILED_TO_GET_VACATION(false, 3015, "휴가 조회에 실패하였습니다."),
    FAILED_TO_PUSH_MESSAGE(false, 3016, "스케줄러, PUSH 메시지 발송에 실패하였습니다."),
    NOT_FOUND_PLAN(false, 3017, "존재하지 않는 일정입니다."),
    FAILED_TO_POST_PLAN(false, 3018, "일정 등록에 실패하였습니다."),
    FAILED_TO_PATCH_PLAN(false, 3019, "일정 수정에 실패하였습니다."),
    FAILED_TO_DELETE_PLAN(false, 3020, "일정 삭제에 실패하였습니다."),
    FAILED_TO_GET_PLAN(false, 3021, "일정 조회에 실패하였습니다."),
    OUT_OF_BOUNDS_DATE_DIARY(false, 3022, "일정 다이어리를 작성할 수 있는 범위를 벗어났습니다."),
    ALREADY_EXIST_DIARY(false, 3023, "이미 해당 날짜에는 일정 다이어리가 존재합니다."),
    NOT_FOUND_DIARY(false, 3024, "존재하지 않는 일정 다이어리입니다."),
    FAILED_TO_POST_DIARY(false, 3025, "일정 다이어리 생성에 실패하였습니다."),
    FAILED_TO_PATCH_DIARY(false, 3026, "일정 다이어리 수정에 실패하였습니다."),
    FAILED_TO_DELETE_DIARY(false, 3027, "일정 다이어리 삭제에 실패하였습니다."),
    FAILED_TO_GET_DIARY(false, 3028, "일정 다이어리 조회에 실패하였습니다."),
    NOT_FOUND_D_DAY(false, 3029, "존재하지 않는 D-Day 입니다."),
    FAILED_TO_POST_D_DAY(false, 3030, "D-Day 등록에 실패하였습니다."),
    FAILED_TO_PATCH_D_DAY(false, 3031, "D-Day 수정에 실패하였습니다."),
    FAILED_TO_DELETE_D_DAY(false, 3032, "D-Day 삭제에 실패하였습니다."),
    FAILED_TO_GET_D_DAY(false, 3033, "D-Day 조회에 실패하였습니다."),








    //vivi: 3500 ~
    FAILED_TO_GET_USER(false, 3500, "회원 정보 조회에 실패하였습니다."),
    FAILED_TO_SIGNUP_USER(false, 3501, "회원 가입에 실패하였습니다."),
    FAILED_TO_SIGNUP_USER_NORMAL_STATE(false, 3502, "회원 가입(일반병사 table)에 실패하였습니다."),
    FAILED_TO_SIGNUP_USER_ABNORMAL_STATE(false, 3503, "회원 가입(일반병사 외 table)에 실패하였습니다."),

    FAILED_TO_GET_USER_STATE_IDX(false, 3510, "회원 복무 형태 조회에 실패하였습니다."),
    FAILED_TO_GET_ABNORMAL_END(false, 3511, "부사관, 준사관, 장교 전역일 조회에 실패하였습니다."),

    FAILED_POST_FIRST_WIEHGT(false, 3520, "첫 몸무게 등록에 실패하였습니다"),
    FAILED_CHECK_FIRST_WIEHGT(false, 3521, "이미 몸무게가 등록된(운동탭에 방문했던) 사용자입니다 "),

    NOT_FOUND_EXERCISEINFO(false, 3530, "존재하지 않는 운동 정보입니다 "),
    FAILED_POST_DAILY_WEIGHT(false, 3531, "일일 체중 기록에 실패하였습니다. "),
    FAILED_PATCH_GOAL_WEIGHT(false, 3532, "목표체중 수정에 실패하였습니다. "),
    FAILED_GET_DAILY_WEIGHT(false, 3533, "일일 체중 기록 조회에 실패하였습니다."),
    FAILED_GET_MONTH_WEIGHT_1(false, 3534, "월별 체중 기록 조회에 실패하였습니다.-1-월별 평균 조회 "),
    FAILED_GET_MONTH_WEIGHT_2(false, 3535, "월별 체중 기록 조회에 실패하였습니다.-2-웗별 일일 조회 "),
    NOT_FOUND_EXERCISE_WEIGHT_RECORD(false, 3536, "해당날짜에 해당 회원의 체중기록이 존재하지 않습니다."),
    FAILED_PATCH_DAILY_WEIGHT(false, 3537, "일일 체중 기록 수정에 실패하였습니다. "),

    FAILED_FIND_DELETE_ROUTINE(false, 3538, "삭제하고자 하는 루틴의 ID와 일치하는 루틴이 존재하지 않습니다. "),
    FAILED_FIND_GET_ROUTINE(false, 3539, "오늘 운동 완료된 루틴이 없습니다. "),
    FAILED_RESET_ROTUINE_DONE(false, 3540, "운동 상태 리셋에 실패하였습니다. "),

    NOT_FOUND_ROUTINE(false, 3550, "해당 루틴ID와 일치하는 루틴이 없습니다. "),

    FAILED_POST_REPORT(false, 3560, "운동 리포트 생성에 실패하였습니다."),
    FAILED_GET_REPORT(false, 3561, "해당 날짜의 리포트 조회에 실패하였습니다."),
    FAILED_GET_REPORT_DONE(false, 3562, "아직 루틴을 완료하지 않았기 때문에 운동 리포트 조회가 불가능합니다"),
    FAILED_GET_REPORT_SETCOUNT(false, 3563, "완료된 세트의 개수가 운동 세트수보다 큽니다."),
    FAILED_GET_REPORT_SETSIZE(false, 3564, "입력된 운동의 개수와 입력된 운동의 개수가 다릅니다."),


    // 4000 : Database 오류
    DATABASE_ERROR(false, 4000, "데이터베이스 오류입니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}