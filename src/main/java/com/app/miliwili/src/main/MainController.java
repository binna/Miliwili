package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.main.dto.GetCalendarMainRes;
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
     * 메인 캘린더 조회
     * [GET] /app/main/calendar
     *
     * @return
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther
     */
    @ApiOperation(value = "메인 화면", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/main/calendar")
    public BaseResponse<GetCalendarMainRes> getCalendarMain(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                            @RequestParam(value = "month", required = false) String month,
                                                            @RequestParam(value = "date", required = false) String date) {
        // TODO 유효성 검사

        try{
            GetCalendarMainRes calendarMain = mainProvider.getCalendarMain(month, date);
            return new BaseResponse(SUCCESS,calendarMain);
        } catch (BaseException exception) {
            logger.warn(exception.getStatus().toString());
            logger.warn(Validation.getPrintStackTrace(exception));
            return new BaseResponse(exception.getStatus());
        }
    }
}