package com.app.miliwili.src.user;

import com.app.miliwili.src.calendar.models.QPlanVacation;
import com.app.miliwili.src.user.dto.*;
import com.app.miliwili.src.user.models.QVacation;
import com.app.miliwili.src.user.models.Vacation;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VacationSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public VacationSelectRepository(JPAQueryFactory queryFactory) {
        super(Vacation.class);
        this.queryFactory = queryFactory;
    }

    /**
     * 회원 휴가 조회
     * 
     * @param userId
     * @return List<VacationSelectDate>
     */
    public List<VacationSelectDate> findVacationByUserIdAndStatusY(Long userId) {
        QVacation vacation = QVacation.vacation;
        QPlanVacation planVacation = QPlanVacation.planVacation;

        return queryFactory
                .select(Projections.constructor(VacationSelectDate.class,
                        vacation.id.as("id"), vacation.title.as("title"),
                        vacation.useDays.as("useDays"), vacation.totalDays.as("totalDays"),
                        ExpressionUtils.as(
                                JPAExpressions.select(planVacation.count)
                                .from(planVacation)
                                .where(planVacation.vacationId.eq(vacation.id), planVacation.status.eq("Y")),
                                "count"
                        )
                ))
                .from(vacation)
                .where(vacation.userInfo.id.eq(userId))
                .fetch();
    }
}