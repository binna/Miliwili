package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.emotionRecord.dto.PatchEmotionRecordReq;
import com.app.miliwili.src.emotionRecord.dto.DayEmotionRecordRes;
import com.app.miliwili.src.emotionRecord.dto.PostEmotionRecordReq;
import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public DayEmotionRecordRes createEmotionRecord(PostEmotionRecordReq parameters) throws BaseException {
        Long userId = jwtService.getUserId();
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(userId);

        EmotionRecord newEmotionRecord = EmotionRecord.builder()
                .date(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE))
                .content(parameters.getContent())
                .emoticon(parameters.getEmotion())
                .userInfo(user)
                .build();

        if (emotionRecordProvider.isEmotionRecordByDateAndUserIdAndStatusY(newEmotionRecord.getDate(), userId)) {
            throw new BaseException(ALREADY_EXIST_EMOTION_RECORD);
        }

        try {
            EmotionRecord savedEmotionRecord = emotionRecordRepository.save(newEmotionRecord);
            return DayEmotionRecordRes.builder()
                    .emotionRecordId(savedEmotionRecord.getId())
                    .date(savedEmotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                    .content(savedEmotionRecord.getContent())
                    .emotion(savedEmotionRecord.getEmoticon())
                    .emotionText(emotionRecordProvider.changeEmotionToEmotionText(savedEmotionRecord.getEmoticon()))
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
    public DayEmotionRecordRes updateEmotionRecord(PatchEmotionRecordReq parameters, Long emotionRecordId) throws BaseException {
        EmotionRecord emotionRecord = emotionRecordProvider.retrieveEmotionRecordByIdAndStatusY(emotionRecordId);

        if (emotionRecord.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        emotionRecord.setContent(parameters.getContent());
        emotionRecord.setEmoticon(parameters.getEmotion());

        try {
            EmotionRecord savedEmotionRecord = emotionRecordRepository.save(emotionRecord);
            return DayEmotionRecordRes.builder()
                    .emotionRecordId(savedEmotionRecord.getId())
                    .date(savedEmotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                    .content(savedEmotionRecord.getContent())
                    .emotion(savedEmotionRecord.getEmoticon())
                    .emotionText(emotionRecordProvider.changeEmotionToEmotionText(savedEmotionRecord.getEmoticon()))
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
    public void deleteEmotionRecordByEmotionRecordId(Long emotionRecordId) throws BaseException {
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

    /**
     * 회원 삭제시, 회원별 감정기록 삭제
     *
     * @throws BaseException
     * @Auther shine
     */
    public void deleteEmotionRecordByUser(Long userId) throws BaseException {
        List<EmotionRecord> emotionRecords = emotionRecordProvider.retrieveEmotionByUser(userId);

        for (EmotionRecord emotionRecord : emotionRecords) {
            emotionRecord.setStatus("N");
        }

        try {
            emotionRecordRepository.saveAll(emotionRecords);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_EMOTION_RECORD);
        }
    }

    /**
     * 회원 삭제 문제 발생시, 회원별 감정기록 삭제 롤백
     *
     * @throws BaseException
     * @Auther shine
     */
    public void deleteRollbackEmotionRecord(Long userId) throws BaseException {
        List<EmotionRecord> emotionRecords = emotionRecordProvider.retrieveEmotionByUserAndStatusN(userId);

        for (EmotionRecord emotionRecord : emotionRecords) {
            emotionRecord.setStatus("Y");
        }

        try {
            emotionRecordRepository.saveAll(emotionRecords);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_ROLLBACK_EMOTION_RECORD);
        }
    }
}