package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.emotionRecord.dto.*;
import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class EmotionRecordService {
    private final EmotionRecordRepository emotionRecordRepository;
    private final UserProvider userProvider;
    private final EmotionRecordProvider emotionRecordProvider;
    private final JwtService jwtService;

    public EmotionRecordRes createEmotionRecord(EmotionRecordReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        EmotionRecord newEmotionRecord = EmotionRecord.builder()
                .date(LocalDate.now())
                .content(parameters.getContent())
                .emoticon(parameters.getEmotion())
                .build();

        // 기존에 등록되어있는지 검자하는 로직 필요

        try {
            EmotionRecord savedEmotionRecord = emotionRecordRepository.save(newEmotionRecord);
            return EmotionRecordRes.builder()
                    .emotionRecordId(savedEmotionRecord.getId())
                    .content(savedEmotionRecord.getContent())
                    .emotion(savedEmotionRecord.getEmoticon())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_EMOTION_RECORD);
        }
    }

    public EmotionRecordRes updateEmotionRecord(EmotionRecordReq parameters, Long emotionRecordId) throws BaseException {
        EmotionRecord emotionRecord = emotionRecordProvider.retrieveEmotionRecordByIdAndStatusY(emotionRecordId);

        if(emotionRecord.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        emotionRecord.setContent(parameters.getContent());
        emotionRecord.setEmoticon(parameters.getEmotion());

        try {
            EmotionRecord savedEmotionRecord = emotionRecordRepository.save(emotionRecord);
            return EmotionRecordRes.builder()
                    .emotionRecordId(savedEmotionRecord.getId())
                    .content(savedEmotionRecord.getContent())
                    .emotion(savedEmotionRecord.getEmoticon())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_EMOTION_RECORD);
        }
    }

    public void deleteEmotionRecord(Long emotionRecordId) throws BaseException {
        EmotionRecord emotionRecord = emotionRecordProvider.retrieveEmotionRecordByIdAndStatusY(emotionRecordId);

        if(emotionRecord.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        emotionRecord.setStatus("N");

        try {
            emotionRecordRepository.save(emotionRecord);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_EMOTION_RECORD);
        }
    }
}