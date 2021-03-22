package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, Long> {
    Optional<UserInfo> findByIdAndStatus(Long id, String status);
    Optional<UserInfo> findBySocialIdAndStatus(String socialId, String status);
    boolean existsBySocialIdAndStatus(String socialId, String status);
    List<UserInfo> findAllByStateIdxAndStatus(Integer stateIdx, String status);
}