package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbnormalPromotionStateRepository extends CrudRepository<AbnormalPromotionState, Long> {
}
