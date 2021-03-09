package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    // TODO
}