package com.app.miliwili.src.user;

import com.app.miliwili.src.calendar.models.QPlanVacation;
import com.app.miliwili.src.main.dto.*;
import com.app.miliwili.src.user.models.*;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserSelectRepository(JPAQueryFactory queryFactory){
        super(UserInfo.class);
        this.queryFactory=queryFactory;
    }

    /**
     * 구글 아이디로 회원 아이디(pk,id)값 검사
     * return: id
     */
    public List<Long> findUsersIdByGoogleId(String gsocialId) {
        QUserInfo user = QUserInfo.userInfo;
        return queryFactory.select((Projections.constructor(Long.class,
                user.id)))
                .from(user)
                .where(user.socialId.eq(gsocialId), user.socialType.eq("G"), user.status.eq("Y"))
                .fetch();
    }

    /**
     * UserMain Date 조회
     * 
     * @param userId
     * @return UserMainData
     * @Auther shine
     */
    public UserMainData findUserMainDataByUserId(Long userId) {
        QUserInfo user = QUserInfo.userInfo;
        QNormalPromotionState normalPromotionState = QNormalPromotionState.normalPromotionState;
        QAbnormalPromotionState abnormalPromotionState = QAbnormalPromotionState.abnormalPromotionState;
        QVacation vacation = QVacation.vacation;
        QPlanVacation planVacation = QPlanVacation.planVacation;

        return queryFactory
                .select((Projections.constructor(UserMainData.class,
                        user.name.as("name"), user.profileImg.as("profileImg"), user.birthday.as("birthday"),
                        user.stateIdx.as("stateIdx"), user.serveType.as("serveType"),
                        user.startDate.as("startDate"), user.endDate.as("endDate"),
                        normalPromotionState.firstDate.as("strPrivate"),
                        normalPromotionState.secondDate.as("strCorporal"),
                        normalPromotionState.thirdDate.as("strSergeant"),
                        normalPromotionState.hobong.as("hobong"),
                        normalPromotionState.stateIdx.as("normalPromotionStateIdx"),
                        abnormalPromotionState.proDate.as("proDate"),
                        user.goal.as("goal"),
                        ExpressionUtils.as(
                                JPAExpressions.select(vacation.totalDays.sum())
                                .from(vacation)
                                .where(vacation.userInfo.id.eq(userId)),
                                "vacationTotalDays"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(vacation.useDays.sum())
                                .from(vacation)
                                .where(vacation.userInfo.id.eq(userId)),
                                "vacationUseDays"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(planVacation.count.sum())
                                .from(planVacation)
                                .where(planVacation.plan.userInfo.id.eq(userId), planVacation.status.eq("Y")),
                                "vacationPlanUseDays"
                        )
                )))
                .from(user)
                .leftJoin(user.normalPromotionState, normalPromotionState)
                .leftJoin(user.abnormalPromotionState, abnormalPromotionState)
                .where(user.status.eq("Y"), user.id.eq(userId))
                .fetchOne();
    }
}