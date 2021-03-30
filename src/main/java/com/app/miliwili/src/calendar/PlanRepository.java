package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.Plan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends CrudRepository<Plan, Long> {
    List<Plan> findByPushAndStatusAndStartDate(String push, String status, LocalDate startDate);
    List<Plan> findByUserInfo_Id(Long userId);
    List<Plan> findByUserInfo_IdAndStatus(Long userId, String status);
    Optional<Plan> findByIdAndStatus(Long planId, String status);
}