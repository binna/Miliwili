package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.dto.MyRoutineInfo;
import com.app.miliwili.src.exercise.model.*;
import com.app.miliwili.src.user.models.QUserInfo;
import com.app.miliwili.src.user.models.UserInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ExerciseSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public ExerciseSelectRepository(JPAQueryFactory queryFactory){
        super(UserInfo.class);
        this.queryFactory=queryFactory;
    }

    /**
     * first-weight에서 등록되어 있는 운동인지 파악
     * exerciseInfo 가져오기
     */
    public List<Long> getExerciseInfoByUserId(Long userId){
        QExerciseInfo exerciseInfo = QExerciseInfo.exerciseInfo;
        return queryFactory.select((Projections.constructor(Long.class,
                exerciseInfo.id)))
                .from(exerciseInfo)
                .where(exerciseInfo.user.id.eq(userId), exerciseInfo.status.eq("Y"))
                .fetch();

    }

    /**
     * 일별 체중 조회
     */
    public List<ExerciseWeightRecord> getWeightRecordByExerciseId(Long exerciseId){
        QExerciseWeightRecord exerciseWeightRecord = QExerciseWeightRecord.exerciseWeightRecord;
        QExerciseInfo exerciseInfo = QExerciseInfo.exerciseInfo;

        return queryFactory.select((Projections.constructor(ExerciseWeightRecord.class)))
                .from(exerciseWeightRecord)
                .where(exerciseWeightRecord.status.eq("Y"))
                .join(exerciseInfo)
                .on(exerciseInfo.id.eq(exerciseWeightRecord.exerciseInfo.id))
                .limit(5)
                .fetchJoin().fetch();
    }

    /**
     * 사용자 id로 운동 Id 찾기
     */
    public Long getExerciseIdByUserId(Long userId) {
        QExerciseInfo exerciseInfo = QExerciseInfo.exerciseInfo;
        return queryFactory.select((Projections.constructor(Long.class,
                exerciseInfo.id)))
                .from(exerciseInfo)
                .where(exerciseInfo.user.id.eq(userId), exerciseInfo.status.eq("Y"))
                .fetchOne();


    }

    /**
     * 오늘의 체중기록 있는지 없는지
     */
    public Long getExerciseTodayWeight(Long exerciseId, LocalDate todayDate) {
        QExerciseWeightRecord exerciseWeightRecord = QExerciseWeightRecord.exerciseWeightRecord;
        return queryFactory.select((Projections.constructor(Long.class)))
                .from(exerciseWeightRecord)
                .where(exerciseWeightRecord.exerciseInfo.id.eq(exerciseId), exerciseWeightRecord.status.eq("Y"),
                        exerciseWeightRecord.exerciseDate.eq(todayDate))
                .fetchOne();


    }
}
