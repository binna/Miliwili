package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class CalendarService {
    private final ScheduleRepository scheduleRepository;

    /**
     * 일정 생성
     * @param PostScheduleReq parameters
     * @return PostScheduleRes
     * @throws BaseException
     * @Auther shine
     */
    public PostScheduleRes createSchedule(PostScheduleReq parameters) throws BaseException {
        // TODO 회원 완성되면 토근 회원인지 검사

        System.out.println(parameters.toString());

        Schedule newSchedule = Schedule.builder()
                .color(parameters.getColor())
                .distinction(parameters.getDistinction())
                .title(parameters.getTitle())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .repetition(null)
                .push(null)
                .build();

//        if(parameters.getToDoList() != null) {
//            List<ToDoList> toDoLists = null;
//
//            for(WorkReq toDoList : parameters.getToDoList()) {
//                ToDoList newToDoList = ToDoList.builder()
//                        .content(toDoList.getContent())
//                        .build();
//                toDoLists.add(newToDoList);
//            }
//
//            newSchedule.setToDoLists(toDoLists);
//        }

        System.out.println(newSchedule.toString());

        try {
            newSchedule = scheduleRepository.save(newSchedule);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(FAILED_TO_POST_SCHEDULE);
        }

        System.out.println(newSchedule.toString());

        return PostScheduleRes.builder()
                .scheduleId(newSchedule.getId())
                .color(newSchedule.getColor())
                .distinction(newSchedule.getDistinction())
                .title(newSchedule.getTitle())
                .startDate(newSchedule.getStartDate().format(DateTimeFormatter.ISO_DATE))
                .endDate(newSchedule.getEndDate().format(DateTimeFormatter.ISO_DATE))
                .repetition(newSchedule.getPush())
                .push(newSchedule.getPush())
                .build();
    }
}