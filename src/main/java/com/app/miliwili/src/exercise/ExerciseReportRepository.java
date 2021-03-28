package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseReport;
import com.app.miliwili.src.exercise.model.ExerciseWeightRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@ResponseBody
public interface ExerciseReportRepository extends CrudRepository<ExerciseReport, Long> {

    List<ExerciseReport> findExerciseReportByExerciseRoutine_IdAndStatusAndDateCreatedBetween(Long routineId,String status, LocalDateTime dateCreated, LocalDateTime dateCreatedNext);
   // List<ExerciseReport> findExerciseReportByExerciseRoutine_IdAndStatusAnd(Long routineId,String status, LocalDateTime dateCreated, LocalDateTime dateCreatedNext);

}
