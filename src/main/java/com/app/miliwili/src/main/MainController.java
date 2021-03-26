package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.config.BaseResponseStatus.*;
import com.app.miliwili.src.main.dto.GetUserCalendarMainRes;
import com.app.miliwili.src.main.dto.UserCalendarMainData;
import com.app.miliwili.src.main.model.GetEndDayRes;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/main")
public class MainController {
    private final MainProvider mainProvider;


    /**
     *
     */
    @ApiOperation(value = "일정 id로 상세조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<UserCalendarMainData> getUserMainInfo(@RequestHeader("X-ACCESS-TOKEN") String token){
        try{
            UserCalendarMainData userMainInfo = mainProvider.retrieveUserMainById();
            return new BaseResponse(SUCCESS,userMainInfo);

        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse(e.getStatus());
        }
    }


}
