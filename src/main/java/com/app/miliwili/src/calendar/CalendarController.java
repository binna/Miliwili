package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.calendar.dto.PostDDayReq;
import com.app.miliwili.src.calendar.dto.PostDDayRes;
import com.app.miliwili.src.calendar.dto.PostScheduleReq;
import com.app.miliwili.src.calendar.dto.PostScheduleRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class CalendarController {
    private final CalendarService calendarService;

    /**
     * 일정 생성 API
     * [POST] /app/calendars/schedule
     * @Token X-ACCESS-TOKEN
     * @RequestBody PostScheduleReq parameters
     * @return BaseResponse<PostScheduleRes>
     * @Auther shine
     */
    @ResponseBody
    @PostMapping("/calendars/schedule")
    public BaseResponse<PostScheduleRes> postSchedule(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                      @RequestBody(required = false) PostScheduleReq parameters) {

        // TODO 파라미터 검사

        try {
            PostScheduleRes schedule = calendarService.createSchedule(parameters);
            return new BaseResponse<>(SUCCESS, schedule);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * API
     * [POST]
     * @Token
     * @RequestBody
     * @return
     * @Auther shine
     */
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