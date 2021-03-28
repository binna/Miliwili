package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.CalendarProvider;
import com.app.miliwili.src.calendar.models.Plan;
import com.app.miliwili.src.main.dto.GetUserCalendarMainRes;
import com.app.miliwili.src.main.dto.PlanData;
import com.app.miliwili.src.main.dto.UserMainData;
import com.app.miliwili.src.user.UserSelectRepository;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;


@RequiredArgsConstructor
@Service
public class MainProvider {
    private final UserSelectRepository userSelectRepository;
    private final CalendarProvider calendarProvider;
    private final JwtService jwtService;



    public UserMainData retrieveUserMainDataById() throws BaseException{
        try{
            UserMainData userMainData = userSelectRepository.findUserMainDataByUserId(jwtService.getUserId());

            if (Objects.isNull(userMainData)) {
                throw new BaseException(NOT_FOUND_MAIN_INFO);
            }
            return userMainData;
        } catch (BaseException exception) {
            if(exception.getStatus() == NOT_FOUND_MAIN_INFO) {
                throw new BaseException(NOT_FOUND_MAIN_INFO);
            }
            throw new BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        }
    }


    public GetUserCalendarMainRes getUserMainById() throws BaseException {
        UserMainData userMainData = retrieveUserMainDataById();
        List<Plan> plans = calendarProvider.retrievePlanListByToday();

        try {
            return changeMainDataToRes(userMainData, plans);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        }
    }

    /**
     * Converter
     * UserCalendarMainData -> GetUserCalendarMainRes
     */
    public GetUserCalendarMainRes changeMainDataToRes(UserMainData mainData, List<Plan> plans){
        if(mainData.getStateIdx() == 1) {
            return GetUserCalendarMainRes.builder()
                    .name(mainData.getName())
                    .profileImg(mainData.getProfileImg())
                    .birthday(Validation.isLocalDateAndChangeString(mainData.getBirthday()))
                    .stateIdx(mainData.getStateIdx())
                    .serveType(mainData.getServeType())
                    .startDate(mainData.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(mainData.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .strPrivate(mainData.getStrPrivate().format(DateTimeFormatter.ISO_DATE))
                    .strCorporal(mainData.getStrCorporal().format(DateTimeFormatter.ISO_DATE))
                    .strSergeant(mainData.getStrSergeant().format(DateTimeFormatter.ISO_DATE))
                    .hobong(mainData.getHobong())
                    .normalPromotionStateIdx(mainData.getNormalPromotionStateIdx())
                    .goal(mainData.getGoal())
                    .vacationTotalDays(Validation.isInteger(mainData.getVacationTotalDays()))
                    .vacationUseDays(Validation.isInteger(mainData.getVacationUseDays()) + Validation.isInteger(mainData.getVacationPlanUseDays()))
                    .plan(plans.stream().map(plan -> {
                        return PlanData.builder()
                                .planId(plan.getId())
                                .title(plan.getTitle())
                                .build();
                    }).collect(Collectors.toList()))
                    .build();
        }
        return GetUserCalendarMainRes.builder()
                .name(mainData.getName())
                .profileImg(mainData.getProfileImg())
                .birthday(Validation.isLocalDateAndChangeString(mainData.getBirthday()))
                .stateIdx(mainData.getStateIdx())
                .serveType(mainData.getServeType())
                .startDate(mainData.getStartDate().format(DateTimeFormatter.ISO_DATE))
                .endDate(mainData.getEndDate().format(DateTimeFormatter.ISO_DATE))
                .proDate(mainData.getProDate().toString())
                .goal(mainData.getGoal())
                .vacationTotalDays(Validation.isInteger(mainData.getVacationTotalDays()))
                .vacationUseDays(Validation.isInteger(mainData.getVacationUseDays()) + Validation.isInteger(mainData.getVacationPlanUseDays()))
                .plan(plans.stream().map(plan -> {
                    return PlanData.builder()
                            .planId(plan.getId())
                            .title(plan.getTitle())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
}
