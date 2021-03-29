package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
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
    @GetMapping("/main")
    public BaseResponse<GetUserCalendarMainRes> getUserMain(@RequestHeader("X-ACCESS-TOKEN") String token){
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
//    @ApiOperation(value = "메인 화면", notes = "X-ACCESS-TOKEN jwt 필요")
//    @ResponseBody
//    @GetMapping("/main/calendar")
//    public BaseResponse<Void> getUserMain(@RequestHeader("X-ACCESS-TOKEN") String token){
//        try{
//            GetUserCalendarMainRes userMain = mainProvider.getUserCalendarMainById();
//            return new BaseResponse(SUCCESS,userMain);
//        } catch (BaseException exception) {
//            logger.warn(exception.getStatus().toString());
//            logger.warn(Validation.getPrintStackTrace(exception));
//            return new BaseResponse(exception.getStatus());
//        }
//    }
}