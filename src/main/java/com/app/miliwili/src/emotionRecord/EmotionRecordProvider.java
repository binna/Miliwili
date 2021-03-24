package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class EmotionRecordProvider {
    private final EmotionRecordRepository emotionRecordRepository;

    public EmotionRecord retrieveEmotionRecordByIdAndStatusY(Long emotionRecordId) throws BaseException {
        return emotionRecordRepository.findByIdAndStatus(emotionRecordId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_EMOTION_RECORD));
    }
}