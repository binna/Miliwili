package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.calendar.models.PostScheduleReq;
import com.app.miliwili.src.calendar.models.PostScheduleRes;
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
    public BaseResponse<PostScheduleRes> postSchedule(@RequestBody(required = false)PostScheduleReq parameters) {

        // TODO 파라미터 검사

        try {
            // 쿼리 실행
            PostScheduleRes schedule = calendarService.createSchedule(parameters);
            return new BaseResponse<>(SUCCESS, schedule);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



}