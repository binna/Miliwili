package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.Plan;
import com.app.miliwili.src.calendar.models.QPlan;
import com.app.miliwili.src.main.dto.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PlanSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public PlanSelectRepository(JPAQueryFactory queryFactory) {
        super(Plan.class);
        this.queryFactory = queryFactory;
    }

    /**
     * 날짜별 유효한 내 일정 전체조회(메인조회용)
     * (일정 시작일로 오름차순)
     *
     * @param userId
     * @param date
     * @return List<PlanData>
     * @Auther shine
     */
    public List<PlanMainData> findPlanByDate(Long userId, LocalDate date) {
        QPlan plan = QPlan.plan;

        return queryFactory
                .select(Projections.constructor(PlanMainData.class,
                        plan.id.as("planId"), plan.title.as("title")
                ))
                .from(plan)
                .where(plan.userInfo.id.eq(userId), plan.status.eq("Y"),
                        plan.startDate.eq(date).or(plan.startDate.before(date)),
                        plan.endDate.eq(date).or(plan.endDate.after(date))
                )
                .orderBy(plan.startDate.asc())
                .fetch();
    }

    /**
     * 월별 유효한 내 일정 전체조회(메인조회용)
     * (일정 시작일로 오름차순)
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return List<PlanCalendarData>
     * @Auther shine
     */
    public List<PlanCalendarData> findPlanByMonth(Long userId, LocalDate startDate, LocalDate endDate) {
        QPlan plan = QPlan.plan;

        return queryFactory
                .select(Projections.constructor(PlanCalendarData.class,
                        plan.color.as("color"), plan.startDate.as("startDate"), plan.endDate.as("endDate")
                ))
                .from(plan)
                .where(plan.userInfo.id.eq(userId), plan.status.eq("Y"),
                        plan.startDate.eq(startDate).or(plan.startDate.before(startDate)),
                        plan.endDate.eq(endDate).or(plan.endDate.after(endDate))
                )
                .orderBy(plan.startDate.asc())
                .fetch();
    }
}