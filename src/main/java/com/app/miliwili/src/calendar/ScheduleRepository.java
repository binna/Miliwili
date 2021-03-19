package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.Schedule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    //List<Schedule> findByUser_IdAndDistinctionAndStatusOrderByStartDate(Long userId, String distinction, String status);
}