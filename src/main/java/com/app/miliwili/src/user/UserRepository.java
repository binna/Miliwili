package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByIdAndStatus(Long id, String status);
    Optional<User> findBySocialIdAndStatus(String socialId, String status);
    boolean existsBySocialIdAndStatus(String socialId, String status);
}