package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class UserSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserSelectRepository(JPAQueryFactory queryFactory){
        super(User.class);
        this.queryFactory=queryFactory;
    }
}
