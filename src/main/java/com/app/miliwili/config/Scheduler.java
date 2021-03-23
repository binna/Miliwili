package com.app.miliwili.config;

import com.app.miliwili.src.calendar.PlanRepository;
import com.app.miliwili.src.calendar.models.Plan;
import com.app.miliwili.src.exercise.ExerciseProvider;
import com.app.miliwili.src.exercise.ExerciseService;
import com.app.miliwili.src.exercise.model.ExerciseRoutine;
import com.app.miliwili.src.user.UserRepository;
import com.app.miliwili.src.user.UserService;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.FirebaseCloudMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Component
public class Scheduler {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final UserService userService;
    private final FirebaseCloudMessage firebaseCloudMessageService;
    private final ExerciseProvider exerciseProvider;
    private final ExerciseService exerciseService;


    @Scheduled(cron = "0 0 0 * * *")
    public void setDailyHobongAndStateIdx() {
        List<UserInfo> users = userRepository.findAllByStateIdxAndStatus(1, "Y");

        for (UserInfo user : users) {
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

    @Scheduled(cron = "0 0 19 * * *")
    public void sendPushMessage() {
        List<Plan> schedules = planRepository.findByPushAndStatusAndStartDate("Y", "Y", LocalDate.now().plusDays(1));

        for (Plan schedule : schedules) {
            System.out.println(schedule.getStartDate() + ", " + schedule.getTitle());
            try {
                    firebaseCloudMessageService.sendMessageTo(
                            schedule.getPushDeviceToken(),
                            schedule.getTitle(),
                            schedule.getTitle() + " 일정 하루 전날입니다. 준비해주세요!");
            } catch (Exception exception) {
                new BaseResponse<>(FAILED_TO_PUSH_MESSAGE);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetExerciseRoutineState(){
        List<ExerciseRoutine> exerciseRoutines = new ArrayList<>();
        try {
            exerciseRoutines = exerciseProvider.getCompleteRoutine();
        }catch (Exception e){
            e.printStackTrace();
            new BaseResponse<>(FAILED_FIND_GET_ROUTINE);
        }
        for(ExerciseRoutine routine: exerciseRoutines){
            try{
                exerciseService.resetRoutineDone(routine);
            }catch (Exception e){
                e.printStackTrace();
                new BaseResponse<>(FAILED_RESET_ROTUINE_DONE);
            }
        }
    }


}