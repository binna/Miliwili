package com.app.miliwili.src.calendar;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.dto.PostDDayReq;
import com.app.miliwili.src.calendar.dto.PostDDayRes;
import com.app.miliwili.src.calendar.dto.PostScheduleReq;
import com.app.miliwili.src.calendar.dto.PostScheduleRes;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.User;
import com.app.miliwili.src.calendar.models.*;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class CalendarService {
    private final ScheduleRepository scheduleRepository;
    private final DDayRepository dDayRepository;
    private final JwtService jwtService;
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
                .scheduleType(parameters.getScheduleType())
                .title(parameters.getTitle())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .user(user)
                .build();

        if(parameters.getPush().equals("Y")) {
            newSchedule.setPush(parameters.getPush());
            newSchedule.setPushDeviceToken(parameters.getPushDeviceToken());
        }

        if(newSchedule.getScheduleType().equals("휴가")) {
            newSchedule.setScheduleVacations(calendarProvider.changeListScheduleVacationReqToSetScheduleVacation(parameters.getScheduleVacation(), newSchedule));
        }

        if(Objects.nonNull(parameters.getToDoList())) {
            newSchedule.setToDoLists(calendarProvider.changeListWorkReqToListToDoList(parameters.getToDoList(), newSchedule));
        }

        try {
            Schedule savedSchedule = scheduleRepository.save(newSchedule);
            return PostScheduleRes.builder()
                    .scheduleId(savedSchedule.getId())
                    .color(savedSchedule.getColor())
                    .scheduleType(savedSchedule.getScheduleType())
                    .title(savedSchedule.getTitle())
                    .startDate(savedSchedule.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedSchedule.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .push(savedSchedule.getPush())
                    .scheduleVacation(calendarProvider.changeSetScheduleVacationToListScheduleVacationRes(savedSchedule.getScheduleVacations()))
                    .toDoList(calendarProvider.changeListToDoListToListWorkRes(savedSchedule.getToDoLists()))
                    .build();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(FAILED_TO_POST_SCHEDULE);
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