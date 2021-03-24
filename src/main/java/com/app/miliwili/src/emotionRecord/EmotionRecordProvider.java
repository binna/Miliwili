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

    public EmotionRecord retrieveEmotionRecordByIdAndStatusY(Long emotionRecordId) throws BaseException {
        return emotionRecordRepository.findByIdAndStatus(emotionRecordId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION_RECORD));
    }

    public EmotionRecord retrieveEmotionByDateAndUserIdAndStatusY(String date) throws BaseException {
        return emotionRecordRepository
                .findByDateAndUserInfo_idAndStatus(LocalDate.parse(date, DateTimeFormatter.ISO_DATE), jwtService.getUserId(), "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION_RECORD));
    }

    public List<EmotionRecord> retrieveEmotionByStatusYAndDateBetween(String month) throws BaseException {
        LocalDate start = LocalDate.parse((month + "01"), DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse((month + start.with(TemporalAdjusters.lastDayOfMonth())), DateTimeFormatter.ISO_DATE);

        try {
            return emotionRecordRepository.findByStatusAndDateBetween("Y", start, end);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_EMOTION_RECORD);
        }
    }

    public List<EmotionRecordRes> getEmotionRecordFromMonth(String month) throws BaseException {
        List<EmotionRecord> emotionRecords = retrieveEmotionByStatusYAndDateBetween(month);

        return emotionRecords.stream().map(emotionRecord -> {
            return EmotionRecordRes.builder()
                    .emotionRecordId(emotionRecord.getId())
                    .date(emotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                    .emotion(emotionRecord.getEmoticon())
                    .content(emotionRecord.getContent())
                    .build();
        }).collect(Collectors.toList());

    }

    public EmotionRecordRes getEmotionRecordFromDate(String date) throws BaseException {
        EmotionRecord emotionRecord = retrieveEmotionByDateAndUserIdAndStatusY(date);

        return EmotionRecordRes.builder()
                .emotionRecordId(emotionRecord.getId())
                .date(emotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                .emotion(emotionRecord.getEmoticon())
                .content(emotionRecord.getContent())
                .build();
    }
}