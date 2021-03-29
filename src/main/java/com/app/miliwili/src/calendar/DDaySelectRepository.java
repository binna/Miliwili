package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.DDay;
import com.app.miliwili.src.calendar.models.QDDay;
import com.app.miliwili.src.main.dto.DDayMainData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DDaySelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public DDaySelectRepository(JPAQueryFactory queryFactory) {
        super(DDay.class);
        this.queryFactory = queryFactory;
    }

    /**
     * 유효한 모든 디데이 조회(메인조회용)
     *
     * @param userId
     * @return List<DDayMainData>
     * @Auther shine
     */
    public List<DDayMainData> findDDayByToday(Long userId) {
        QDDay dday = QDDay.dDay;

        return queryFactory
                .select(Projections.constructor(DDayMainData.class,
                        dday.id.as("id"), dday.ddayType.as("ddayType"), dday.title.as("title"),
                        dday.choiceCalendar.as("choiceCalendar"), dday.date.as("date")
                ))
                .from(dday)
                .where(dday.userInfo.id.eq(userId), dday.status.eq("Y"))
                .fetch();
    }
}