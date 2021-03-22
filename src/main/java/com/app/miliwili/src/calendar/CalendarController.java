package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.calendar.dto.PostDDayReq;
import com.app.miliwili.src.calendar.dto.PostDDayRes;
import com.app.miliwili.src.calendar.dto.PostScheduleReq;
import com.app.miliwili.src.calendar.dto.PostScheduleRes;
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
     * [POST] /app/calendars/schedule
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostScheduleReq parameters
     * @return BaseResponse<PostScheduleRes>
     * @Auther shine
     */
    @ApiOperation(value = "일정 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/calendars/schedule")
    public BaseResponse<PostScheduleRes> postSchedule(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                      @RequestBody(required = false) PostScheduleReq parameters) {
        if(Objects.isNull(parameters.getColor()) || parameters.getColor().length() == 0) {
            return new BaseResponse<>(EMPTY_COLOR);
        }
        if(Objects.isNull(parameters.getScheduleType()) || parameters.getScheduleType().length() == 0) {
            return new BaseResponse<>(EMPTY_SCHEDULE_TYPE);
        }
        if(Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if(Objects.isNull(parameters.getStartDate()) || parameters.getStartDate().length() == 0) {
            return new BaseResponse<>(EMPTY_CALENDAR_START_DATE);
        }
        if(!Validation.isRegexDate(parameters.getStartDate())) {
            return new BaseResponse<>(INVALID_CALENDAR_START_DATE);
        }
        if(Objects.isNull(parameters.getEndDate()) || parameters.getEndDate().length() == 0) {
            return new BaseResponse<>(EMPTY_CALENDAR_END_DATE);
        }
        if(!Validation.isRegexDate(parameters.getEndDate())) {
            return new BaseResponse<>(INVALID_CALENDAR_END_DATE);
        }

        LocalDate startDate = LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE);
        if(parameters.getScheduleType().equals("면회") || parameters.getScheduleType().equals("외출")
                || parameters.getScheduleType().equals("전투휴무") || parameters.getScheduleType().equals("당직")) {
            if (startDate.isEqual(endDate)) {
                return new BaseResponse<>(ONLY_ON_THE_SAME_DAY);
            }
        }

        else if(parameters.getScheduleType().equals("일정") ||
                parameters.getScheduleType().equals("휴가") || parameters.getScheduleType().equals("외박")) {
            if(startDate.isBefore(endDate)) {
                return new BaseResponse<>(FASTER_THAN_CALENDAR_START_DATE);
            }
        }

        else {
            return new BaseResponse<>(INVALID_SCHEDULE_TYPE);
        }

        if(parameters.getPush().equals("Y")) {
            if(Objects.isNull(parameters.getPushDeviceToken()) || parameters.getPushDeviceToken().length() == 0) {
                return new BaseResponse<>(EMPTY_PUSH_DEVICE_TOKEN);
            }
        }

        if(parameters.getScheduleType().equals("휴가")) {
            if(Objects.isNull(parameters.getScheduleVacation()) || parameters.getScheduleVacation().isEmpty()) {
                return new BaseResponse<>(EMPTY_SCHEDULE_VACATION);
            }
        }

        try {
            PostScheduleRes schedule = calendarService.createSchedule(parameters);
            return new BaseResponse<>(SUCCESS, schedule);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * D-Day 생성 API
     * [POST]
     * @Token
     * @RequestBody
     * @return
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