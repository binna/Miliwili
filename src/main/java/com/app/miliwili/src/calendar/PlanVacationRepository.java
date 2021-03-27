package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.PlanVacation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanVacationRepository extends CrudRepository<PlanVacation, Long> {
    List<PlanVacation> findByVacationIdAndStatus(Long vacationId, String status);
}
