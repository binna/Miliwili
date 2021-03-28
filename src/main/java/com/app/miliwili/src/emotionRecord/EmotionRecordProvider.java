package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.emotionRecord.dto.EmotionRecordRes;
import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class EmotionRecordProvider {
    private final EmotionRecordRepository emotionRecordRepository;
    private final JwtService jwtService;


    /**
     * emotionRecordId로 유효한 감정일기 조회
     *
     * @param emotionRecordId
     * @return EmotionRecord
     * @throws BaseException
     * @Auther shine
     */
    public EmotionRecord retrieveEmotionRecordByIdAndStatusY(Long emotionRecordId) throws BaseException {
        return emotionRecordRepository.findByIdAndStatus(emotionRecordId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION_RECORD));
    }

    /**
     * 날짜로 내 유효한 감정일기 조회
     *
     * @param date
     * @return EmotionRecord
     * @throws BaseException
     * @Auther shine
     */
    public EmotionRecord retrieveEmotionByDateAndUserIdAndStatusY(String date) throws BaseException {
        return emotionRecordRepository
                .findByDateAndUserInfo_idAndStatus(LocalDate.parse(date, DateTimeFormatter.ISO_DATE), jwtService.getUserId(), "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION_RECORD));
    }

    /**
     * 날짜로 존재하는 감정일기 여부 확인
     *
     * @param date
     * @return boolean
     * @Auther shine
     */
    public boolean isEmotionRecordByDateAndStatusY(LocalDate date) {
        return emotionRecordRepository.existsByDateAndStatus(date, "Y");
    }

    /**
     * 월별 내 유효한 감정일기 조회
     *
     * @param month
     * @return List<EmotionRecord>
     * @throws BaseException
     * @Auther shine
     */
    public List<EmotionRecord> retrieveEmotionByStatusYAndDateBetween(String month) throws BaseException {
        month = month.substring(0, 4) + "-" + month.substring(4, 6);

        LocalDate start = LocalDate.parse((month + "-01"), DateTimeFormatter.ISO_DATE);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());

        try {
            return emotionRecordRepository.findByStatusAndDateBetween("Y", start, end);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_EMOTION_RECORD);
        }
    }




    /**
     * 감정기록 월별 조회
     *
     * @param month
     * @return List<EmotionRecordRes>
     * @throws BaseException
     * @Auther shine
     */
    public List<EmotionRecordRes> getEmotionRecordFromMonth(String month) throws BaseException {
        List<EmotionRecord> emotionRecords = retrieveEmotionByStatusYAndDateBetween(month);

        if (emotionRecords.isEmpty()) {
            throw new BaseException(NOT_FOUND_EMOTION_RECORD);
        }

        for (EmotionRecord emotionRecord : emotionRecords) {
            if (emotionRecord.getUserInfo().getId() != jwtService.getUserId()) {
                throw new BaseException(DO_NOT_AUTH_USER);
            }
        }

        return emotionRecords.stream().map(emotionRecord -> {
            return EmotionRecordRes.builder()
                    .emotionRecordId(emotionRecord.getId())
                    .date(emotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                    .emotion(emotionRecord.getEmoticon())
                    .content(emotionRecord.getContent())
                    .emotionText(changeEmotionToEmotionText(emotionRecord.getEmoticon()))
                    .build();
        }).collect(Collectors.toList());

    }

    /**
     * 감정기록 일별 조회
     *
     * @param date
     * @return EmotionRecordRes
     * @throws BaseException
     * @Auther shine
     */
    public EmotionRecordRes getEmotionRecordFromDate(String date) throws BaseException {
        EmotionRecord emotionRecord = retrieveEmotionByDateAndUserIdAndStatusY(date);

        if (emotionRecord.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        return EmotionRecordRes.builder()
                .emotionRecordId(emotionRecord.getId())
                .date(emotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                .emotion(emotionRecord.getEmoticon())
                .content(emotionRecord.getContent())
                .emotionText(changeEmotionToEmotionText(emotionRecord.getEmoticon()))
                .build();
    }




    /**
     * emotion을 넣으면 알맞은 emotionText으로 변환
     *
     * @param emotion
     * @return String
     */
    public String changeEmotionToEmotionText(Integer emotion) {
        String[] emotionText = {"행복해", "슬퍼", "화가나!", "힘들어..", "gunchim", "흠..", "???", "룰루~", "좋-아"};

        return emotionText[emotion + 1];
    }
}