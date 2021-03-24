package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.PlanVacation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanVacationRepository extends CrudRepository<PlanVacation, Long> {
    Optional<PlanVacation> findByVacationIdAndStatus(Long vacationId, String status);
}
