package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.main.dto.GetCalendarMainRes;
import com.app.miliwili.src.main.dto.GetDateCalendarMainRes;
import com.app.miliwili.src.main.dto.GetMonthCalendarMainRes;
import com.app.miliwili.src.main.dto.GetUserCalendarMainRes;
import com.app.miliwili.utils.Validation;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@EnableSwagger2
@RequestMapping("/app")
public class MainController {
    private final MainProvider mainProvider;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 메인조회
     * [GET] /app/main
     *
     * @return BaseResponse<GetUserCalendarMainRes>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ApiOperation(value = "메인 화면", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/users-calendars")
    public BaseResponse<GetUserCalendarMainRes> getUserCalendarMain(@RequestHeader("X-ACCESS-TOKEN") String token) {
        try{
            GetUserCalendarMainRes userMain = mainProvider.getUserCalendarMainById();
            return new BaseResponse(SUCCESS,userMain);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 월별 메인 캘린더 조회
     * [GET] /app/main/calendar
     *
     * @return
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther
     */
    @ApiOperation(value = "메인 화면", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/calendars")
    public BaseResponse<GetCalendarMainRes> getCalendarMain(@RequestHeader("X-ACCESS-TOKEN") String token) {
        try{
            GetCalendarMainRes calendarMain = mainProvider.getCalendarMain();
            return new BaseResponse(SUCCESS,calendarMain);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 월별 메인 캘린더 조회
     * [GET] /app/main/calendar?month=
     *
     * @return
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther
     */
    @ApiOperation(value = "메인 화면", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/calendars-month")
    public BaseResponse<GetMonthCalendarMainRes> getCalendarMainFromMonth(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                                          @RequestParam(value = "month", required = false) String month) {
        if (!Validation.isRegexMonthParam(month)) {
            return new BaseResponse<>(INVALID_MONTH_PARAM);
        }

        try{
            GetMonthCalendarMainRes calendarMain = mainProvider.getCalendarMainFromMonth(month);
            return new BaseResponse(SUCCESS,calendarMain);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse(exception.getStatus());
        }
    }

    /**
     * 일별 메인 캘린더 조회
     * [GET] /app/main/calendar?month=
     *
     * @return
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther
     */
    @ApiOperation(value = "메인 화면", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/calendars-day")
    public BaseResponse<GetDateCalendarMainRes> getCalendarMainFromDate(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                                        @RequestParam(value = "date", required = false) String date) {
        if (!Validation.isRegexDateParam(date)) {
            return new BaseResponse<>(INVALID_DATE_PARAM);
        }

        try{
            GetDateCalendarMainRes calendarMain = mainProvider.getCalendarMainFromDate(date);
            return new BaseResponse(SUCCESS,calendarMain);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse(exception.getStatus());
        }
    }
}