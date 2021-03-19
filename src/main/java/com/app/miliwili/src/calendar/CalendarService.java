package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.PostDDayReq;
import com.app.miliwili.src.calendar.dto.PostDDayRes;
import com.app.miliwili.src.calendar.dto.PostScheduleReq;
import com.app.miliwili.src.calendar.dto.PostScheduleRes;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.User;
import com.app.miliwili.utils.FirebaseCloudMessage;
import com.app.miliwili.src.calendar.models.*;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class CalendarService {
    private final ScheduleRepository scheduleRepository;
    private final DDayRepository dDayRepository;
    private final JwtService jwtService;
    private final FirebaseCloudMessage firebaseCloudMessageService;
    private final CalendarProvider calendarProvider;
    private final UserProvider userProvider;

    /**
     * 일정 생성
     * @param PostScheduleReq parameters
     * @return PostScheduleRes
     * @throws BaseException
     * @Auther shine
     */
    public PostScheduleRes createSchedule(PostScheduleReq parameters) throws BaseException {
        User user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        Schedule newSchedule = Schedule.builder()
                .color(parameters.getColor())
                .distinction(parameters.getDistinction())
                .title(parameters.getTitle())
                //.startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                //.endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .user(user)
                .build();

        if(!Objects.isNull(parameters.getRepetition())) {
            newSchedule.setRepetition(parameters.getRepetition());
        }
        if(!Objects.isNull(parameters.getPush())) {
            newSchedule.setPush(parameters.getPush());
        }
        if(!Objects.isNull(parameters.getToDoList())) {
            newSchedule.setToDoLists(calendarProvider.retrieveToDoList(parameters.getToDoList()));
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

        try {
            Schedule savedSchedule = scheduleRepository.save(newSchedule);
            return PostScheduleRes.builder()
                    .scheduleId(savedSchedule.getId())
                    .color(savedSchedule.getColor())
                    .distinction(savedSchedule.getDistinction())
                    .title(savedSchedule.getTitle())
                    //.startDate(savedSchedule.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    //.endDate(savedSchedule.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .repetition(savedSchedule.getRepetition())
                    .push(savedSchedule.getPush())
                    .toDoList(calendarProvider.retrieveWorkRes(savedSchedule.getToDoLists()))
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_SCHEDULE);
        }
    }

    /**
     * D-Day 생성
     * @param
     * @return
     * @throws BaseException
     * @Auther shine
     */
    public PostDDayRes createDDay(PostDDayReq parameters) throws BaseException {
        User user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        DDay dDay = DDay.builder()
                .distinction(parameters.getDistinction())
                .title(parameters.getTitle())
                .subtitle(parameters.getSubTitle())
                .startDay(LocalDate.parse(parameters.getStartDay(), DateTimeFormatter.ISO_DATE))
                .endDay(LocalDate.parse(parameters.getEndDay(), DateTimeFormatter.ISO_DATE))
                .placeLat(parameters.getPlaceLat())
                .placeLon(parameters.getPlaceLon())
                .user(user)
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