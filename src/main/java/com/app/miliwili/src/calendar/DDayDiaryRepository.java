package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.DDayDiary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DDayDiaryRepository extends CrudRepository<DDayDiary, Long> {
    boolean existsByDateAndDday_Id(LocalDate date, Long ddayId);
}