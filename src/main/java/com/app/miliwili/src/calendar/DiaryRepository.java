package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.Diary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiaryRepository extends CrudRepository<Diary, Long> {
    Optional<Diary> findById(Long diaryId);
}