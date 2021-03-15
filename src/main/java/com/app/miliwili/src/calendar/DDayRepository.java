package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.DDay;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DDayRepository extends CrudRepository<DDay, Long> {
    // TODO
}