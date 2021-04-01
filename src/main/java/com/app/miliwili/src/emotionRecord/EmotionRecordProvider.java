package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.emotionRecord.dto.DateEmotionRecordRes;
import com.app.miliwili.src.emotionRecord.dto.GetCurrentMonthTodayEmotionRecordRes;
import com.app.miliwili.src.emotionRecord.dto.MonthEmotionRecordRes;
import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
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
    public EmotionRecord retrieveEmotionByDateAndUserIdAndStatusY(String date, Long userId) throws BaseException {
        return emotionRecordRepository
                .findByDateAndUserInfo_idAndStatus(LocalDate.parse(date, DateTimeFormatter.ISO_DATE), userId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION_RECORD));
    }

    /**
     * 날짜로 존재하는 내 감정일기 여부 확인
     *
     * @param date
     * @param userId
     * @return boolean
     * @Auther shine
     */
    public boolean isEmotionRecordByDateAndUserIdAndStatusY(LocalDate date, Long userId) {
        return emotionRecordRepository.existsByDateAndUserInfo_IdAndStatus(date, userId, "Y");
    }

    /**
     * 월별 내 유효한 감정일기 조회
     *
     * @param month
     * @return List<EmotionRecord>
     * @throws BaseException
     * @Auther shine
     */
    public List<EmotionRecord> retrieveEmotionByStatusYAndDateBetween(String month, Long userId) throws BaseException {
        LocalDate start = LocalDate.parse((month + "-01"), DateTimeFormatter.ISO_DATE);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());

        try {
            return emotionRecordRepository.findByStatusAndUserInfo_IdAndDateBetween("Y", userId, start, end);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_MONTH_EMOTION_RECORD);
        }
    }




    /**
     * 회원별 모든 감정일기 조회(삭제용)
     *
     * @return List<EmotionRecord>
     * @throws BaseException
     * @Auther shine
     */
    public List<EmotionRecord> retrieveEmotionByUser(Long userId) throws BaseException {
        try {
            return emotionRecordRepository.findByUserInfo_Id(userId);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_EMOTION_RECORD);
        }
    }

    /**
     * 회원별 상태 "N"의 모든 감정일기 조회(삭제 롤백용)
     *
     * @return List<EmotionRecord>
     * @throws BaseException
     * @Auther shine
     */
    public List<EmotionRecord> retrieveEmotionByUserAndStatusN(Long userId) throws BaseException {
        try {
            return emotionRecordRepository.findByUserInfo_IdAndStatus(userId, "N");
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_EMOTION_RECORD);
        }
    }




    /**
     * 이번달, 오늘 내 감정일기 조회
     *
     * @return GetCurrentMonthTodayEmotionRecordRes
     * @throws BaseException
     * @Auther shine
     */
    public GetCurrentMonthTodayEmotionRecordRes getEmotionRecordFromCurrentMonthAndToday() throws BaseException {
        List<EmotionRecord> monthEmotionRecord = null;
        Long userId = jwtService.getUserId();

        try {
            monthEmotionRecord = retrieveEmotionByStatusYAndDateBetween(Validation.getCurrentMonth(), userId);
            EmotionRecord dayEmotionRecord = retrieveEmotionByDateAndUserIdAndStatusY(LocalDate.now().format(DateTimeFormatter.ISO_DATE), userId);
            return GetCurrentMonthTodayEmotionRecordRes.builder()
                    .month(changeListEmotionRecordToListMonthEmotionRecordRes(monthEmotionRecord))
                    .today(changeEmotionRecordToDateEmotionRecordRes(dayEmotionRecord))
                    .build();
        } catch (BaseException exception) {
            if (exception.getStatus() == NOT_FOUND_EMOTION_RECORD) {
                return GetCurrentMonthTodayEmotionRecordRes.builder()
                        .month(changeListEmotionRecordToListMonthEmotionRecordRes(monthEmotionRecord))
                        .today(null)
                        .build();
            }
            exception.printStackTrace();
            throw new BaseException(exception.getStatus());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(FAILED_TO_GET_EMOTION_RECORD);
        }
    }

    /**
     * 월별 감정 조회
     *
     * @param month
     * @return List<MonthEmotionRecordRes>
     * @throws BaseException
     */
    public List<MonthEmotionRecordRes> getEmotionRecordFromMonth(String month) throws BaseException {
        try {
            List<EmotionRecord> monthEmotionRecord = retrieveEmotionByStatusYAndDateBetween(month.substring(0, 4) + "-" + month.substring(4, 6), jwtService.getUserId());
            return monthEmotionRecord.stream().map(emotionRecord -> {
                return MonthEmotionRecordRes.builder()
                        .date(emotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                        .emotion(emotionRecord.getEmoticon())
                        .build();
            }).collect(Collectors.toList());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(FAILED_TO_GET_EMOTION_RECORD);
        }
    }

    /**
     * 감정기록 일별 조회
     *
     * @param date
     * @return EmotionRecordRes
     * @throws BaseException
     * @Auther shine
     */
    public DateEmotionRecordRes getEmotionRecordFromDate(String date) throws BaseException {
        date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);

        if (LocalDate.parse(date).isAfter(LocalDate.now())) {
            throw new BaseException(FASTER_THAN_TODAY);
        }

        try {
            EmotionRecord dateEmotionRecord = retrieveEmotionByDateAndUserIdAndStatusY(date, jwtService.getUserId());
            return changeEmotionRecordToDateEmotionRecordRes(dateEmotionRecord);
        } catch (BaseException exception) {
            if (exception.getStatus() == NOT_FOUND_EMOTION_RECORD) {
                throw new BaseException(NOT_FOUND_EMOTION_RECORD);
            }
            throw new BaseException(exception.getStatus());
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_DAY_EMOTION_RECORD);
        }
    }




    /**
     * emotion을 넣으면 알맞은 emotionText으로 변환
     *
     * @param emotion
     * @return String
     */
    public String changeEmotionToEmotionText(Integer emotion) {
        String[] emotionText = {"행복해", "슬퍼", "화가나!", "힘들어..", "gunchim", "흠..", "???", "룰루~", "좋-아"};

        return emotionText[emotion - 1];
    }




    private List<MonthEmotionRecordRes> changeListEmotionRecordToListMonthEmotionRecordRes(List<EmotionRecord> parameter) {
        if (Objects.isNull(parameter) || parameter.isEmpty()) return null;

        return parameter.stream().map(emotionRecord -> {
            return MonthEmotionRecordRes.builder()
                    .date(emotionRecord.getDate().format(DateTimeFormatter.ISO_DATE))
                    .emotion(emotionRecord.getEmoticon())
                    .build();
        }).collect(Collectors.toList());

    }

    private DateEmotionRecordRes changeEmotionRecordToDateEmotionRecordRes(EmotionRecord parameter) {
        if (Objects.isNull(parameter)) return null;

        return DateEmotionRecordRes.builder()
                .emotionRecordId(parameter.getId())
                .date(parameter.getDate().format(DateTimeFormatter.ISO_DATE))
                .emotion(parameter.getEmoticon())
                .content(parameter.getContent())
                .emotionText(changeEmotionToEmotionText(parameter.getEmoticon()))
                .build();
    }
}