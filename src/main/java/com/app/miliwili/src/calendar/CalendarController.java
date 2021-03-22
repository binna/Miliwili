package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.calendar.dto.*;
import com.app.miliwili.utils.Validation;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class CalendarController {
    private final CalendarService calendarService;

    /**
     * 일정생성 API
     * [POST] /app/calendars/plans
     *
     * @return BaseResponse<PostPlanRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostPlanReq parameters
     * @Auther shine
     */
    @ApiOperation(value = "일정 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/plans")
    public BaseResponse<PostPlanRes> postPlan(@RequestHeader("X-ACCESS-TOKEN") String token,
                                              @RequestBody(required = false) PostPlanReq parameters) {
        if (Objects.isNull(parameters.getColor()) || parameters.getColor().length() == 0) {
            return new BaseResponse<>(EMPTY_COLOR);
        }
        if (parameters.getColor().length() < 10) {
            return new BaseResponse<>(EXCEED_MAX10);
        }
        if (Objects.isNull(parameters.getPlanType()) || parameters.getPlanType().length() == 0) {
            return new BaseResponse<>(EMPTY_PLAN_TYPE);
        }
        if (Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() < 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }
        if (Objects.isNull(parameters.getStartDate()) || parameters.getStartDate().length() == 0) {
            return new BaseResponse<>(EMPTY_CALENDAR_START_DATE);
        }
        if (!Validation.isRegexDate(parameters.getStartDate())) {
            return new BaseResponse<>(INVALID_CALENDAR_START_DATE);
        }
        if (Objects.isNull(parameters.getEndDate()) || parameters.getEndDate().length() == 0) {
            return new BaseResponse<>(EMPTY_CALENDAR_END_DATE);
        }
        if (!Validation.isRegexDate(parameters.getEndDate())) {
            return new BaseResponse<>(INVALID_CALENDAR_END_DATE);
        }

        LocalDate startDate = LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE);
        if (parameters.getPlanType().equals("면회") || parameters.getPlanType().equals("외출")
                || parameters.getPlanType().equals("전투휴무") || parameters.getPlanType().equals("당직")) {
            if (startDate.isEqual(endDate)) {
                return new BaseResponse<>(ONLY_ON_THE_SAME_DAY);
            }
        } else if (parameters.getPlanType().equals("일정") ||
                parameters.getPlanType().equals("휴가") || parameters.getPlanType().equals("외박")) {
            if (startDate.isBefore(endDate)) {
                return new BaseResponse<>(FASTER_THAN_CALENDAR_START_DATE);
            }
        } else {
            return new BaseResponse<>(INVALID_PLAN_TYPE);
        }

        if (Objects.nonNull(parameters.getPush()) && parameters.getPush().equals("Y")) {
            if (Objects.isNull(parameters.getPushDeviceToken()) || parameters.getPushDeviceToken().length() == 0) {
                return new BaseResponse<>(EMPTY_PUSH_DEVICE_TOKEN);
            }
        }

        if (parameters.getPlanType().equals("휴가")) {
            if (Objects.isNull(parameters.getPlanVacation()) || parameters.getPlanVacation().isEmpty()) {
                return new BaseResponse<>(EMPTY_PLAN_VACATION);
            }
        }

        try {
            PostPlanRes schedule = calendarService.createPlan(parameters);
            return new BaseResponse<>(SUCCESS, schedule);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정 다이어리 생성 API
     * [POST] /app/calendars/plans/:planId/diaries
     *
     * @return BaseResponse<PostDiaryRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostDiaryReq parameters
     * @Auther shine
     */
    @ApiOperation(value = "일정 다이어리 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/plans/{planId}/diaries")
    public BaseResponse<PostDiaryRes> postDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                @RequestBody(required = false) PostDiaryReq parameters,
                                                @PathVariable Long planId) {
        if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
            return new BaseResponse<>(EMPTY_DATE);
        }
        if (!Validation.isRegexDate(parameters.getDate())) {
            return new BaseResponse<>(INVALID_DATE);
        }
        if (Objects.isNull(parameters.getContent()) || parameters.getContent().length() == 0) {
            return new BaseResponse<>(EMPTY_CONTENT);
        }

        try {
            PostDiaryRes diary = calendarService.createDiary(parameters, planId);
            return new BaseResponse<>(SUCCESS, diary);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * D-Day 생성 API
     * [POST]
     *
     * @return
     * @Token
     * @RequestBody
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/dday")
    public BaseResponse<PostDDayRes> postDDay(@RequestHeader("X-ACCESS-TOKEN") String token,
                                              @RequestBody(required = false) PostDDayReq parameters) {

        // TODO 파라미터 검사

        try {
            PostDDayRes dDay = calendarService.createDDay(parameters);
            return new BaseResponse<>(SUCCESS, dDay);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}