package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.emotionRecord.dto.EmotionRecordReq;
import com.app.miliwili.src.emotionRecord.dto.EmotionRecordRes;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class EmotionRecordController {
    private final EmotionRecordService emotionRecordService;
    private final EmotionRecordProvider emotionRecordProvider;

    /**
     * 감정기록 월별 조회 API
     * [GET] /app/emotions-record/month?month=
     *
     * @return BaseResponse<List<EmotionRecordRes>>
     * @Token X-ACCESS-TOKEN
     * @RequestParam String month
     * @Auther shine
     */
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

    /**
     * 감정기록 일별 조회 API
     * [GET] /app/emotions-record/day?date=
     *
     * @return BaseResponse<EmotionRecordRes>
     * @Token X-ACCESS-TOKEN
     * @RequestParam String date
     * @Auther shine
     */
    @ApiOperation(value = "감정기록 일별 조회", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @GetMapping("/app/emotions-record/day")
    public BaseResponse<EmotionRecordRes> getEmotionRecordFromDate(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                                   @RequestParam(value = "date", required = false) String date) {
        try {
            EmotionRecordRes emotionRecord = emotionRecordProvider.getEmotionRecordFromDate(date);
            return new BaseResponse<>(SUCCESS, emotionRecord);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 감정기록 생성 API
     * [POST] /app/emotions-record
     *
     * @return BaseResponse<EmotionRecordRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody EmotionRecordReq parameters
     * @Auther shine
     */
    @ApiOperation(value = "감정기록 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/app/emotions-record")
    public BaseResponse<EmotionRecordRes> postEmotionRecord(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                            @RequestBody(required = false) EmotionRecordReq parameters) {
        if (Objects.isNull(parameters.getContent()) || parameters.getContent().length() == 0) {
            return new BaseResponse<>(EMPTY_CONTENT);
        }
        if (Objects.isNull(parameters.getEmotion()) || parameters.getEmotion().length() == 0) {
            return new BaseResponse<>(EMPTY_EMOTION);
        }

        try {
            EmotionRecordRes emotionRecord = emotionRecordService.createEmotionRecord(parameters);
            return new BaseResponse<>(SUCCESS, emotionRecord);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 감정기록 수정 API
     * [PATCH] /app/emotions-record/:emotionsRecordId
     *
     * @return BaseResponse<EmotionRecordRes>
     * @Token X-ACCESS-TOKEN
     * @RequestBody EmotionRecordReq parameters
     * @PathVariable Long emotionsRecordId
     * @Auther shine
     */
    @ApiOperation(value = "감정기록 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/app/emotions-record/{emotionsRecordId}")
    public BaseResponse<EmotionRecordRes> updateEmotionRecord(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                              @RequestBody(required = false) EmotionRecordReq parameters,
                                                              @PathVariable Long emotionsRecordId) {
        if (Objects.isNull(parameters.getContent()) || parameters.getContent().length() == 0) {
            return new BaseResponse<>(EMPTY_CONTENT);
        }
        if (Objects.isNull(parameters.getEmotion()) || parameters.getEmotion().length() == 0) {
            return new BaseResponse<>(EMPTY_EMOTION);
        }

        try {
            EmotionRecordRes emotionRecord = emotionRecordService.updateEmotionRecord(parameters, emotionsRecordId);
            return new BaseResponse<>(SUCCESS, emotionRecord);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 감정기록 삭제 API
     * [DELETE] /app/emotions-record/:emotionsRecordId
     *
     * @return BaseResponse<Void>
     * @Token X-ACCESS-TOKEN
     * @PathVariable Long emotionsRecordId
     * @Auther shine
     */
    @ApiOperation(value = "감정기록 삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @DeleteMapping("/app/emotions-record/{emotionsRecordId}")
    public BaseResponse<Void> deleteEmotionRecord(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                  @PathVariable Long emotionsRecordId) {
        try {
            emotionRecordService.deleteEmotionRecord(emotionsRecordId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}