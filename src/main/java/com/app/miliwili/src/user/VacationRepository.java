package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.Vacation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends CrudRepository<Vacation, Long> {
    List<Vacation> findByUserInfo_IdOrderById(Long userid);
}