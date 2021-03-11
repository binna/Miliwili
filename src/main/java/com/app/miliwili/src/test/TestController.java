package com.app.miliwili.src.test;

import com.app.miliwili.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.app.miliwili.config.BaseResponseStatus.SUCCESS;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    @ResponseBody
    @GetMapping("")
    public BaseResponse<String> testDeployment(){
        return new BaseResponse<>(SUCCESS,"success Building");
    }
}
