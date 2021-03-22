package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.PlanVacationReq;
import com.app.miliwili.src.calendar.dto.PlanVacationRes;
import com.app.miliwili.src.calendar.dto.WorkReq;
import com.app.miliwili.src.calendar.dto.WorkRes;
import com.app.miliwili.src.calendar.models.Plan;
import com.app.miliwili.src.calendar.models.PlanVacation;
import com.app.miliwili.src.calendar.models.ToDoList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class CalendarProvider {
    private final PlanRepository planRepository;

    /**
     * 정기휴가 스케줄 검색
     *
     * @param Long userId
     * @return List<Schedule>
     * @throws BaseException
     * @Auther shine
     */
//    public List<Plan> retrieveOrdinaryLeaveScheduleByStatusY(Long userId) throws BaseException {
//        List<Plan> schedules = null;
//
//        try {
//            //schedules = scheduleRepository.findByUser_IdAndDistinctionAndStatusOrderByStartDate(userId, "정기휴가", "Y");
//            return schedules;
//        } catch (Exception exception) {
//            throw new BaseException(FAILED_TO_GET_SCHEDULE);
//        }
//    }

    /**
     * planId로 유효한 일정 조회
     *
     * @param planId
     * @return Plan
     * @throws BaseException
     */
    @Transactional
    public Plan retrievePlanByIdAndStatusY(Long planId) throws BaseException {
        return planRepository.findByIdAndStatus(planId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_PLAN));
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
        if (parameters == null) return null;

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
        if (parameters == null) return null;

        return parameters.stream().map(scheduleVacation -> {
            return PlanVacationRes.builder()
                    .planVacationId(scheduleVacation.getVacationId())
                    .count(scheduleVacation.getCount())
                    .vacationId(scheduleVacation.getVacationId())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<WorkReq> -> List<ToDoList> 변경
     *
     * @param parameters
     * @param plan
     * @return List<ToDoList>
     * @Auther shine
     */
    public List<ToDoList> changeListWorkReqToListToDoList(List<WorkReq> parameters, Plan plan) {
        if (parameters == null) return null;

        return parameters.stream().map(workReq -> {
            return ToDoList.builder()
                    .content(workReq.getContent())
                    .plan(plan)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<ToDoList> -> List<WorkRes> 변경
     *
     * @param parameters
     * @return List<WorkRes>
     * @Auther shine
     */
    public List<WorkRes> changeListToDoListToListWorkRes(List<ToDoList> parameters) {
        if (parameters == null) return null;

        return parameters.stream().map(toDoList -> {
            return WorkRes.builder()
                    .id(toDoList.getId())
                    .content(toDoList.getContent())
                    .processingStatus(toDoList.getProcessingStatus())
                    .build();
        }).collect(Collectors.toList());
    }
}