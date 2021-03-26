package com.app.miliwili.src.user;

import com.app.miliwili.src.calendar.models.QPlanVacation;
import com.app.miliwili.src.main.dto.*;
import com.app.miliwili.src.user.dto.*;
import com.app.miliwili.src.user.models.*;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.tomcat.util.ExceptionUtils;
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
     * stateIdx 조회
     */
    public List<Integer> findUserStateIdxByUserId(long userId){
        QUserInfo user = QUserInfo.userInfo;
        return queryFactory.select((Projections.constructor(Integer.class,
                user.stateIdx)))
                .from(user)
                .where(user.status.eq("Y"), user.id.eq(userId))
                .fetch();
    }

    /**
     * 전역일 계산을 하기 위해
     * --> mainProvider
     */
    public List<GetAbnormalUserEndDate> findEndDateInfoByUserId(long userId){
        QUserInfo user = QUserInfo.userInfo;
        QAbnormalPromotionState abnormal = QAbnormalPromotionState.abnormalPromotionState;

        return queryFactory.select((Projections.constructor(GetAbnormalUserEndDate.class,
                user.id, user.profileImg, user.name, user.endDate,
                user.startDate,abnormal.proDate,user.goal)))
                .from(user)
                .where(user.status.eq("Y"), user.id.eq(userId))
                .join(abnormal)
                .on(abnormal.userInfo.id.eq(user.id))   //???왜래키라면???
                .fetch();
    }


    /**
     *
     * @param userId
     * @return
     */
    public UserCalendarMainData findtest(Long userId) {
        QUserInfo user = QUserInfo.userInfo;
        QNormalPromotionState normalPromotionState = QNormalPromotionState.normalPromotionState;
        QAbnormalPromotionState abnormalPromotionState = QAbnormalPromotionState.abnormalPromotionState;
        QVacation vacation = QVacation.vacation;
        QPlanVacation planVacation = QPlanVacation.planVacation;

        return queryFactory
                .select((Projections.constructor(UserCalendarMainData.class,
                        user.name.as("name"), user.profileImg.as("profileImg"), user.birthday.as("birthday"),
                        user.stateIdx.as("stateIdx"), user.serveType.as("serveType"),
                        user.startDate.as("startDate"), user.endDate.as("endDate"),
                        normalPromotionState.firstDate.as("strPrivate"),
                        normalPromotionState.secondDate.as("strCorporal"),
                        normalPromotionState.thirdDate.as("strSergeant"),
                        normalPromotionState.hobong.as("hobong"),
                        normalPromotionState.stateIdx.as("normalPromotionStateIdx"),
                        abnormalPromotionState.proDate.as("proDate"),
                        user.goal.as("goal")
//                        ExpressionUtils.as(
//                                JPAExpressions.select(vacation.totalDays)
//                                .from(vacation)
//                                .where(vacation.userInfo.id.eq(userId)),
//                                "vacationTotalDays"
//                        ),
//                        ExpressionUtils.as(
//                                JPAExpressions.select(vacation.useDays)
//                                .from(vacation)
//                                .where(vacation.userInfo.id.eq(userId)),
//                                "vacationUseDays"
//                        ),
//
//                        ExpressionUtils.as(
//                                JPAExpressions.select(planVacation.count)
//                                .from(planVacation)
//                                .where(planVacation.vacationId.eq(vacation.id), planVacation.status.eq("Y")),
//                                "vacationPlanUseDays"
//                        )
                )))
                .from(user)
                .leftJoin(user.normalPromotionState, normalPromotionState)
                .leftJoin(user.abnormalPromotionState,abnormalPromotionState)
                .where(user.status.eq("Y"), user.id.eq(userId))
                .fetchOne();
    }



}