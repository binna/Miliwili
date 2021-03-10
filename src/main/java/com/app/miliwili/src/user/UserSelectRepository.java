package com.app.miliwili.src.user;

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
}
