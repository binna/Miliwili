package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends CrudRepository<ExerciseInfo, Long> {
    Optional<ExerciseInfo> findByIdAndStatus(Long exerciseId, String status);
    Optional<ExerciseInfo> findExerciseInfoByUserIdAndStatus(Long userId, String status);

}
