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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
     * @return PostPlanRes
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public PostPlanRes createPlan(PostPlanReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        Plan newPlan = Plan.builder()
                .color(parameters.getColor())
                .planType(parameters.getPlanType())
                .title(parameters.getTitle())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .userInfo(user)
                .build();

        if (Objects.nonNull(parameters.getPush()) && parameters.getPush().equals("Y")) {
            newPlan.setPush(parameters.getPush());
            newPlan.setPushDeviceToken(parameters.getPushDeviceToken());
        }

        if (newPlan.getPlanType().equals("휴가")) {
            newPlan.setPlanVacations(calendarProvider.changeListPlanVacationReqToSetPlanVacation(parameters.getPlanVacation(), newPlan));
        }

        if (Objects.nonNull(parameters.getWork())) {
            newPlan.setPlanWorks(calendarProvider.changeListWorkReqToListPlanWork(parameters.getWork(), newPlan));
        }

        try {
            Plan savedPlan = planRepository.save(newPlan);
            return PostPlanRes.builder()
                    .planId(savedPlan.getId())
                    .color(savedPlan.getColor())
                    .planType(savedPlan.getPlanType())
                    .title(savedPlan.getTitle())
                    .startDate(savedPlan.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedPlan.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .push(savedPlan.getPush())
                    .planVacation(calendarProvider.changeSetPlanVacationToListPlanVacationRes(savedPlan.getPlanVacations()))
                    .work(calendarProvider.changeListToDoListToListWorkRes(savedPlan.getPlanWorks()))
                    .build();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(FAILED_TO_POST_PLAN);
        }
    }

    /**
     * 일정수정
     *
     * @param parameters
     * @param planId
     * @return PatchPlanRes
     * @throws BaseException
     * @Auther shine
     */
    public PatchPlanRes updatePlan(PatchPlanReq parameters, Long planId) throws BaseException {
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

        if (plan.getPlanType().equals("면회") || plan.getPlanType().equals("외출")
                || plan.getPlanType().equals("전투휴무") || plan.getPlanType().equals("당직")) {
            if (startDate.isEqual(endDate)) {
                throw new BaseException(ONLY_ON_THE_SAME_DAY);
            }
        } else if (plan.getPlanType().equals("일정") ||
                plan.getPlanType().equals("휴가") || plan.getPlanType().equals("외박")) {
            if (startDate.isBefore(endDate)) {
                throw new BaseException(FASTER_THAN_CALENDAR_START_DATE);
            }
        } else {
            throw new BaseException(INVALID_PLAN_TYPE);
        }

        if (Objects.nonNull(parameters.getPush()) && parameters.getPush().equals("Y")) {
            plan.setPush(parameters.getPush());
            plan.setPushDeviceToken(parameters.getPushDeviceToken());
        }

        if (plan.getPlanType().equals("휴가")) {
            plan.getPlanVacations().clear();
            plan.getPlanVacations().addAll(calendarProvider.changeListPlanVacationReqToSetPlanVacation(parameters.getPlanVacation(), plan));
        }

        if (Objects.nonNull(parameters.getWork())) {
            plan.getPlanWorks().clear();
            plan.getPlanWorks().addAll(calendarProvider.changeListWorkReqToListPlanWork(parameters.getWork(), plan));
        }

        try {
            Plan savedPlan = planRepository.save(plan);
            return PatchPlanRes.builder()
                    .planId(savedPlan.getId())
                    .color(savedPlan.getColor())
                    .planType(savedPlan.getPlanType())
                    .title(savedPlan.getTitle())
                    .startDate(savedPlan.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedPlan.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .push(savedPlan.getPush())
                    .planVacation(calendarProvider.changeSetPlanVacationToListPlanVacationRes(savedPlan.getPlanVacations()))
                    .work(calendarProvider.changeListToDoListToListWorkRes(savedPlan.getPlanWorks()))
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
        for (PlanVacation planVacation : plan.getPlanVacations()) {
            planVacation.setStatus("N");
        }

        try {
            planRepository.save(plan);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_PLAN);
        }
    }


    /**
     * 일정 다이어리 생성
     *
     * @param parameters
     * @param planId
     * @return PostDiaryRes
     * @throws BaseException
     * @Auther shine
     */
    public PostDiaryRes createPlanDiary(PostDiaryReq parameters, Long planId) throws BaseException {
        Plan plan = calendarProvider.retrievePlanByIdAndStatusY(planId);

        PlanDiary newDiary = PlanDiary.builder()
                .date(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE))
                .content(parameters.getContent())
                .plan(plan)
                .build();

        if (plan.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        checkDate(plan, parameters.getDate());

        try {
            PlanDiary savedDiary = planDiaryRepository.save(newDiary);
            return PostDiaryRes.builder()
                    .id(savedDiary.getId())
                    .date(savedDiary.getDate().format(DateTimeFormatter.ISO_DATE))
                    .title(savedDiary.getDate().format(DateTimeFormatter.ofPattern("MM월 dd일")))
                    .content(savedDiary.getContent())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_DIARY);
        }
    }

    /**
     * 일정 다이어리 수정
     *
     * @param parameters
     * @param diaryId
     * @return PatchDiaryRes
     * @throws BaseException
     * @Auther shine
     */
    public PatchDiaryRes updatePlanDiary(PatchDiaryReq parameters, Long diaryId) throws BaseException {
        PlanDiary diary = calendarProvider.retrieveDiaryById(diaryId);

        if (diary.getPlan().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        diary.setContent(parameters.getContent());

        try {
            PlanDiary savedDiary = planDiaryRepository.save(diary);
            return PatchDiaryRes.builder()
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
     * 일정 다이어리 삭제
     *
     * @param diaryId
     * @throws BaseException
     * @Auther shine
     */
    public void deletePlanDiary(Long diaryId) throws BaseException {
        PlanDiary diary = calendarProvider.retrieveDiaryById(diaryId);

        if (diary.getPlan().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        try {
            planDiaryRepository.delete(diary);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_DIARY);
        }
    }


    /**
     * 할일 완료 -> 미완료, 미완료 -> 완료 처리
     *
     * @param todolistId
     * @return WorkRes
     * @throws BaseException
     */
    @Transactional
    public WorkRes deletePlanWork(Long todolistId) throws BaseException {
        PlanWork work = calendarProvider.retrieveToDoListById(todolistId);

        if(work.getProcessingStatus().equals("Y")) {
            work.setProcessingStatus("N");
        }
        if(work.getProcessingStatus().equals("N")) {
            work.setProcessingStatus("Y");
        }

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
    public PostDDayRes createDDay(PostDDayReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        DDay newDDay = DDay.builder()
                .ddayType(parameters.getDdayType())
                .title(parameters.getTitle())
                .date(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE))
                .userInfo(user)
                .build();

        if(Objects.nonNull(parameters.getSubTitle())) {
            newDDay.setSubtitle(parameters.getSubTitle());
        }

        if(parameters.getDdayType().equals("생일")) {
            newDDay.setChoiceCalendar(parameters.getChoiceCalendar());
        }
        if(parameters.getDdayType().equals("자격증") || parameters.getDdayType().equals("준비물")) {
            newDDay.setLink(parameters.getLink());
            newDDay.setPlaceLat(parameters.getPlaceLat());
            newDDay.setPlaceLon(parameters.getPlaceLon());
            newDDay.setDdayWorks(calendarProvider.changeListWorkReqToListDDayWork(parameters.getWork(), newDDay));
        }

        try {
            DDay savedDDay = ddayRepository.save(newDDay);
            return PostDDayRes.builder()
                    .ddayId(savedDDay.getId())
                    .ddayType(savedDDay.getDdayType())
                    .title(savedDDay.getTitle())
                    .subtitle(savedDDay.getSubtitle())
                    .date(savedDDay.getDate().format(DateTimeFormatter.ISO_DATE))
                    .link(savedDDay.getLink())
                    .choiceCalendar(Validation.isString(savedDDay.getChoiceCalendar()))
                    .placeLat(Validation.isBigDecimal(savedDDay.getPlaceLat()))
                    .placeLon(Validation.isBigDecimal(savedDDay.getPlaceLon()))
                    .works(calendarProvider.changeListSuppliesToListWorkRes(savedDDay.getDdayWorks()))
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
     * @return PatchDDayRes
     * @throws BaseException
     * @Auther shine
     */
    public PatchDDayRes updateDDay(PatchDDayReq parameters, Long ddayId) throws BaseException {
        DDay dday = calendarProvider.retrieveDDayByIdAndStatusY(ddayId);

        if (dday.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }
        
        dday.setTitle(parameters.getTitle());
        dday.setDate(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE));

        if(Objects.nonNull(parameters.getSubTitle())) {
            dday.setSubtitle(parameters.getSubTitle());
        }

        if(dday.getDdayType().equals("생일")) {
            dday.setChoiceCalendar(parameters.getChoiceCalendar());
        }
        if(dday.getDdayType().equals("자격증") || dday.getDdayType().equals("준비물")) {
            dday.setLink(parameters.getLink());
            dday.setPlaceLat(parameters.getPlaceLat());
            dday.setPlaceLon(parameters.getPlaceLon());
            dday.setDdayWorks(calendarProvider.changeListWorkReqToListDDayWork(parameters.getWork(), dday));
        }

        try {
            DDay savedDDay = ddayRepository.save(dday);
            return PatchDDayRes.builder()
                    .ddayId(savedDDay.getId())
                    .ddayType(savedDDay.getDdayType())
                    .title(savedDDay.getTitle())
                    .subtitle(savedDDay.getSubtitle())
                    .date(savedDDay.getDate().format(DateTimeFormatter.ISO_DATE))
                    .link(savedDDay.getLink())
                    .choiceCalendar(Validation.isString(savedDDay.getChoiceCalendar()))
                    .placeLat(Validation.isBigDecimal(savedDDay.getPlaceLat()))
                    .placeLon(Validation.isBigDecimal(savedDDay.getPlaceLon()))
                    .work(calendarProvider.changeListSuppliesToListWorkRes(savedDDay.getDdayWorks()))
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
    public PostDiaryRes createDDayDiary(PostDiaryReq parameters, Long ddayId) throws BaseException {
        DDay dday = calendarProvider.retrieveDDayByIdAndStatusY(ddayId);

        DDayDiary newMemo = DDayDiary.builder()
                .date(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE))
                .content(parameters.getContent())
                .dday(dday)
                .build();

        if (dday.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        for (DDayDiary diary : dday.getDdayDiaries()) {
            if(diary.getDate().isEqual(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE))) {
                throw new BaseException(ALREADY_EXIST_DIARY);
            }
        }

        try {
            DDayDiary savedMemo = ddayDiaryRepository.save(newMemo);
            return PostDiaryRes.builder()
                    .id(savedMemo.getId())
                    .date(savedMemo.getDate().format(DateTimeFormatter.ISO_DATE))
                    .title(savedMemo.getDate().format(DateTimeFormatter.ofPattern("MM월 dd일")))
                    .content(savedMemo.getContent())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_DIARY);
        }
    }

    /**
     * D-Day 다이어리 수정
     * 
     * @param parameters
     * @param diaryId
     * @return PatchDiaryRes
     * @throws BaseException
     */
    public PatchDiaryRes updateDDayDiary(PatchDiaryReq parameters, Long diaryId) throws BaseException {
        DDayDiary diary = calendarProvider.retrieveDDayDiaryById(diaryId);

        if (diary.getDday().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        diary.setContent(parameters.getContent());

        try {
            DDayDiary savedDiary = ddayDiaryRepository.save(diary);
            return PatchDiaryRes.builder()
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
     * D-Day 다이어리 삭제
     *
     * @param ddayId
     * @throws BaseException
     * @Auther shine
     */
    public void deleteDDayDiary(Long diaryId) throws BaseException {
        DDayDiary diary = calendarProvider.retrieveDDayDiaryById(diaryId);

        if (diary.getDday().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        try {
            ddayDiaryRepository.delete(diary);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_DIARY);
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

        if (work.getProcessingStatus().equals("Y")) {
            work.setProcessingStatus("N");
        }
        if (work.getProcessingStatus().equals("N")) {
            work.setProcessingStatus("Y");
        }

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



    private void checkDate(Plan plan, String diaryDate) throws BaseException {
        LocalDate date = LocalDate.parse(diaryDate, DateTimeFormatter.ISO_DATE);

        if (date.isBefore(plan.getStartDate()) || date.isAfter(plan.getEndDate())) {
            throw new BaseException(OUT_OF_BOUNDS_DATE_DIARY);
        }
        for (PlanDiary diary : plan.getPlanDiaries()) {
            if (diary.getDate().isEqual(date)) {
                throw new BaseException(ALREADY_EXIST_DIARY);
            }
        }
    }
}