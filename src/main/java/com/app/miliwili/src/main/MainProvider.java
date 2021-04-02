package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.CalendarProvider;
import com.app.miliwili.src.main.dto.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.utils.ChineseCalendarUtil;
import com.app.miliwili.utils.JwtService;
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
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class MainProvider {
    private final UserProvider userProvider;
    private final CalendarProvider calendarProvider;
    private final JwtService jwtService;


    /**
     * 메인 유저, 캘린더 조회
     * 
     * @return GetUserCalendarMainRes
     * @throws BaseException
     * @Auther shine
     */
    public GetUserCalendarMainRes getUserCalendarMainById() throws BaseException {
        UserMainData userMainData = userProvider.retrieveUserMainDataById();

        Long userId = jwtService.getUserId();

        List<DDayMainDataRes> ddayMainDate = retrieveDDayMainDataResSortDateAsc(userId);
        List<PlanMainData> planMainData = calendarProvider.retrievePlanMainDataByDateAndStatusY(userId, LocalDate.now());

        try {
            return changeGetUserCalendarMainRes(userMainData, ddayMainDate, planMainData);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER_CALENDAR_MAIN);
        }
    }

    /**
     * 내 메인 캘린더 조회
     *
     * @return GetCalendarMainRes
     * @throws BaseException
     * @Auther Shine
     */
    public GetCalendarMainRes getCalendarMain() throws BaseException {
        String month = Validation.getCurrentMonth();

        Long userId = jwtService.getUserId();

        List<PlanCalendarData> planCalendarData = calendarProvider.retrievePlanCalendarDataByMonthAndStatusY(userId, month);
        List<DDayMainDataRes> ddayMainDate = retrieveDDayMainDataResSortDateAsc(userId);
        List<String> ddayCalendar = retrieveDDayCalendarByMonth(ddayMainDate, month);
        List<PlanMainData> planMainData = calendarProvider.retrievePlanMainDataByDateAndStatusY(userId, LocalDate.now());

        return changeGetCalendarMainRes(planCalendarData, ddayCalendar, ddayMainDate, planMainData);
    }

    /**
     * 월별 내 메인 캘린더 조회
     *
     * @param month
     * @return GetMonthCalendarMainRes
     * @throws BaseException
     * @Auther shine
     */
    public GetMonthCalendarMainRes getCalendarMainFromMonth(String month) throws BaseException {
        month = month.substring(0, 4) + "-" + month.substring(4, 6);

        Long userId = jwtService.getUserId();

        List<PlanCalendarData> planCalendarData = calendarProvider.retrievePlanCalendarDataByMonthAndStatusY(userId, month);
        List<DDayMainDataRes> ddayMainDate = retrieveDDayMainDataResSortDateAsc(userId);

        List<String> ddayCalendar = retrieveDDayCalendarByMonth(ddayMainDate, month);

        return GetMonthCalendarMainRes.builder()
                .planCalendar(changeListPlanCalendarDataToListPlanCalendarDataRes(planCalendarData))
                .ddayCalendar(ddayCalendar)
                .build();
    }

    /**
     * 일별 내 메인 캘린더 조회
     *
     * @return GetDateCalendarMainRes
     * @throws BaseException
     * @Auther shine
     */
    public GetDateCalendarMainRes getCalendarMainFromDate(String date) throws BaseException {
        date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);

        Long userId = jwtService.getUserId();

        List<DDayMainDataRes> ddayMainDate = retrieveDDayMainDataResSortDateAsc(userId);
        List<PlanMainData> planMainData = calendarProvider.retrievePlanMainDataByDateAndStatusY(userId, LocalDate.parse(date, DateTimeFormatter.ISO_DATE));

        return GetDateCalendarMainRes.builder()
                .dday(ddayMainDate)
                .plan(planMainData)
                .build();
    }




    private List<DDayMainDataRes> retrieveDDayMainDataResSortDateAsc(Long userId) throws BaseException {
        List<DDayMainDataRes> ddayMainData = new ArrayList<>();

        for (DDayMainData dday : calendarProvider.retrieveDDayMainDataByTodayAndStatusY(userId)) {
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

    private List<String> retrieveDDayCalendarByMonth(List<DDayMainDataRes> ddayMainDate, String month) {
        List<String> ddayCalendar = new ArrayList<>();

        LocalDate startDate = LocalDate.parse((month + "-01"), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        for (DDayMainDataRes ddayMain : ddayMainDate) {
            LocalDate targetDate = LocalDate.parse(ddayMain.getDate(), DateTimeFormatter.ISO_DATE);

            if ((targetDate.isEqual(startDate) || targetDate.isAfter(startDate)) && (targetDate.isEqual(endDate) || targetDate.isBefore(endDate))) {
                ddayCalendar.add(targetDate.format(DateTimeFormatter.ISO_DATE));
            }
        }

        return ddayCalendar;
    }

    private List<PlanCalendarDataRes> changeListPlanCalendarDataToListPlanCalendarDataRes(List<PlanCalendarData> planCalendarDataList) {
        return planCalendarDataList.stream().map(planCalendarData -> {
            return PlanCalendarDataRes.builder()
                    .color(planCalendarData.getColor())
                    .startDate(planCalendarData.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(planCalendarData.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .build();
        }).collect(Collectors.toList());

    }

    private GetCalendarMainRes changeGetCalendarMainRes(List<PlanCalendarData> planCalendarData, List<String> ddayCalendar, List<DDayMainDataRes> ddayMainDate, List<PlanMainData> planMainData) {
        return GetCalendarMainRes.builder()
                .planCalendar(changeListPlanCalendarDataToListPlanCalendarDataRes(planCalendarData))
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