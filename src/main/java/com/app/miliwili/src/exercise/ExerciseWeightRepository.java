package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.exercise.model.ExerciseWeightRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseWeightRepository extends CrudRepository<ExerciseWeightRecord, Long> {
    List<ExerciseWeightRecord> findTop5ByExerciseInfo_IdAndStatusOrderByDateCreatedDesc(Long exerciseId, String status);


}

