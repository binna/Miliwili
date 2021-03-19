package com.app.miliwili.src.user;

import com.app.miliwili.src.user.dto.GetAbnormalUserEndDate;
import com.app.miliwili.src.user.models.QAbnormalPromotionState;
import com.app.miliwili.src.user.models.QUser;
import com.app.miliwili.src.user.models.User;
import com.querydsl.core.types.Projections;
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
        super(User.class);
        this.queryFactory=queryFactory;
    }

    /**
     * 구글 아이디로 회원 아이디(pk,id)값 검사
     * return: id
     */
    public List<Long> findUsersIdByGoogleId(String gsocialId) {
        QUser user = QUser.user;
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
        QUser user = QUser.user;
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
        QUser user = QUser.user;
        QAbnormalPromotionState abnormal = QAbnormalPromotionState.abnormalPromotionState;

        return queryFactory.select((Projections.constructor(GetAbnormalUserEndDate.class,
                user.id, user.profileImg, user.name, user.endDate,
                user.startDate,abnormal.proDate,user.goal)))
                .from(user)
                .where(user.status.eq("Y"), user.id.eq(userId))
                .join(abnormal)
                .on(abnormal.user.id.eq(user.id))   //???왜래키라면???
                .fetch();

    }


    /**
     * user정보 찾기
     */


}
