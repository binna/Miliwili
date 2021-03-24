package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.emotionRecord.dto.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class EmotionRecordController {
    private final EmotionRecordService emotionRecordService;
    private final EmotionRecordProvider emotionRecordProvider;

    @ApiOperation(value = "감정기록 월별 조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/app/emotions-record/month")
    public BaseResponse<List<EmotionRecordRes>> getEmotionRecordFromMonth(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                                          @RequestParam(value = "month", required = false) String month) {
        try {
            List<EmotionRecordRes> emotionRecords = emotionRecordProvider.getEmotionRecordFromMonth(month);
            return new BaseResponse<>(SUCCESS, emotionRecords);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ApiOperation(value = "감정기록 일별 조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/app/emotions-record/date")
    public BaseResponse<EmotionRecordRes> getEmotionRecordFromDate(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                                  @RequestParam(value = "date", required = false) String date) {
        try {
            EmotionRecordRes emotionRecord = emotionRecordProvider.getEmotionRecordFromDate(date);
            return new BaseResponse<>(SUCCESS, emotionRecord);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

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