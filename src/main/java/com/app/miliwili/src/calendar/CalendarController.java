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
    @ApiOperation(value = "일정생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/plans")
    public BaseResponse<PostPlanRes> postPlan(@RequestHeader("X-ACCESS-TOKEN") String token,
                                              @RequestBody(required = false) PostPlanReq parameters) {
        if (Objects.isNull(parameters.getColor()) || parameters.getColor().length() == 0) {
            return new BaseResponse<>(EMPTY_COLOR);
        }
        if (parameters.getColor().length() >= 10) {
            return new BaseResponse<>(EXCEED_MAX10);
        }
        if (Objects.isNull(parameters.getPlanType()) || parameters.getPlanType().length() == 0) {
            return new BaseResponse<>(EMPTY_PLAN_TYPE);
        }
        if (Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() >= 20) {
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
        }
        else if (parameters.getPlanType().equals("일정") ||
                parameters.getPlanType().equals("휴가") || parameters.getPlanType().equals("외박")) {
            if (startDate.isBefore(endDate)) {
                return new BaseResponse<>(FASTER_THAN_CALENDAR_START_DATE);
            }
        }
        else {
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
            PostPlanRes plan = calendarService.createPlan(parameters);
            return new BaseResponse<>(SUCCESS, plan);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정수정 API
     * [PATCH] /app/calendars/plans/:planId
     *
     * @return BaseResponse<PatchPlanRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PatchPlanReq parameters
     * @PathVariable Long planId
     * @Auther shine
     */
    @ApiOperation(value = "일정수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/plans/{planId}")
    private BaseResponse<PatchPlanRes> updatePlan(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                  @RequestBody(required = false) PatchPlanReq parameters,
                                                  @PathVariable Long planId) {
        if (Objects.isNull(parameters.getColor()) || parameters.getColor().length() == 0) {
            return new BaseResponse<>(EMPTY_COLOR);
        }
        if (parameters.getColor().length() >= 10) {
            return new BaseResponse<>(EXCEED_MAX10);
        }
        if (Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() >= 20) {
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
        if (Objects.nonNull(parameters.getPush()) && parameters.getPush().equals("Y")) {
            if (Objects.isNull(parameters.getPushDeviceToken()) || parameters.getPushDeviceToken().length() == 0) {
                return new BaseResponse<>(EMPTY_PUSH_DEVICE_TOKEN);
            }
        }

        try {
            PatchPlanRes plan = calendarService.updatePlan(parameters, planId);
            return new BaseResponse<>(SUCCESS, plan);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정삭제 API
     * [DELETE] /app/calendars/plans/:planId
     *
     * @return BaseResponse<Void>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long planId
     * @Auther shine
     */
    @ApiOperation(value = "일정삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @DeleteMapping("/calendars/plans/{planId}")
    private BaseResponse<Void> deletePlan(@RequestHeader("X-ACCESS-TOKEN") String token,
                                          @PathVariable Long planId) {
        try {
            calendarService.deletePlan(planId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정 다이어리 생성 API
     * [POST] /app/calendars/plans/:planId/plans-diary
     *
     * @return BaseResponse<PostDiaryRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostDiaryReq parameters
     * @PathVariable Long planId
     * @Auther shine
     */
    @ApiOperation(value = "일정 다이어리 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/plans/{planId}/plans-diary")
    public BaseResponse<PostDiaryRes> postPlanDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
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
            PostDiaryRes diary = calendarService.createPlanDiary(parameters, planId);
            return new BaseResponse<>(SUCCESS, diary);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정 다이어리 수정 API
     * [PATCH] /app/calendars/plans/plans-diary/:diaryId
     *
     * @return BaseResponse<PatchDiaryRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PatchDiaryReq parameters
     * @PathVariable Long diaryId
     * @Auther shine
     */
    @ApiOperation(value = "일정 다이어리 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/plans/plans-diary/{diaryId}")
    public BaseResponse<PatchDiaryRes> updatePlanDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                       @RequestBody(required = false) PatchDiaryReq parameters,
                                                       @PathVariable Long diaryId) {
        if (Objects.isNull(parameters.getContent()) || parameters.getContent().length() == 0) {
            return new BaseResponse<>(EMPTY_CONTENT);
        }

        try {
            PatchDiaryRes diary = calendarService.updatePlanDiary(parameters, diaryId);
            return new BaseResponse<>(SUCCESS, diary);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정 다이어리 삭제 API
     * [DELETE] /app/calendars/plans/plans-diary/:diaryId
     *
     * @return BaseResponse<Void>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long diaryId
     * @Auther shine
     */
    @ApiOperation(value = "일정 다이어리 삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @DeleteMapping("/calendars/plans/plans-diary/{diaryId}")
    public BaseResponse<Void> deletePlanDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                              @PathVariable Long diaryId) {
        try {
            calendarService.deletePlanDiary(diaryId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 할일 완료 -> 미완료, 미완료 -> 완료 처리 API
     * [PATCH] /app/calendars/plans/plans-work/:workId
     *
     * @return BaseResponse<WorkRes>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long workId
     * @Auther shine
     */
    @ApiOperation(value = "할일 완료 -> 미완료, 미완료 -> 완료 처리", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/plans/plans-work/{workId}")
    public BaseResponse<WorkRes> updatePlanWork(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                @PathVariable Long workId) {
        try {
            WorkRes work = calendarService.updatePlanWork(workId);
            return new BaseResponse<>(SUCCESS, work);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * D-Day 생성 API
     * [POST] /app/calendars/ddays
     *
     * @return BaseResponse<PostDDayRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostDDayReq parameters
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/ddays")
    public BaseResponse<PostDDayRes> postDDay(@RequestHeader("X-ACCESS-TOKEN") String token,
                                              @RequestBody(required = false) PostDDayReq parameters) {
        if(Objects.isNull(parameters.getDdayType()) || parameters.getDdayType().length() == 0) {
            return new BaseResponse<>(EMPTY_D_DAY_TYPE);
        }
        if(!(parameters.getDdayType().equals("기념일") || parameters.getDdayType().equals("생일")
                || parameters.getDdayType().equals("자격증") || parameters.getDdayType().equals("수능"))) {
            return new BaseResponse<>(INVALID_D_DAY_TYPE);
        }

        if(Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() >= 10) {
            return new BaseResponse<>(EXCEED_MAX10);
        }
        if (parameters.getSubTitle().length() >= 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }
        if(Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
            return new BaseResponse<>(EMPTY_DATE);
        }
        if(!Validation.isRegexDate(parameters.getDate())) {
            return new BaseResponse<>(INVALID_DATE);
        }

        try {
            PostDDayRes dday = calendarService.createDDay(parameters);
            return new BaseResponse<>(SUCCESS, dday);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 수정 API
     * [PATCH] /app/calendars/ddays/:ddayId
     *
     * @return BaseResponse<PatchDDayRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PatchDDayReq parameters
     * @PathVariable Long ddayId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/ddays/{ddayId}")
    public BaseResponse<PatchDDayRes> updateDDay(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                 @RequestBody(required = false) PatchDDayReq parameters,
                                                 @PathVariable Long ddayId) {
        if(Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() >= 10) {
            return new BaseResponse<>(EXCEED_MAX10);
        }
        if (parameters.getSubTitle().length() >= 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }
        if(Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
            return new BaseResponse<>(EMPTY_DATE);
        }
        if(!Validation.isRegexDate(parameters.getDate())) {
            return new BaseResponse<>(INVALID_DATE);
        }

        try {
            PatchDDayRes dday = calendarService.updateDDay(parameters, ddayId);
            return new BaseResponse<>(SUCCESS, dday);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 삭제 API
     * [DELETE] /app/calendars/ddays/:ddayId
     *
     * @return BaseResponse<Void>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long ddayId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @DeleteMapping("/calendars/ddays/{ddayId}")
    public BaseResponse<Void> deleteDDay(@RequestHeader("X-ACCESS-TOKEN") String token,
                                         @PathVariable Long ddayId) {
        try {
            calendarService.deleteDDay(ddayId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * D-Day 다이어리 생성 API
     * [POST] /app/calendars/ddays/:ddayId/ddays-diary
     *
     * @return BaseResponse<PostDiaryRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostDiaryReq parameters
     * @PathVariable Long ddayId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 다이어리 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/ddays/{ddayId}/ddays-diary")
    public BaseResponse<PostDiaryRes> postDDayDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                    @RequestBody(required = false) PostDiaryReq parameters,
                                                    @PathVariable Long ddayId) {
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
            PostDiaryRes diary = calendarService.createDDayDiary(parameters, ddayId);
            return new BaseResponse<>(SUCCESS, diary);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 다이어리 수정 API
     * [PATCH] /app/calendars/ddays/ddays-diary/:diaryId
     *
     * @return BaseResponse<PatchDiaryRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PatchDiaryReq parameters
     * @PathVariable Long diaryId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 다이어리 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/ddays/ddays-diary/{diaryId}")
    public BaseResponse<PatchDiaryRes> updateDDayDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                       @RequestBody(required = false) PatchDiaryReq parameters,
                                                       @PathVariable Long diaryId) {
        if (Objects.isNull(parameters.getContent()) || parameters.getContent().length() == 0) {
            return new BaseResponse<>(EMPTY_CONTENT);
        }

        try {
            PatchDiaryRes diary = calendarService.updateDDayDiary(parameters, diaryId);
            return new BaseResponse<>(SUCCESS, diary);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 다이어리 삭제 API
     * [DELETE] /app/calendars/ddays/ddays-diary/:diaryId
     *
     * @return BaseResponse<Void>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long diaryId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 다이어리 삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @DeleteMapping("/calendars/ddays/ddays-diary/{diaryId}")
    public BaseResponse<Void> deleteDDayDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                              @PathVariable Long diaryId) {
        try {
            calendarService.deleteDDayDiary(diaryId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 준비물 준비 완료 -> 미완료, 미완료 -> 완료로 처리 API
     * [PATCH] /app/calendars/ddays/ddays-work/:workId
     *
     * @return BaseResponse<WorkRes>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long workId
     * @Auther shine
     */
    @ApiOperation(value = "준비물 완료 -> 미완료, 미완료 -> 완료 처리", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/ddays/ddays-work/{workId}")
    public BaseResponse<WorkRes> updateDDayWork(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                @PathVariable Long workId) {
        try {
            WorkRes todolist = calendarService.updateDDayWork(workId);
            return new BaseResponse<>(SUCCESS, todolist);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}