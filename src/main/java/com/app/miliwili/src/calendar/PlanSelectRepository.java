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
     * 금일 유효한 내 일정 전체조회(메인조회용)
     * (일정 시작일 순으로 오름차순)
     *
     * @param userId
     * @param today
     * @return List<PlanData>
     * @Auther shine
     */
    public List<PlanMainData> findPlanByToday(Long userId, LocalDate today) {
        QPlan plan = QPlan.plan;

        return queryFactory
                .select(Projections.constructor(PlanMainData.class,
                        plan.id.as("planId"), plan.title.as("title")
                ))
                .from(plan)
                .where(plan.userInfo.id.eq(userId), plan.status.eq("Y"),
                        plan.startDate.eq(today).or(plan.startDate.before(today)),
                        plan.endDate.eq(today).or(plan.endDate.after(today))
                )
                .orderBy(plan.startDate.asc())
                .fetch();
    }
}