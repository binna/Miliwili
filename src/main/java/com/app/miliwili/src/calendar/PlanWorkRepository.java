package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.PlanWork;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanWorkRepository extends CrudRepository<PlanWork, Long> {;}