package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.WorkReq;
import com.app.miliwili.src.calendar.dto.WorkRes;
import com.app.miliwili.src.calendar.models.Schedule;
import com.app.miliwili.src.calendar.models.ToDoList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * List<ToDoList> -> List<WorkRes> 변경
     *
     * @param List<WorkReq> parameters
     * @return List<ToDoList>
     * @Auther shine
     */
    public List<ToDoList> retrieveToDoList(List<WorkReq> parameters) {
        if (parameters == null) return null;

        return parameters.stream().map(workReq -> {
            return ToDoList.builder()
                    .content(workReq.getContent())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * List<ToDoList> -> List<WorkRes> 변경
     *
     * @param List<ToDoList> parameters
     * @return List<WorkRes>
     * @Auther shine
     */
    public List<WorkRes> retrieveWorkRes(List<ToDoList> parameters) {
        if (parameters == null) return null;

        return parameters.stream().map(toDoList -> {
            return new WorkRes(toDoList.getContent(), toDoList.getProcessingStatus());
        }).collect(Collectors.toList());
    }
}