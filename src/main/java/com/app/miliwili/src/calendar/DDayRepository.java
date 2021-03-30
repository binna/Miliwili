package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.DDay;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DDayRepository extends CrudRepository<DDay, Long> {
    Optional<DDay> findByIdAndStatus(Long ddayId, String status);
    List<DDay> findByUserInfo_Id(Long userId);
    List<DDay> findByUserInfo_IdAndStatus(Long userId, String status);
}