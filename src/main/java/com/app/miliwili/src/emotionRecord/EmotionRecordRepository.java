package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmotionRecordRepository extends CrudRepository<EmotionRecord, Long> {
    Optional<EmotionRecord> findByIdAndStatus(Long emotionRecordId, String status);
}