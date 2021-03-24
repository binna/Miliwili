package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.PlanVacationReq;
import com.app.miliwili.src.calendar.dto.PlanVacationRes;
import com.app.miliwili.src.calendar.dto.WorkReq;
import com.app.miliwili.src.calendar.dto.WorkRes;
import com.app.miliwili.src.calendar.models.*;
import com.app.miliwili.src.user.VacationSelectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class CalendarProvider {
    private final PlanRepository planRepository;
    private final PlanWorkRepository planWorkRepository;
    private final PlanDiaryRepository diaryRepository;
    private final DDayRepository ddayRepository;
    private final DDayWorkRepository ddayWorkRepository;
    private final DDayDiaryRepository ddayDiaryRepository;
    private final PlanVacationRepository planVacationRepository;
    private final VacationSelectRepository planVacationSelectRepository;

    /**
     * planId로 유효한 일정조회
     *
     * @param planId
     * @return Plan
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public Plan retrievePlanByIdAndStatusY(Long planId) throws BaseException {
        return planRepository.findByIdAndStatus(planId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_PLAN));
    }

    /**
     * planId로 일정 다이어리 조회
     *
     * @param diaryId
     * @return Diary
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public PlanDiary retrievePlanDiaryById(Long diaryId) throws BaseException {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_DIARY));
    }

    /**
     * workId로 할일 조회
     *
     * @param workId
     * @return PlanWork
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public PlanWork retrievePlanWorkById(Long workId) throws BaseException {
        return planWorkRepository.findById(workId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_WORK));
    }

    /**
     * ddayId로 유효한 디데이 조회
     *
     * @param ddayId
     * @return DDay
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public DDay retrieveDDayByIdAndStatusY(Long ddayId) throws BaseException {
        return ddayRepository.findByIdAndStatus(ddayId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_D_DAY));
    }

    /**
     * ddayId로 디데이 다이어리 조회
     *
     * @param ddayId
     * @return DDayDiary
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public DDayDiary retrieveDDayDiaryById(Long ddayId) throws BaseException {
        return ddayDiaryRepository.findById(ddayId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_DIARY));
    }

    /**
     * workId로 준비물 조회
     *
     * @param workId
     * @return DDayWork
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public DDayWork retrieveDDayWorkById(Long workId) throws BaseException {
        return ddayWorkRepository.findById(workId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_WORK));
    }

    /**
     * vacationId로 일정휴가 조회
     *
     * @param vacationId
     * @return PlanVacation
     * @throws BaseException
     */
    public PlanVacation retrievePlanVacationByIdAndStatusY(Long vacationId) throws BaseException {
        return planVacationRepository.findByVacationIdAndStatus(vacationId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_VACATION_PLAN));
    }




        /**
     * List<PlanVacationReq> -> Set<PlanVacation> 변경
     *
     * @param parameters
     * @param plan
     * @return Set<PlanVacation>
     * @Auther shine
     */
    public Set<PlanVacation> changeListPlanVacationReqToSetPlanVacation(List<PlanVacationReq> parameters, Plan plan) {
        if (Objects.isNull(parameters)) return null;

        return parameters.stream().map(scheduleVacationReq -> {
            return PlanVacation.builder()
                    .count(scheduleVacationReq.getCount())
                    .vacationId(scheduleVacationReq.getVacationId())
                    .plan(plan)
                    .build();
        }).collect(Collectors.toSet());
    }

    /**
     * Set<PlanVacation> -> List<PlanVacationRes> 변경
     *
     * @param parameters
     * @return List<PlanVacationRes>
     * @Auther shine
     */
    public List<PlanVacationRes> changeSetPlanVacationToListPlanVacationRes(Set<PlanVacation> parameters) {
        if (Objects.isNull(parameters)) return null;

        return parameters.stream().map(scheduleVacation -> {
            return PlanVacationRes.builder()
                    .planVacationId(scheduleVacation.getVacationId())
                    .count(scheduleVacation.getCount())
                    .vacationId(scheduleVacation.getVacationId())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<WorkReq> -> List<PlanWork> 변경
     *
     * @param parameters
     * @param plan
     * @return List<ToDoList>
     * @Auther shine
     */
    public List<PlanWork> changeListWorkReqToListPlanWork(List<WorkReq> parameters, Plan plan) {
        if (Objects.isNull(parameters)) return null;

        return parameters.stream().map(workReq -> {
            return PlanWork.builder()
                    .content(workReq.getContent())
                    .plan(plan)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<PlanWork> -> List<WorkRes> 변경
     *
     * @param parameters
     * @return List<WorkRes>
     * @Auther shine
     */
    public List<WorkRes> changeListPlanWorkToListWorkRes(List<PlanWork> parameters) {
        if (Objects.isNull(parameters)) return null;

        return parameters.stream().map(toDoList -> {
            return WorkRes.builder()
                    .workId(toDoList.getId())
                    .content(toDoList.getContent())
                    .processingStatus(toDoList.getProcessingStatus())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<WorkReq> -> List<DDayWork> 변경
     *
     * @param parameters
     * @param dday
     * @return List<Supplies>
     * @Auther shine
     */
    public List<DDayWork> changeListWorkReqToListDDayWork(List<WorkReq> parameters, DDay dday) {
        if (Objects.isNull(parameters)) return null;

        return parameters.stream().map(workReq -> {
            return DDayWork.builder()
                    .content(workReq.getContent())
                    .dday(dday)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<DDayWork> -> List<WorkRes> 변경
     *
     * @param parameters
     * @return List<WorkRes>
     * @Auther shine
     */
    public List<WorkRes> changeListDDayWorkToListWorkRes(List<DDayWork> parameters) {
        if (Objects.isNull(parameters)) return null;

        return parameters.stream().map(supplies -> {
            return WorkRes.builder()
                    .workId(supplies.getId())
                    .content(supplies.getContent())
                    .processingStatus(supplies.getProcessingStatus())
                    .build();
        }).collect(Collectors.toList());
    }
}