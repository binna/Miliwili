package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.*;
import com.app.miliwili.src.calendar.models.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class CalendarService {
    private final PlanRepository planRepository;
    private final PlanWorkRepository planWorkRepository;
    private final PlanDiaryRepository planDiaryRepository;
    private final DDayRepository ddayRepository;
    private final DDayWorkRepository ddayWorkRepository;
    private final DDayDiaryRepository ddayDiaryRepository;
    private final JwtService jwtService;
    private final CalendarProvider calendarProvider;
    private final UserProvider userProvider;


    /**
     * 일정 생성
     *
     * @param parameters
     * @return PlanRes
     * @throws BaseException
     * @Auther shine
     */
    public PlanRes createPlan(PostPlanReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        Plan newPlan = Plan.builder()
                .color(parameters.getColor())
                .planType(parameters.getPlanType())
                .title(parameters.getTitle())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .userInfo(user)
                .build();
        setPushMessage(parameters.getPush(), parameters.getPushDeviceToken(), newPlan);
        setPlanVacation(newPlan.getPlanType(), parameters.getPlanVacation(), newPlan);
        setWorks(parameters.getWork(), newPlan);
        newPlan.setPlanDiaries(getPlanDiaryData(newPlan));

        try {
            Plan savedPlan = planRepository.save(newPlan);
            return PlanRes.builder()
                    .planId(savedPlan.getId())
                    .color(savedPlan.getColor())
                    .planType(savedPlan.getPlanType())
                    .title(savedPlan.getTitle())
                    .startDate(savedPlan.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedPlan.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .push(savedPlan.getPush())
                    .planVacation(calendarProvider.changeSetPlanVacationToListPlanVacationRes(savedPlan.getPlanVacations()))
                    .work(calendarProvider.changeListPlanWorkToListWorkRes(savedPlan.getPlanWorks()))
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_PLAN);
        }
    }

    /**
     * 일정수정
     *
     * @param parameters
     * @param planId
     * @return PlanRes
     * @throws BaseException
     * @Auther shine
     */
    public PlanRes updatePlan(PatchPlanReq parameters, Long planId) throws BaseException {
        Plan plan = calendarProvider.retrievePlanByIdAndStatusY(planId);

        if (plan.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        LocalDate startDate = LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE);

        plan.setColor(parameters.getColor());
        plan.setTitle(parameters.getTitle());
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);

        setPushMessage(parameters.getPush(), parameters.getPushDeviceToken(), plan);
        setPlanVacation(parameters.getPlanVacation(), plan);
        setPlanWork(parameters.getWork(), plan);

        try {
            Plan savedPlan = planRepository.save(plan);
            return PlanRes.builder()
                    .planId(savedPlan.getId())
                    .color(savedPlan.getColor())
                    .planType(savedPlan.getPlanType())
                    .title(savedPlan.getTitle())
                    .startDate(savedPlan.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedPlan.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .push(savedPlan.getPush())
                    .planVacation(calendarProvider.changeSetPlanVacationToListPlanVacationRes(savedPlan.getPlanVacations()))
                    .work(calendarProvider.changeListPlanWorkToListWorkRes(savedPlan.getPlanWorks()))
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_PLAN);
        }
    }

    /**
     * 일정삭제
     *
     * @param planId
     * @throws BaseException
     * @Auther shine
     */
    public void deletePlan(Long planId) throws BaseException {
        Plan plan = calendarProvider.retrievePlanByIdAndStatusY(planId);

        if (plan.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        plan.setStatus("N");
        setPlanVacationStatusN(plan);

        try {
            planRepository.save(plan);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_PLAN);
        }
    }

    /**
     * 일정 다이어리 수정
     *
     * @param parameters
     * @param diaryId
     * @return DiaryRes
     * @throws BaseException
     * @Auther shine
     */
    public DiaryRes updatePlanDiary(DiaryReq parameters, Long diaryId) throws BaseException {
        PlanDiary diary = calendarProvider.retrievePlanDiaryById(diaryId);

        if (diary.getPlan().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        diary.setContent(parameters.getContent());

        try {
            PlanDiary savedDiary = planDiaryRepository.save(diary);
            return DiaryRes.builder()
                    .diaryId(savedDiary.getId())
                    .date(savedDiary.getDate().format(DateTimeFormatter.ISO_DATE))
                    .title(savedDiary.getDate().format(DateTimeFormatter.ofPattern("MM월 dd일")))
                    .content(savedDiary.getContent())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_DIARY);
        }
    }


    /**
     * 할일 완료 -> 미완료, 미완료 -> 완료 처리
     *
     * @param workId
     * @return WorkRes
     * @throws BaseException
     */
    public WorkRes updatePlanWork(Long workId) throws BaseException {
        PlanWork work = calendarProvider.retrievePlanWorkById(workId);

        if (work.getPlan().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        setPlanWorkToggleProcessingStatus(work);

        try {
            PlanWork savedWork = planWorkRepository.save(work);
            return WorkRes.builder()
                    .workId(savedWork.getId())
                    .content(savedWork.getContent())
                    .processingStatus(savedWork.getProcessingStatus())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_WORK);
        }
    }


    /**
     * D-Day 생성
     *
     * @param parameters
     * @return PostDDayRes
     * @throws BaseException
     * @Auther shine
     */
    public DDayRes createDDay(PostDDayReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        DDay newDDay = DDay.builder()
                .ddayType(parameters.getDdayType())
                .title(parameters.getTitle())
                .date(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE))
                .userInfo(user)
                .build();
        setSubTitle(parameters.getSubTitle(), newDDay);
        setChoiceCalendar(newDDay, parameters.getDdayType(), parameters.getChoiceCalendar());
        setLinkOrPlaceOrWork(parameters.getDdayType(), parameters.getLink(), parameters.getPlaceLat(), parameters.getPlaceLon(), parameters.getWork(), newDDay);
        newDDay.setDdayDiaries(getDDayDiaries(newDDay));

        //System.out.println("음력을 양력으로---------------------------------" + ChineseCalendarUtil.convertLunarToSolar("20210319"));
        //System.out.println("양력을 음력으로---------------------------------" + ChineseCalendarUtil.converSolarToLunar("20210319"));

        try {
            DDay savedDDay = ddayRepository.save(newDDay);
            return DDayRes.builder()
                    .ddayId(savedDDay.getId())
                    .ddayType(savedDDay.getDdayType())
                    .title(savedDDay.getTitle())
                    .subtitle(savedDDay.getSubtitle())
                    .date(savedDDay.getDate().format(DateTimeFormatter.ISO_DATE))
                    .link(savedDDay.getLink())
                    .choiceCalendar(Validation.isString(savedDDay.getChoiceCalendar()))
                    .placeLat(Validation.isBigDecimal(savedDDay.getPlaceLat()))
                    .placeLon(Validation.isBigDecimal(savedDDay.getPlaceLon()))
                    .work(calendarProvider.changeListDDayWorkToListWorkRes(savedDDay.getDdayWorks()))
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_D_DAY);
        }
    }

    /**
     * D-Day 수정
     * 
     * @param parameters
     * @param ddayId
     * @return DDayRes
     * @throws BaseException
     * @Auther shine
     */
    public DDayRes updateDDay(PatchDDayReq parameters, Long ddayId) throws BaseException {
        DDay dday = calendarProvider.retrieveDDayByIdAndStatusY(ddayId);

        if (dday.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        checkValidationOfInputValuesByddayType(parameters, dday);

        dday.setTitle(parameters.getTitle());
        setSubTitle(parameters.getSubTitle(), dday);
        dday.setDate(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE));
        setChoiceCalendar(dday, dday.getDdayType(), parameters.getChoiceCalendar());
        setLinkOrPlaceOrWork(parameters.getLink(), parameters.getPlaceLat(), parameters.getPlaceLon(), parameters.getWork(), dday);

        try {
            DDay savedDDay = ddayRepository.save(dday);
            return DDayRes.builder()
                    .ddayId(savedDDay.getId())
                    .ddayType(savedDDay.getDdayType())
                    .title(savedDDay.getTitle())
                    .subtitle(savedDDay.getSubtitle())
                    .date(savedDDay.getDate().format(DateTimeFormatter.ISO_DATE))
                    .link(savedDDay.getLink())
                    .choiceCalendar(Validation.isString(savedDDay.getChoiceCalendar()))
                    .placeLat(Validation.isBigDecimal(savedDDay.getPlaceLat()))
                    .placeLon(Validation.isBigDecimal(savedDDay.getPlaceLon()))
                    .work(calendarProvider.changeListDDayWorkToListWorkRes(savedDDay.getDdayWorks()))
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_D_DAY);
        }
    }



    /**
     * D-Day 삭제
     * @param ddayId
     * @throws BaseException
     * @Auther shine
     */
    public void deleteDDay(Long ddayId) throws BaseException {
        DDay dday = calendarProvider.retrieveDDayByIdAndStatusY(ddayId);

        if (dday.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        dday.setStatus("N");

        try {
            ddayRepository.save(dday);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_D_DAY);
        }
    }


    /**
     * D-Day 다이어리 생성
     *
     * @param parameters
     * @param ddayId
     * @return PostDiaryRes
     * @throws BaseException
     */
    public DiaryRes createDDayDiary(DiaryReq parameters, Long ddayId) throws BaseException {
        DDay dday = calendarProvider.retrieveDDayByIdAndStatusY(ddayId);

        if (dday.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        DDayDiary newDiary = DDayDiary.builder()
                .date(LocalDate.parse(LocalDate.now().getYear() + "-" + dday.getDate().format(DateTimeFormatter.ISO_DATE).substring(5)))
                .content(parameters.getContent())
                .dday(dday)
                .build();

        if(!dday.getDdayType().equals("생일") && !dday.getDate().isEqual(newDiary.getDate())) {
            throw new BaseException(OUT_OF_BOUNDS_DATE_DIARY);
        }

        checkDDayDiaryDay(newDiary.getDate().format(DateTimeFormatter.ISO_DATE), dday);

        try {
            DDayDiary savedDiary = ddayDiaryRepository.save(newDiary);
            return calendarProvider.changeDDayDiaryToDiaryRes(savedDiary);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_DIARY);
        }
    }

    /**
     * D-Day 다이어리 수정
     * 
     * @param parameters
     * @param diaryId
     * @return DiaryRes
     * @throws BaseException
     */
    public DiaryRes updateDDayDiary(DiaryReq parameters, Long diaryId) throws BaseException {
        DDayDiary diary = calendarProvider.retrieveDDayDiaryById(diaryId);

        if (diary.getDday().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        diary.setContent(parameters.getContent());

        try {
            DDayDiary savedDiary = ddayDiaryRepository.save(diary);
            return calendarProvider.changeDDayDiaryToDiaryRes(savedDiary);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_DIARY);
        }
    }


    /**
     * 준비물 준비 완료 -> 미완료, 미완료 -> 완료로 처리
     *
     * @param workId
     * @return WorkRes
     * @throws BaseException
     * @Auther shine
     */
    public WorkRes updateDDayWork(Long workId) throws BaseException {
        DDayWork work = calendarProvider.retrieveDDayWorkById(workId);

        if (work.getDday().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        setDDayWorkToggleProcessingStatus(work);

        try {
            DDayWork savedWork = ddayWorkRepository.save(work);
            return WorkRes.builder()
                    .workId(savedWork.getId())
                    .content(savedWork.getContent())
                    .processingStatus(savedWork.getProcessingStatus())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_WORK);
        }
    }




    private void checkValidationOfInputValuesByddayType(PatchDDayReq parameters, DDay dday) throws BaseException {
        if (dday.getDdayType().equals("생일")) {
            if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
                throw new BaseException(EMPTY_DATE);
            }
            if (!Validation.isRegexBirthdayDate(parameters.getDate())) {
                throw new BaseException(INVALID_DATE);
            }
            if (!(parameters.getChoiceCalendar().equals("S") || parameters.getChoiceCalendar().equals("L"))) {
                throw new BaseException(MUST_ENTER_CHOICE_CALENDAR_S_OR_B);
            }
            if (Objects.nonNull(parameters.getLink())
                    || Objects.nonNull(parameters.getPlaceLat()) || Objects.nonNull(parameters.getPlaceLon())
                    || Objects.nonNull(parameters.getWork())) {
                throw new BaseException(NOT_ENTER_LINK_PLACE_WORK);
            }
        } else if (dday.equals("기념일")) {
            if (Objects.nonNull(parameters.getLink())
                    || Objects.nonNull(parameters.getPlaceLat()) || Objects.nonNull(parameters.getPlaceLon())
                    || Objects.nonNull(parameters.getWork())) {
                throw new BaseException(NOT_ENTER_LINK_PLACE_WORK);
            }
            if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
                throw new BaseException(EMPTY_DATE);
            }
            if (!Validation.isRegexDate(parameters.getDate())) {
                throw new BaseException(INVALID_DATE);
            }
        } else if (dday.equals("자격증") || dday.equals("수능")) {
            if (Objects.isNull(parameters.getDate()) || parameters.getDate().length() == 0) {
                throw new BaseException(EMPTY_DATE);
            }
            if (!Validation.isRegexDate(parameters.getDate())) {
                throw new BaseException(INVALID_DATE);
            }
        }
    }

    private void setDDayWorkToggleProcessingStatus(DDayWork work) {
        if (work.getProcessingStatus().equals("T")) {
            work.setProcessingStatus("F");
        }
        if (work.getProcessingStatus().equals("F")) {
            work.setProcessingStatus("T");
        }
    }

    private void setPlanWorkToggleProcessingStatus(PlanWork work) {
        if (work.getProcessingStatus().equals("T")) {
            work.setProcessingStatus("F");
        }
        if (work.getProcessingStatus().equals("F")) {
            work.setProcessingStatus("T");
        }
    }

    private void checkDDayDiaryDay(String date, DDay dday) throws BaseException {
        for (DDayDiary diary : dday.getDdayDiaries()) {
            if(diary.getDate().isEqual(LocalDate.parse(date, DateTimeFormatter.ISO_DATE))) {
                throw new BaseException(ALREADY_EXIST_DIARY);
            }
        }
    }
    
    private void setPlanVacationStatusN(Plan plan) {
        for (PlanVacation planVacation : plan.getPlanVacations()) {
            planVacation.setStatus("N");
        }
    }

    private void setLinkOrPlaceOrWork(String link, BigDecimal placeLat, BigDecimal placeLon, List<WorkReq> work, DDay dday) {
        if(dday.getDdayType().equals("자격증") || dday.getDdayType().equals("수능")) {
            if(Objects.nonNull(link)) {
                dday.setLink(link);
            }
            if(Objects.nonNull(placeLat) && Objects.nonNull(placeLon)) {
                dday.setPlaceLat(placeLat);
                dday.setPlaceLon(placeLon);
            }
            if(Objects.nonNull(work)) {
                dday.getDdayWorks().clear();
                dday.getDdayWorks().addAll(calendarProvider.changeListWorkReqToListDDayWork(work, dday));
            }
        }
    }

    private void setPlanWork(List<WorkReq> work, Plan plan) {
        if (Objects.nonNull(work)) {
            plan.getPlanWorks().clear();
            plan.getPlanWorks().addAll(calendarProvider.changeListWorkReqToListPlanWork(work, plan));
        }
    }

    private void setPlanVacation(List<PlanVacationReq> planVacation, Plan plan) throws BaseException {
        if (plan.getPlanType().equals("휴가")) {
            int sum = 0;
            for (PlanVacationReq planVacationCount : planVacation) {
                sum += planVacationCount.getCount();
            }
            if(sum > ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate())) {
                throw new BaseException(NOT_BE_GREATER_THAN_TOTAL_DAYS);
            }
            if(sum < ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate())) {
                throw new BaseException(NOT_BE_LESS_THAN_USE_DAYS);
            }

            plan.getPlanVacations().clear();
            plan.getPlanVacations().addAll(calendarProvider.changeListPlanVacationReqToSetPlanVacation(planVacation, plan));
        }
    }

    private Set<DDayDiary> getDDayDiaries(DDay dday) {
        if(dday.getDdayType().equals("생일")) return null;
        return Set.of(DDayDiary.builder().date(dday.getDate()).dday(dday).build());
    }

    private Set<PlanDiary> getPlanDiaryData(Plan plan) {
        Set<PlanDiary> diaryList = new HashSet<>();

        int day =  (int) ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate());
        LocalDate targetDate = plan.getStartDate();

        for(int i = 0; i <= day; i++) {
            diaryList.add(PlanDiary.builder().date(targetDate).plan(plan).build());
            targetDate = targetDate.plusDays(1);
        }

        return diaryList;
    }

    private void setLinkOrPlaceOrWork(String ddayType, String link, BigDecimal placeLat, BigDecimal placeLon, List<WorkReq> work, DDay dday) {
        if(ddayType.equals("자격증") || ddayType.equals("수능")) {
            if(Objects.nonNull(link)) {
                dday.setLink(link);
            }
            if(Objects.nonNull(placeLat) && Objects.nonNull(placeLon)) {
                dday.setPlaceLat(placeLat);
                dday.setPlaceLon(placeLon);
            }
            if(Objects.nonNull(work)) {
                dday.setDdayWorks(calendarProvider.changeListWorkReqToListDDayWork(work, dday));
            }
        }
    }

    private void setChoiceCalendar(DDay dday, String ddayType, String choiceCalendar) {
        if (ddayType.equals("생일")) {
            dday.setChoiceCalendar(choiceCalendar);
        }
    }

    private void setSubTitle(String subTitle, DDay dday) {
        if(Objects.nonNull(subTitle)) {
            dday.setSubtitle(subTitle);
        }
    }

    private void setWorks(List<WorkReq> work, Plan plan) {
        if (Objects.nonNull(work)) {
            plan.setPlanWorks(calendarProvider.changeListWorkReqToListPlanWork(work, plan));
        }
    }

    private void setPlanVacation(String planType, List<PlanVacationReq> planVacation, Plan plan) throws BaseException {
        if (planType.equals("휴가")) {
            int sum = 0;
            for (PlanVacationReq planVacationCount : planVacation) {
                sum += planVacationCount.getCount();
            }
            if(sum > ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate())) {
                throw new BaseException(NOT_BE_GREATER_THAN_TOTAL_DAYS);
            }
            if(sum < ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate())) {
                throw new BaseException(NOT_BE_LESS_THAN_USE_DAYS);
            }

            plan.setPlanVacations(calendarProvider.changeListPlanVacationReqToSetPlanVacation(planVacation, plan));
        }
    }

    private void setPushMessage(String push, String pushDeviceToken, Plan plan) {
        if (Objects.nonNull(push) && push.equals("T")) {
            plan.setPush(push);
            plan.setPushDeviceToken(pushDeviceToken);
        }
    }
}