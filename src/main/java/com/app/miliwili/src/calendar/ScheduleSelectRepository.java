package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.Plan;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public ScheduleSelectRepository(JPAQueryFactory queryFactory) {
        super(Plan.class);
        this.queryFactory = queryFactory;
    }

    /**
     * 내 등록된 일정 중 정기휴가의 날짜 부분만 검색
     */
//    public List<Schedule> findSchedulePushYAndStatusY() {
//        QSchedule schedule = QSchedule.schedule;
//        QScheduleDate scheduleDate = QScheduleDate.scheduleDate;
//
//        return queryFactory.select((Projections.constructor(Schedule.class)))
//                .from(schedule)
//                .where(schedule.push.eq("Y"), schedule.status.eq("Y"))
//                .innerJoin(scheduleDate.schedule, schedule)
//                .orderBy(scheduleDate.date.asc())
//                .fetch();
//    }
}