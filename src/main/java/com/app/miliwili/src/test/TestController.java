package com.app.miliwili.src.test;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 로그 테스트 API
     * [GET] /test/log
     * @return BaseResponse<List<UserInfoRes>>
     */
    @ApiOperation(value = "로그테스트 API")
    @ResponseBody
    @GetMapping("/log")
    public String getAll() {
        logger.warn("Warn Level 테스트");

        return "Success Test";
    }
}