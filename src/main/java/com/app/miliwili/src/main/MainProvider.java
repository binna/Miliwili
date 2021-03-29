package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.CalendarProvider;
import com.app.miliwili.src.main.dto.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.utils.ChineseCalendarUtil;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.FAILED_TO_GET_USER_MAIN_INFO;


@RequiredArgsConstructor
@Service
public class MainProvider {
    private final UserProvider userProvider;
    private final CalendarProvider calendarProvider;


    /**
     * 메인화면 조회
     * 
     * @return GetUserCalendarMainRes
     * @throws BaseException
     * @Auther shine
     */
    public GetUserCalendarMainRes getUserCalendarMainById() throws BaseException {
        UserMainData userMainData = userProvider.retrieveUserMainDataById();
        List<PlanMainData> planMainData = calendarProvider.retrievePlanMainDataByTodayAndStatusY();
        List<DDayMainDataRes> ddayMainDate = retrieveDDayMainDataResByTodaySortDateAsc();

        try {
            return changeGetUserCalendarMainRes(userMainData, planMainData, ddayMainDate);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        }
    }




    /**
     * 생일구분 날짜 계산(양력 -> 양력, 음력 -> 양력) 및 디데이 최신순으로 정렬
     *
     * @return List<DDayMainDataRes>
     * @throws BaseException
     * @Auther shine
     */
    public List<DDayMainDataRes> retrieveDDayMainDataResByTodaySortDateAsc() throws BaseException {
        List<DDayMainDataRes> ddayMainData = new ArrayList<>();

        for (DDayMainData dday : calendarProvider.retrieveDDayMainDataByTodayAndStatusY()) {
            if (dday.getDdayType().equals("생일")) {
                ddayMainData.add(DDayMainDataRes.builder()
                        .ddayId(dday.getId())
                        .date(ChineseCalendarUtil.convertSolar(dday.getDate().format(DateTimeFormatter.ISO_DATE).substring(5), dday.getChoiceCalendar()))
                        .title(dday.getTitle())
                        .build());
                continue;
            }
            ddayMainData.add(DDayMainDataRes.builder()
                    .ddayId(dday.getId())
                    .date(dday.getDate().format(DateTimeFormatter.ISO_DATE))
                    .title(dday.getTitle())
                    .build());
        }

        Collections.sort(ddayMainData, new dateSortAsc());

        return ddayMainData;
    }




    /**
     * GetUserCalendarMainRes 변환
     *
     * @param userMainData
     * @param planMainData
     * @param ddayMainDate
     * @return GetUserCalendarMainRes
     * @Auther shine
     */
    public GetUserCalendarMainRes changeGetUserCalendarMainRes(UserMainData userMainData, List<PlanMainData> planMainData, List<DDayMainDataRes> ddayMainDate) {
        if (userMainData.getStateIdx() == 1) {
            return GetUserCalendarMainRes.builder()
                    .name(userMainData.getName())
                    .profileImg(userMainData.getProfileImg())
                    .birthday(Validation.isLocalDateAndChangeString(userMainData.getBirthday()))
                    .stateIdx(userMainData.getStateIdx())
                    .serveType(userMainData.getServeType())
                    .startDate(userMainData.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(userMainData.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .strPrivate(userMainData.getStrPrivate().format(DateTimeFormatter.ISO_DATE))
                    .strCorporal(userMainData.getStrCorporal().format(DateTimeFormatter.ISO_DATE))
                    .strSergeant(userMainData.getStrSergeant().format(DateTimeFormatter.ISO_DATE))
                    .hobong(userMainData.getHobong())
                    .normalPromotionStateIdx(userMainData.getNormalPromotionStateIdx())
                    .goal(userMainData.getGoal())
                    .vacationTotalDays(Validation.isInteger(userMainData.getVacationTotalDays()))
                    .vacationUseDays(Validation.isInteger(userMainData.getVacationUseDays()) + Validation.isInteger(userMainData.getVacationPlanUseDays()))
                    .dday(ddayMainDate)
                    .plan(planMainData)
                    .build();
        }
        return GetUserCalendarMainRes.builder()
                .name(userMainData.getName())
                .profileImg(userMainData.getProfileImg())
                .birthday(Validation.isLocalDateAndChangeString(userMainData.getBirthday()))
                .stateIdx(userMainData.getStateIdx())
                .serveType(userMainData.getServeType())
                .startDate(userMainData.getStartDate().format(DateTimeFormatter.ISO_DATE))
                .endDate(userMainData.getEndDate().format(DateTimeFormatter.ISO_DATE))
                .proDate(userMainData.getProDate().toString())
                .goal(userMainData.getGoal())
                .vacationTotalDays(Validation.isInteger(userMainData.getVacationTotalDays()))
                .vacationUseDays(Validation.isInteger(userMainData.getVacationUseDays()) + Validation.isInteger(userMainData.getVacationPlanUseDays()))
                .dday(ddayMainDate)
                .plan(planMainData)
                .build();
    }
}




class dateSortAsc implements Comparator<DDayMainDataRes> {

    @Override
    public int compare(DDayMainDataRes o1, DDayMainDataRes o2) {
        LocalDate date1 = LocalDate.parse(o1.getDate(), DateTimeFormatter.ISO_DATE);
        LocalDate date2 = LocalDate.parse(o2.getDate(), DateTimeFormatter.ISO_DATE);

        if (date1.isBefore(date2)) {
            return 1;
        } else if (date1.isAfter(date2)) {
            return -1;
        } else {
            return 0;
        }
    }
}