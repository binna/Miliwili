package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseRoutine;
import com.app.miliwili.src.exercise.model.ExerciseRoutineDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRoutineDetailRepository extends CrudRepository<ExerciseRoutineDetail, Long> {


}
