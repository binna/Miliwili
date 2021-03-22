package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.ScheduleVacationReq;
import com.app.miliwili.src.calendar.dto.ScheduleVacationRes;
import com.app.miliwili.src.calendar.dto.WorkReq;
import com.app.miliwili.src.calendar.dto.WorkRes;
import com.app.miliwili.src.calendar.models.Schedule;
import com.app.miliwili.src.calendar.models.ScheduleVacation;
import com.app.miliwili.src.calendar.models.ToDoList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.FAILED_TO_GET_SCHEDULE;

@RequiredArgsConstructor
@Service
public class CalendarProvider {
    private final ScheduleRepository scheduleRepository;

    /**
     * 정기휴가 스케줄 검색
     *
     * @param Long userId
     * @return List<Schedule>
     * @throws BaseException
     * @Auther shine
     */
    public List<Schedule> retrieveOrdinaryLeaveScheduleByStatusY(Long userId) throws BaseException {
        List<Schedule> schedules = null;

        try {
            //schedules = scheduleRepository.findByUser_IdAndDistinctionAndStatusOrderByStartDate(userId, "정기휴가", "Y");
            return schedules;
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_SCHEDULE);
        }
    }

    /**
     * List<ScheduleVacationReq> -> Set<ScheduleVacation> 변경
     *
     * @param parameters
     * @param schedule
     * @return Set<ScheduleVacation>
     * @Auther shine
     */
    public Set<ScheduleVacation> changeListScheduleVacationReqToSetScheduleVacation(List<ScheduleVacationReq> parameters, Schedule schedule) {
        if (parameters == null) return null;

        return parameters.stream().map(scheduleVacationReq -> {
            return ScheduleVacation.builder()
                    .count(scheduleVacationReq.getCount())
                    .vacationId(scheduleVacationReq.getVacationId())
                    .schedule(schedule)
                    .build();
        }).collect(Collectors.toSet());
    }

    /**
     * Set<ScheduleVacation> -> List<ScheduleVacationRes> 변경
     *
     * @param parameters
     * @return Set<ScheduleVacation>
     * @Auther shine
     */
    public List<ScheduleVacationRes> changeSetScheduleVacationToListScheduleVacationRes(Set<ScheduleVacation> parameters) {
        if (parameters == null) return null;

        return parameters.stream().map(scheduleVacation -> {
            return ScheduleVacationRes.builder()
                    .scheduleVacationId(scheduleVacation.getVacationId())
                    .count(scheduleVacation.getCount())
                    .vacationId(scheduleVacation.getVacationId())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<WorkReq> -> List<ToDoList> 변경
     *
     * @param parameters
     * @param schedule
     * @return List<ToDoList>
     * @Auther shine
     */
    public List<ToDoList> changeListWorkReqToListToDoList(List<WorkReq> parameters, Schedule schedule) {
        if (parameters == null) return null;

        return parameters.stream().map(workReq -> {
            return ToDoList.builder()
                    .content(workReq.getContent())
                    .schedule(schedule)
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