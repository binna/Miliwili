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
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class MainProvider {
    private final UserProvider userProvider;
    private final CalendarProvider calendarProvider;


    /**
     * 메인 유저, 캘린더 조회
     * 
     * @return GetUserCalendarMainRes
     * @throws BaseException
     * @Auther shine
     */
    public GetUserCalendarMainRes getUserCalendarMainById() throws BaseException {
        UserMainData userMainData = userProvider.retrieveUserMainDataById();

        List<DDayMainDataRes> ddayMainDate = retrieveDDayMainDataResSortDateAsc();
        List<PlanMainData> planMainData = calendarProvider.retrievePlanMainDataByDateAndStatusY(LocalDate.now());

        try {
            return changeGetUserCalendarMainRes(userMainData, ddayMainDate, planMainData);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        }
    }

    /**
     * 메인 캘린더 조회
     *
     * @param month
     * @param date
     * @return GetCalendarMainRes
     * @throws BaseException
     */
    public GetCalendarMainRes getCalendarMain(String month, String date) throws BaseException {
        month = month.substring(0, 4) + "-" + month.substring(4, 6);

        LocalDate start = LocalDate.parse((month + "-01"), DateTimeFormatter.ISO_DATE);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());

        List<PlanCalendarData> planCalendarData = calendarProvider.retrievePlanCalendarDataByMonthAndStatusY(start, end);
        List<DDayMainDataRes> ddayMainDate = retrieveDDayMainDataResSortDateAsc();

        List<String> ddayCalendar = retrieveDDayCalendarByMonth(ddayMainDate, start, end);
        List<PlanMainData> planMainData = calendarProvider.retrievePlanMainDataByDateAndStatusY(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));

        return changeGetCalendarMainRes(planCalendarData, ddayCalendar, ddayMainDate, planMainData);
    }




    private List<DDayMainDataRes> retrieveDDayMainDataResSortDateAsc() throws BaseException {
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
        Collections.sort(ddayMainData, new dateSortDesc());
        return ddayMainData;
    }

    private List<String> retrieveDDayCalendarByMonth(List<DDayMainDataRes> ddayMainDate, LocalDate startDate, LocalDate endDate) {
        List<String> ddayCalendar = new ArrayList<>();

        for (DDayMainDataRes ddayMain : ddayMainDate) {
            LocalDate targetDate = LocalDate.parse(ddayMain.getDate(), DateTimeFormatter.ISO_DATE);

            if ((targetDate.isEqual(startDate) || targetDate.isAfter(startDate)) && (targetDate.isEqual(endDate) || targetDate.isBefore(endDate))) {
                ddayCalendar.add(targetDate.format(DateTimeFormatter.ISO_DATE));
            }
        }

        return ddayCalendar;
    }

    private GetCalendarMainRes changeGetCalendarMainRes(List<PlanCalendarData> planCalendarData, List<String> ddayCalendar, List<DDayMainDataRes> ddayMainDate, List<PlanMainData> planMainData) {
        return GetCalendarMainRes.builder()
                .planCalendar(planCalendarData)
                .ddayCalendar(ddayCalendar)
                .dday(ddayMainDate)
                .plan(planMainData)
                .build();
    }

    private GetUserCalendarMainRes changeGetUserCalendarMainRes(UserMainData userMainData, List<DDayMainDataRes> ddayMainDate, List<PlanMainData> planMainData) {
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




class dateSortDesc implements Comparator<DDayMainDataRes> {
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