package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.PlanDiary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanDiaryRepository extends CrudRepository<PlanDiary, Long> {;}