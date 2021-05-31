package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.calendar.dto.*;
import com.app.miliwili.utils.Validation;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@EnableSwagger2
@RequestMapping("/app")
public class CalendarController {
    private final CalendarService calendarService;
    private final CalendarProvider calendarProvider;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 일정조회 API
     * [GET] /app/calendars/plans/:planId
     *
     * @return BaseResponse<GetPlanRes>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long planId
     * @Auther shine
     */
    @ApiOperation(value = "일정 id로 상세조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/calendars/plans/{planId}")
    public BaseResponse<GetPlanRes> getPlan(@RequestHeader("X-ACCESS-TOKEN") String token,
                                            @PathVariable Long planId) {
        try {
            GetPlanRes plan = calendarProvider.getPlan(planId);
            return new BaseResponse<>(SUCCESS, plan);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정생성 API
     * [POST] /app/calendars/plans
     *
     * @return BaseResponse<PlanRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostPlanReq parameters
     * @Auther shine
     */
    @ApiOperation(value = "일정생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/plans")
    public BaseResponse<PlanRes> postPlan(@RequestHeader("X-ACCESS-TOKEN") String token,
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
            return new BaseResponse<>(EMPTY_PLAN_START_DATE);
        }
        if (!Validation.isRegexDate(parameters.getStartDate())) {
            return new BaseResponse<>(INVALID_PLAN_START_DATE);
        }
        if (Objects.isNull(parameters.getEndDate()) || parameters.getEndDate().length() == 0) {
            return new BaseResponse<>(EMPTY_PLAN_END_DATE);
        }
        if (!Validation.isRegexDate(parameters.getEndDate())) {
            return new BaseResponse<>(INVALID_PLAN_END_DATE);
        }

        LocalDate startDate = LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE);
        if (parameters.getPlanType().equals("면회") || parameters.getPlanType().equals("외출")
                || parameters.getPlanType().equals("전투휴무") || parameters.getPlanType().equals("당직")) {
            if (!startDate.isEqual(endDate)) {
                return new BaseResponse<>(ONLY_ON_THE_SAME_DAY);
            }
        } else if (parameters.getPlanType().equals("일정") || parameters.getPlanType().equals("휴가")
                || parameters.getPlanType().equals("외박") || parameters.getPlanType().equals("훈련")) {
            if (startDate.isAfter(endDate)) {
                return new BaseResponse<>(FASTER_THAN_PLAN_START_DATE);
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
            PlanRes plan = calendarService.createPlan(parameters);
            return new BaseResponse<>(SUCCESS, plan);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 일정수정 API
     * [PATCH] /app/calendars/plans/:planId
     *
     * @return BaseResponse<PlanRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PatchPlanReq parameters
     * @PathVariable Long planId
     * @Auther shine
     */
    @ApiOperation(value = "일정수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/plans/{planId}")
    private BaseResponse<PlanRes> updatePlan(@RequestHeader("X-ACCESS-TOKEN") String token,
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
            return new BaseResponse<>(EMPTY_PLAN_START_DATE);
        }
        if (!Validation.isRegexDate(parameters.getStartDate())) {
            return new BaseResponse<>(INVALID_PLAN_START_DATE);
        }
        if (Objects.isNull(parameters.getEndDate()) || parameters.getEndDate().length() == 0) {
            return new BaseResponse<>(EMPTY_PLAN_END_DATE);
        }
        if (!Validation.isRegexDate(parameters.getEndDate())) {
            return new BaseResponse<>(INVALID_PLAN_END_DATE);
        }
        if (Objects.nonNull(parameters.getPush()) && parameters.getPush().equals("Y")) {
            if (Objects.isNull(parameters.getPushDeviceToken()) || parameters.getPushDeviceToken().length() == 0) {
                return new BaseResponse<>(EMPTY_PUSH_DEVICE_TOKEN);
            }
        }

        try {
            PlanRes plan = calendarService.updatePlan(parameters, planId);
            return new BaseResponse<>(SUCCESS, plan);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
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
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 일정 다이어리 수정 API
     * [PATCH] /app/calendars/plans/plans-diary/:diaryId
     *
     * @return BaseResponse<DiaryRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody DiaryReq parameters
     * @PathVariable Long diaryId
     * @Auther shine
     */
    @ApiOperation(value = "일정 다이어리 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/plans/plans-diary/{diaryId}")
    public BaseResponse<PlanDiaryRes> updatePlanDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                      @RequestBody(required = false) PlanDiaryReq parameters,
                                                      @PathVariable Long diaryId) {
        if (Objects.isNull(parameters.getContent()) || parameters.getContent().length() == 0) {
            return new BaseResponse<>(EMPTY_CONTENT);
        }

        try {
            PlanDiaryRes diary = calendarService.updatePlanDiary(parameters, diaryId);
            return new BaseResponse<>(SUCCESS, diary);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
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
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }




    /**
     * 디데이 조회 API
     * [GET] /app/calendars/ddays/:ddayId
     *
     * @return BaseResponse<GetDDayRes>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long ddayId
     * @Auther shine
     */
    @ApiOperation(value = "디데이 id로 상세조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/calendars/ddays/{ddayId}")
    public BaseResponse<GetDDayRes> getDDay(@RequestHeader("X-ACCESS-TOKEN") String token,
                                            @PathVariable Long ddayId) {
        try {
            GetDDayRes dday = calendarProvider.getDDay(ddayId);
            return new BaseResponse<>(SUCCESS, dday);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 생성 API
     * [POST] /app/calendars/ddays
     *
     * @return BaseResponse<DDayRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostDDayReq parameters
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/ddays")
    public BaseResponse<DDayRes> postDDay(@RequestHeader("X-ACCESS-TOKEN") String token,
                                          @RequestBody(required = false) PostDDayReq parameters) {
        if (Objects.isNull(parameters.getDdayType()) || parameters.getDdayType().length() == 0) {
            return new BaseResponse<>(EMPTY_D_DAY_TYPE);
        }
        if (Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() >= 10) {
            return new BaseResponse<>(EXCEED_MAX10);
        }
        if (Objects.nonNull(parameters.getSubTitle()) && parameters.getSubTitle().length() >= 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }

        if (parameters.getDdayType().equals("생일")) {
            if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
                return new BaseResponse<>(EMPTY_DATE);
            }
            if (!Validation.isRegexBirthdayDate(parameters.getDate())) {
                return new BaseResponse<>(INVALID_DATE);
            }
            if (Objects.isNull(parameters.getChoiceCalendar()) || parameters.getChoiceCalendar().length() == 0) {
                return new BaseResponse<>(EMPTY_CHOICE_CALENDAR);
            }
            if (!(parameters.getChoiceCalendar().equals("S") || parameters.getChoiceCalendar().equals("L"))) {
                return new BaseResponse<>(MUST_ENTER_CHOICE_CALENDAR_S_OR_B);
            }
            if (Objects.nonNull(parameters.getLink())
                    || Objects.nonNull(parameters.getPlaceLat()) || Objects.nonNull(parameters.getPlaceLon())
                    || Objects.nonNull(parameters.getWork())) {
                return new BaseResponse<>(NOT_ENTER_LINK_PLACE_WORK);
            }
        } else if (parameters.getDdayType().equals("기념일")) {
            if (Objects.nonNull(parameters.getLink())
                    || Objects.nonNull(parameters.getPlaceLat()) || Objects.nonNull(parameters.getPlaceLon())
                    || Objects.nonNull(parameters.getWork())) {
                return new BaseResponse<>(NOT_ENTER_LINK_PLACE_WORK);
            }
            if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
                return new BaseResponse<>(EMPTY_DATE);
            }
            if (!Validation.isRegexDate(parameters.getDate())) {
                return new BaseResponse<>(INVALID_DATE);
            }
            if (Objects.nonNull(parameters.getChoiceCalendar())) {
                return new BaseResponse<>(NOT_ENTER_CHOICE_CALENDAR);
            }
        } else if (parameters.getDdayType().equals("자격증") || parameters.getDdayType().equals("수능")) {
            if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
                return new BaseResponse<>(EMPTY_DATE);
            }
            if (!Validation.isRegexDate(parameters.getDate())) {
                return new BaseResponse<>(INVALID_DATE);
            }
            if (Objects.nonNull(parameters.getChoiceCalendar())) {
                return new BaseResponse<>(NOT_ENTER_CHOICE_CALENDAR);
            }
        } else {
            return new BaseResponse<>(INVALID_D_DAY_TYPE);
        }

        try {
            DDayRes dday = calendarService.createDDay(parameters);
            return new BaseResponse<>(SUCCESS, dday);
        } catch (BaseException exception) {
            exception.printStackTrace();
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 수정 API
     * [PATCH] /app/calendars/ddays/:ddayId
     *
     * @return BaseResponse<DDayRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody PatchDDayReq parameters
     * @PathVariable Long ddayId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/ddays/{ddayId}")
    public BaseResponse<DDayRes> updateDDay(@RequestHeader("X-ACCESS-TOKEN") String token,
                                            @RequestBody(required = false) PatchDDayReq parameters,
                                            @PathVariable Long ddayId) {
        if (Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() >= 10) {
            return new BaseResponse<>(EXCEED_MAX10);
        }
        if (Objects.nonNull(parameters.getSubTitle()) && parameters.getSubTitle().length() >= 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }

        try {
            DDayRes dday = calendarService.updateDDay(parameters, ddayId);
            return new BaseResponse<>(SUCCESS, dday);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
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
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * D-Day 다이어리 생성 API
     * [POST] /app/calendars/ddays/:ddayId/ddays-diary
     *
     * @return BaseResponse<PostDiaryRes>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long ddayId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 다이어리 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/ddays/{ddayId}/ddays-diary")
    public BaseResponse<DDayDiaryRes> postDDayDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                    @RequestBody(required = false) PostDDayDiaryReq parameters,
                                                    @PathVariable Long ddayId) {
        if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
            return new BaseResponse<>(EMPTY_DATE);
        }
        if (!Validation.isRegexDate(parameters.getDate())) {
            return new BaseResponse<>(INVALID_DATE);
        }

        try {
            DDayDiaryRes ddayDiary = calendarService.createDDayDiary(parameters, ddayId);
            return new BaseResponse<>(SUCCESS, ddayDiary);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 다이어리 수정 API
     * [PATCH] /app/calendars/ddays/ddays-diary/:diaryId
     *
     * @return BaseResponse<DiaryRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody DiaryReq parameters
     * @PathVariable Long diaryId
     * @Auther shine
     */
    @ApiOperation(value = "D-Day 다이어리 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/ddays/ddays-diary/{diaryId}")
    public BaseResponse<DDayDiaryRes> updateDDayDiary(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                  @RequestBody(required = false) PatchDDayDiaryReq parameters,
                                                  @PathVariable Long diaryId) {
        if (Objects.isNull(parameters.getContent()) || parameters.getContent().length() == 0) {
            return new BaseResponse<>(EMPTY_CONTENT);
        }

        try {
            DDayDiaryRes ddayDiary = calendarService.updateDDayDiary(parameters, diaryId);
            return new BaseResponse<>(SUCCESS, ddayDiary);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 다이어리 삭제 API
     * [DELETE] /calendars/ddays/ddays-diary/:diaryId
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
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
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
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 할당량 완료 -> 미완료, 미완료 -> 완료로 처리
     * [PATCH] /app/calendars/ddays/ddays-targetAmount/:targetAmountId
     *
     * @return
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long goalId
     * @Auther shine
     */
    @ApiOperation(value = "할당량 완료 -> 미완료, 미완료 -> 완료로 처리", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/calendars/ddays/ddays-targetAmount/{targetAmountId}")
    public BaseResponse<TargetAmountRes> updateTargetAmount(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                            @PathVariable Long targetAmountId) {
        try {
            TargetAmountRes targetAmount = calendarService.updateTargetAmount(targetAmountId);
            return new BaseResponse<>(SUCCESS, targetAmount);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse<>(exception.getStatus());
        }
    }
}