package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.emotionRecord.dto.EmotionRecordReq;
import com.app.miliwili.src.emotionRecord.dto.EmotionRecordRes;
import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class EmotionRecordService {
    private final EmotionRecordRepository emotionRecordRepository;
    private final UserProvider userProvider;
    private final EmotionRecordProvider emotionRecordProvider;
    private final JwtService jwtService;

    /**
     * 감정기록 생성
     *
     * @param parameters
     * @return EmotionRecordRes
     * @throws BaseException
     * @Auther shine
     */
    public EmotionRecordRes createEmotionRecord(EmotionRecordReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        EmotionRecord newEmotionRecord = EmotionRecord.builder()
                .date(LocalDate.now())
                .content(parameters.getContent())
                .emoticon(parameters.getEmotion())
                .userInfo(user)
                .build();

        if (emotionRecordProvider.isEmotionRecordByDateAndStatusY(newEmotionRecord.getDate())) {
            throw new BaseException(ALREADY_EXIST_EMOTION_RECORD);
        }

        try {
            EmotionRecord savedEmotionRecord = emotionRecordRepository.save(newEmotionRecord);
            return EmotionRecordRes.builder()
                    .emotionRecordId(savedEmotionRecord.getId())
                    .date(savedEmotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                    .content(savedEmotionRecord.getContent())
                    .emotion(savedEmotionRecord.getEmoticon())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_EMOTION_RECORD);
        }
    }

    /**
     * 감정기록 수정
     *
     * @param parameters
     * @param emotionRecordId
     * @return EmotionRecordRes
     * @throws BaseException
     * @Auther shine
     */
    public EmotionRecordRes updateEmotionRecord(EmotionRecordReq parameters, Long emotionRecordId) throws BaseException {
        EmotionRecord emotionRecord = emotionRecordProvider.retrieveEmotionRecordByIdAndStatusY(emotionRecordId);

        if (emotionRecord.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        emotionRecord.setContent(parameters.getContent());
        emotionRecord.setEmoticon(parameters.getEmotion());

        try {
            EmotionRecord savedEmotionRecord = emotionRecordRepository.save(emotionRecord);
            return EmotionRecordRes.builder()
                    .emotionRecordId(savedEmotionRecord.getId())
                    .date(savedEmotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                    .content(savedEmotionRecord.getContent())
                    .emotion(savedEmotionRecord.getEmoticon())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_EMOTION_RECORD);
        }
    }

    /**
     * 감정기록 삭제
     *
     * @param emotionRecordId
     * @throws BaseException
     * @Auther shine
     */
    public void deleteEmotionRecord(Long emotionRecordId) throws BaseException {
        EmotionRecord emotionRecord = emotionRecordProvider.retrieveEmotionRecordByIdAndStatusY(emotionRecordId);

        if (emotionRecord.getUserInfo().getId() != jwtService.getUserId()) {
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