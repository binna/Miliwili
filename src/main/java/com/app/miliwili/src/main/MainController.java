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
     * [GET] /app/main/users-calendars
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
     * 당월, 금일 캘린더 조회(캘린더 클릭했을 때 처음 세팅되는 값)
     * [GET] /app/main/calendars/current-month-today
     *
     * @return BaseResponse<GetCalendarMainRes>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther Shine
     */
    @ApiOperation(value = "당월, 금일 캘린더 조회(캘린더 클릭했을 때 처음 세팅되는 값)", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/calendars/current-month-today")
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
     * 월별 캘린더 조회
     * [GET] /app/main/calendars/month?month=
     *
     * @return GetMonthCalendarMainRes
     * @RequestParam String month
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther
     */
    @ApiOperation(value = "월별 캘린더 조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/calendars/month")
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
     * 날짜별 메인 캘린더 조회
     * [GET] /app/main/calendars/day?month=
     *
     * @return GetDateCalendarMainRes
     * @RequestParam String date
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther Shine
     */
    @ApiOperation(value = "날짜별 메인 캘린더 조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/calendars/day")
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