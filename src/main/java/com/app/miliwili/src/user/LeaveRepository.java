package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.Leave;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends CrudRepository<Leave, Long> {
    List<Leave> findByUser_IdOrderById(Long userId);
}