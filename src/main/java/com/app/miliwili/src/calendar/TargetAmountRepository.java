package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.TargetAmount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetAmountRepository extends CrudRepository<TargetAmount, Long> {;}