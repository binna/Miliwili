package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.utils.FirebaseCloudMessage;
import com.app.miliwili.src.calendar.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class CalendarService {
    private final ScheduleRepository scheduleRepository;
    private final CalendarProvider calendarProvider;
    private final FirebaseCloudMessage firebaseCloudMessageService;

    /**
     * 일정 생성
     * @param PostScheduleReq parameters
     * @return PostScheduleRes
     * @throws BaseException
     * @Auther shine
     */
    public PostScheduleRes createSchedule(PostScheduleReq parameters) throws BaseException {
        // TODO 회원 완성되면 토근 회원인지 검사

        Schedule newSchedule = Schedule.builder()
                .color(parameters.getColor())
                .distinction(parameters.getDistinction())
                .title(parameters.getTitle())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .build();

        if(parameters.getRepetition() != null) {
            newSchedule.setRepetition(parameters.getRepetition());
        }
        if(parameters.getPush() != null) {
            newSchedule.setPush(parameters.getPush());
        }
        if(parameters.getToDoList() != null) {
            newSchedule.setToDoLists(calendarProvider.retrieveToDoList(parameters.getToDoList()));
        }

        try {
            newSchedule = scheduleRepository.save(newSchedule);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_SCHEDULE);
        }

        // TODO PUSH 알람 -> 스케줄러로 결정
        newSchedule.setPush("Y");
        if(newSchedule.getPush().equals("Y")) {
            System.out.println("PUSH 알람");
            try {
                firebaseCloudMessageService.sendMessageTo(
                        parameters.getPushDeviceToken(),
                        "test " + newSchedule.getTitle(),
                        "test " + newSchedule.getTitle() + " 일정 하루 전날입니다.\n준비해주세요!");
            } catch (Exception exception) {
                // TODO 에러 처리
            }
        }

        return PostScheduleRes.builder()
                .scheduleId(newSchedule.getId())
                .color(newSchedule.getColor())
                .distinction(newSchedule.getDistinction())
                .title(newSchedule.getTitle())
                .startDate(newSchedule.getStartDate().format(DateTimeFormatter.ISO_DATE))
                .endDate(newSchedule.getEndDate().format(DateTimeFormatter.ISO_DATE))
                .repetition(newSchedule.getRepetition())
                .push(newSchedule.getPush())
                .toDoList(calendarProvider.retrieveWorkRes(newSchedule.getToDoLists()))
                .build();
    }




}