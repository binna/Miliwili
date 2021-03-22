package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.exercise.model.ExerciseRoutine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ExerciseRoutineRepository extends CrudRepository<ExerciseRoutine, Long> {


}
