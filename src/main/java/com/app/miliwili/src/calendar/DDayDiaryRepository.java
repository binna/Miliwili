package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.DDayDiary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DDayDiaryRepository extends CrudRepository<DDayDiary, Long> {
}