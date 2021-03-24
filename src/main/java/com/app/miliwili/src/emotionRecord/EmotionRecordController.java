package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.emotionRecord.dto.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class EmotionRecordController {
    private final EmotionRecordService emotionRecordService;

    /**
     *
     */
    @ApiOperation(value = "감정기록 등록", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/app/emotions-record")
    public BaseResponse<EmotionRecordRes> postEmotionRecord(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                            @RequestBody(required = false) EmotionRecordReq parameters) {
        // TODO 유효성 검사

        try {
            EmotionRecordRes emotionRecord = emotionRecordService.createEmotionRecord(parameters);
            return new BaseResponse<>(SUCCESS, emotionRecord);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     *
     * @param token
     * @param parameters
     * @param emotionsRecordId
     * @return
     */
    @ApiOperation(value = "감정기록 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/app/emotions-record/{emotionsRecordId}")
    public BaseResponse<EmotionRecordRes> updateEmotionRecord(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                              @RequestBody(required = false) EmotionRecordReq parameters,
                                                              @PathVariable Long emotionsRecordId) {
        // TODO 유효성 검사

        try {
            EmotionRecordRes emotionRecord = emotionRecordService.updateEmotionRecord(parameters, emotionsRecordId);
            return new BaseResponse<>(SUCCESS, emotionRecord);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     *
     * @param token
     * @param emotionsRecordId
     * @return
     */
    @ApiOperation(value = "감정기록 삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @DeleteMapping("/app/emotions-record/{emotionsRecordId}")
    public BaseResponse<Void> deleteEmotionRecord(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                  @PathVariable Long emotionsRecordId) {
        // TODO 유효성 검사

        try {
            emotionRecordService.deleteEmotionRecord(emotionsRecordId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}