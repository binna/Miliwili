package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.main.model.GetEndDayRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/main")
public class MainController {

    /**
     * 전역일 계산기
     * [GET]
     * @RequestHeader jwt
     * @return GetEndDayRes
     * @Auther vivi
     */
//    @ResponseBody
//    @GetMapping("/endDay")
//    public BaseResponse<GetEndDayRes> getEndDay() (@RequestHeader("X-ACCESS-TOKEN") String token){
//
//    }
}
