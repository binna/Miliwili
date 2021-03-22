package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseDetailSet;
import com.app.miliwili.src.exercise.model.ExerciseRoutine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseDetailSetRepository extends CrudRepository<ExerciseDetailSet, Long> {


}

