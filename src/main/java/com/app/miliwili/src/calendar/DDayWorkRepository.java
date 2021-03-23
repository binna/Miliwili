package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.DDayWork;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DDayWorkRepository extends CrudRepository<DDayWork, Long> {;}