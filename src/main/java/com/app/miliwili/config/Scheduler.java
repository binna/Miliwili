package com.app.miliwili.config;

import com.app.miliwili.src.calendar.ScheduleRepository;
import com.app.miliwili.src.user.UserRepository;
import com.app.miliwili.src.user.UserService;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Component
public class Scheduler {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;

    @Scheduled(cron  = "0 0 0 * * *")
    public void setDailyHobongAndStateIdx() {
        List<User> users = userRepository.findAllByStateIdxAndStatus(1, "Y");

        for(User user : users) {
            String startDate = user.getStartDate().format(DateTimeFormatter.ISO_DATE);
            String strPrivate = user.getNormalPromotionState().getFirstDate().format(DateTimeFormatter.ISO_DATE);
            String strCorporal = user.getNormalPromotionState().getSecondDate().format(DateTimeFormatter.ISO_DATE);
            String strSergeant = user.getNormalPromotionState().getThirdDate().format(DateTimeFormatter.ISO_DATE);
            NormalPromotionState normalPromotionState = user.getNormalPromotionState();

            userService.setStateIdx(strPrivate, strCorporal, strSergeant, normalPromotionState);
            userService.setHobong(normalPromotionState.getStateIdx(), startDate, strPrivate, strCorporal, strSergeant, normalPromotionState);

            try {
                userRepository.save(user);
            } catch (Exception exception) {
                new BaseResponse<>(FAILED_TO_SET_DAILY_HOBONG_STATUSIDX);
            }
        }
    }

    // TODO FCM 보내기
    public void sendPushMessage() {
//        // TODO PUSH 알람 -> 스케줄러로 결정
//        newSchedule.setPush("Y");
//        if(newSchedule.getPush().equals("Y")) {
//            System.out.println("PUSH 알람");
//            try {
//                firebaseCloudMessageService.sendMessageTo(
//                        parameters.getPushDeviceToken(),
//                        "test " + newSchedule.getTitle(),
//                        "test " + newSchedule.getTitle() + " 일정 하루 전날입니다.\n준비해주세요!");
//            } catch (Exception exception) {
//                // TODO 에러 처리
//            }
//        }
    }

}