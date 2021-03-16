package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends CrudRepository<ExerciseInfo, Long> {


}
