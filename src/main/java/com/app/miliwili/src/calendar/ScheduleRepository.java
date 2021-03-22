package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.Plan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Plan, Long> {
    List<Plan> findByPushAndStatusAndStartDate(String push, String status, LocalDate startDate);
}