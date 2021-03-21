package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.exercise.model.ExerciseWeightRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseWeightRepository extends CrudRepository<ExerciseWeightRecord, Long> {
    List<ExerciseWeightRecord> findTop5ByExerciseInfo_IdAndStatusOrderByDateCreatedDesc(Long exerciseId, String status);

    //체중 조회 --> 지정 월의 모든 날 정보 가져오기
  //  @Query("select * from ExerciseWeightRecord where status == "Y"")
   //
    // List<ExerciseWeightRecord> findAllByExerciseInfo_IdAndStatusAndDateCreated_YearAndDateCreated_MonthValue(Long exerciseId, String status, Integer year, Integer month);



}

