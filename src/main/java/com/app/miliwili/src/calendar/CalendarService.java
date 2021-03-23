package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.*;
import com.app.miliwili.src.calendar.models.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
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
    private final DiaryRepository diaryRepository;
    private final ToDoListRepository toDoListRepository;
    private final DDayRepository dDayRepository;
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

        if (Objects.nonNull(parameters.getToDoList())) {
            newPlan.setToDoLists(calendarProvider.changeListWorkReqToListToDoList(parameters.getToDoList(), newPlan));
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
                    .toDoList(calendarProvider.changeListToDoListToListWorkRes(savedPlan.getToDoLists()))
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

        if (Objects.nonNull(parameters.getToDoList())) {
            plan.getToDoLists().clear();
            plan.getToDoLists().addAll(calendarProvider.changeListWorkReqToListToDoList(parameters.getToDoList(), plan));
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
                    .toDoList(calendarProvider.changeListToDoListToListWorkRes(savedPlan.getToDoLists()))
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
     */
    public PostDiaryRes createDiary(PostDiaryReq parameters, Long planId) throws BaseException {
        Plan plan = calendarProvider.retrievePlanByIdAndStatusY(planId);

        Diary newDiary = Diary.builder()
                .date(LocalDate.parse(parameters.getDate(), DateTimeFormatter.ISO_DATE))
                .content(parameters.getContent())
                .plan(plan)
                .build();

        if (plan.getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        checkDate(plan, parameters.getDate());

        try {
            Diary savedDiary = diaryRepository.save(newDiary);
            return PostDiaryRes.builder()
                    .diaryId(savedDiary.getId())
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
     */
    public PatchDiaryRes updateDiary(PatchDiaryReq parameters, Long diaryId) throws BaseException {
        Diary diary = calendarProvider.retrieveDiaryById(diaryId);

        if (diary.getPlan().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        diary.setContent(parameters.getContent());

        try {
            Diary savedDiary = diaryRepository.save(diary);
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
     */
    public void deleteDiary(Long diaryId) throws BaseException {
        Diary diary = calendarProvider.retrieveDiaryById(diaryId);

        if (diary.getPlan().getUserInfo().getId() != jwtService.getUserId()) {
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        try {
            diaryRepository.delete(diary);
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
    public WorkRes deleteToDoList(Long todolistId) throws BaseException {
        ToDoList toDoList = calendarProvider.retrieveToDoListById(todolistId);

        if(toDoList.getProcessingStatus().equals("Y")) {
            toDoList.setProcessingStatus("N");

        }
        if(toDoList.getProcessingStatus().equals("N")) {
            toDoList.setProcessingStatus("Y");
        }

        try {
            ToDoList savedToDoList = toDoListRepository.save(toDoList);
            return WorkRes.builder()
                    .id(savedToDoList.getId())
                    .content(savedToDoList.getContent())
                    .processingStatus(savedToDoList.getProcessingStatus())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_TODOLIST);
        }
    }


    private void checkDate(Plan plan, String diaryDate) throws BaseException {
        LocalDate date = LocalDate.parse(diaryDate, DateTimeFormatter.ISO_DATE);

        if (date.isBefore(plan.getStartDate()) || date.isAfter(plan.getEndDate())) {
            throw new BaseException(OUT_OF_BOUNDS_DATE_DIARY);
        }
        for (Diary diary : plan.getDiaries()) {
            if (diary.getDate().isEqual(date)) {
                throw new BaseException(ALREADY_EXIST_DIARY);
            }
        }
    }


//    private void setScheduleDate(String startDate, String endDate, Schedule schedule) {
//        List<Diary> scheduleDates = new ArrayList<>();
//
//        LocalDate startDay = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
//        LocalDate endDay = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
//
//        LocalDate targetDate = startDay;
//        int days = (int) ChronoUnit.DAYS.between(startDay, endDay);
//        for (int i = 0; i <= days; i++) {
//            scheduleDates.add(Diary.builder()
//                    .date(targetDate)
//                    .schedule(schedule)
//                    .build());
//            targetDate = targetDate.plusDays(Long.valueOf(1));
//        }
//        schedule.setScheduleDates(scheduleDates);
//    }

    /**
     * D-Day 생성
     *
     * @param
     * @return
     * @throws BaseException
     * @Auther shine
     */
    public PostDDayRes createDDay(PostDDayReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        DDay dDay = DDay.builder()
                .distinction(parameters.getDistinction())
                .title(parameters.getTitle())
                .subtitle(parameters.getSubTitle())
                .startDay(LocalDate.parse(parameters.getStartDay(), DateTimeFormatter.ISO_DATE))
                .endDay(LocalDate.parse(parameters.getEndDay(), DateTimeFormatter.ISO_DATE))
                .placeLat(parameters.getPlaceLat())
                .placeLon(parameters.getPlaceLon())
                .userInfo(user)
                .build();

        if (!Objects.isNull(parameters.getLink())) {
            dDay.setLink(parameters.getLink());
        }
        if (!Objects.isNull(parameters.getChoiceCalendar())) {
            dDay.setChoiceCalendar(parameters.getChoiceCalendar());
        }

        try {
            DDay savedDDay = dDayRepository.save(dDay);
            return PostDDayRes.builder()
                    .dDayId(savedDDay.getId())
                    .distinction(savedDDay.getDistinction())
                    .title(savedDDay.getTitle())
                    .subtitle(savedDDay.getSubtitle())
                    .startDay(savedDDay.getStartDay().format(DateTimeFormatter.ISO_DATE))
                    .endDay(savedDDay.getEndDay().format(DateTimeFormatter.ISO_DATE))
                    .link(savedDDay.getLink())
                    .choiceCalendar(savedDDay.getChoiceCalendar())
                    .placeLat(savedDDay.getPlaceLat())
                    .placeLon(savedDDay.getPlaceLon())
                    //.supplies()
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_D_DAY);
        }
    }


}