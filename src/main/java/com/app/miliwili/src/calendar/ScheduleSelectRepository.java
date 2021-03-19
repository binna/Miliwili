package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.QSchedule;
import com.app.miliwili.src.calendar.models.QScheduleDate;
import com.app.miliwili.src.calendar.models.Schedule;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScheduleSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public ScheduleSelectRepository(JPAQueryFactory queryFactory) {
        super(Schedule.class);
        this.queryFactory = queryFactory;
    }

    /**
     * 내 등록된 일정 중 정기휴가의 날짜 부분만 검색
     */
    public List<String> findScheduleDateByUserId(Long userId) {
        QSchedule schedule = QSchedule.schedule;
        QScheduleDate scheduleDate = QScheduleDate.scheduleDate;

        return queryFactory.select((Projections.constructor(String.class,
                scheduleDate.date)))
                .from(schedule)
                .where(schedule.distinction.eq("정기휴가"), schedule.user.id.eq(userId))
                .innerJoin(scheduleDate.schedule, schedule)
                .orderBy(scheduleDate.date.asc())
                .fetch();
    }
}
