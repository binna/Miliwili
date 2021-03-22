package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.exercise.dto.*;
import com.app.miliwili.src.exercise.model.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRoutineRepository exerciseRoutineRepository;
    private final ExerciseRoutineDetailRepository exerciseRoutineDetailRepository;
    private final ExerciseDetailSetRepository exerciseDetailSetRepository;
    private final ExerciseProvider exerciseProvider;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    /**
     * 운동탭 처음 입장시 --> 목표 몸무게, 현재 몸무게 입력 ui
     *
     */
    public Long createFistWeight(PostExerciseFirstWeightReq param) throws BaseException{
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        if(exerciseProvider.isExerciseInfoUser(user.getId()) == true)
            throw new BaseException(FAILED_CHECK_FIRST_WIEHGT);

        ExerciseInfo exerciseInfo = ExerciseInfo.builder()
                .user(user)
                .firstWeight(param.getFirstWeight())
                .goalWeight(param.getGoalWeight())
                .build();
        try{
            exerciseRepository.save(exerciseInfo);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_POST_FIRST_WIEHGT);
        }

        return exerciseInfo.getId();
    }

    /**
     * 데일리 몸무게 입력
     */
    public String createDayilyWeight(PostExerciseWeightReq param, long exerciseId) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        ExerciseWeightRecord weightRecord = ExerciseWeightRecord.builder()
                .weight(param.getDayWeight())
                .exerciseInfo(exerciseInfo)
                .build();

        exerciseInfo.addWeightRecord(weightRecord);

        try {
            exerciseRepository.save(exerciseInfo);
        }catch (Exception e){
            throw new BaseException(FAILED_POST_DAILY_WEIGHT);
        }

        return weightRecord.getWeight()+"kg 입력되었습니다";
    }

    /**
     * 데일리 몸무게 수정
     */
    public String modifyDailyWeight(PatchExerciseDailyWeightReq param, long exerciseId) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        LocalDateTime targetDate = LocalDateTime.parse(param.getDayDate()+"T00:00:00");
        LocalDateTime targetNextDate = LocalDateTime.parse((param.getDayDate()+"T23:59:59"));

        ExerciseWeightRecord targetWeightRecord = exerciseProvider.getExerciseWiehgtRecord(exerciseId, targetDate,targetNextDate);
        targetWeightRecord.setWeight(param.getDayWeight());

        exerciseRepository.save(exerciseInfo);

        return param.getDayWeight()+"kg으로 수정되었습니다.";
    }

    /**
     * 목표 몸무게 수정
     */
    public String modifyGoalWeight(PatchExerciseGoalWeight param, long exerciseId) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }
        exerciseInfo.setGoalWeight(param.getGoalWeight());

        try {
            exerciseRepository.save(exerciseInfo);
        }catch (Exception e){
            throw new BaseException(FAILED_PATCH_GOAL_WEIGHT);
        }

        return "목표체중이 "+ exerciseInfo.getGoalWeight() +"kg으로 수정되었습니다.";

    }

    /**
     *루틴 만들기
     *
     */
    public Long createRoutine(PostExerciseRoutineReq param, long exerciseId) throws BaseException {
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if (exerciseInfo.getUser().getId() != jwtService.getUserId()) {
            throw new BaseException(INVALID_USER);
        }

        ExerciseRoutine newRoutine = ExerciseRoutine.builder()
                .name(param.getRoutineName())
                .bodyPart(param.getBodyPart())
                .repeaDay(param.getRepeatDay())
                .exerciseInfo(exerciseInfo)
                .routineDetails(new ArrayList<>())
                .build();


      //  System.out.println(exerciseInfo.getExerciseRoutines().get(0).getRoutineDetails());

        for (int i = 0; i < param.getDetailName().size(); i++) {
            ExerciseRoutineDetail newRoutineDetail = ExerciseRoutineDetail.builder()
                    .sequence(i + 1)
                    .name(param.getDetailName().get(i))
                    .routineTypeId(param.getDetailType().get(i))
                    .setCount(param.getDetailSet().get(i))
                    .isSame((param.getDetailSetEqual().get(i)) ? "Y" : "N")
                    .exerciseRoutine(newRoutine)
                    .detailSets(new ArrayList<>())
                    .build();
            System.out.println(newRoutineDetail.getName());
//            exerciseRoutineDetailRepository.save(newRoutineDetail);

            //무게 + 개수
            if (newRoutineDetail.getRoutineTypeId() == 1) {
                String[] totalArr = param.getDetailTypeContext().get(i).split("/");
                System.out.println(totalArr[0]);
                System.out.println(totalArr[1]);
                for (int j = 0; j < totalArr.length; j++) {
                    String[] weightCount = totalArr[j].split("#");
                    ExerciseDetailSet newDetailSet = ExerciseDetailSet.builder()
                            .setIdx(j + 1)
                            .setWeight(Double.parseDouble(weightCount[0]))
                            .setCount(Integer.parseInt(weightCount[1]))
                            .exerciseRoutineDetail(newRoutineDetail)
                            .build();
                    //arr추가
                    newRoutineDetail.addDetailSet(newDetailSet);
                }

            }
            // 개수간
            else if (newRoutineDetail.getRoutineTypeId() == 2) {
                String[] totalArr = param.getDetailTypeContext().get(i).split("/");
                for (int j = 0; j < totalArr.length; j++) {
                    ExerciseDetailSet newDetailSet = ExerciseDetailSet.builder()
                            .setIdx(j + 1)
                            .setCount(Integer.parseInt(totalArr[j]))
                            .exerciseRoutineDetail(newRoutineDetail)
                            .build();
                    //arr추가
                    newRoutineDetail.addDetailSet(newDetailSet);

                }
            }
            //시간
            else {
                String[] totalArr = param.getDetailTypeContext().get(i).split("/");
                for (int j = 0; j < totalArr.length; j++) {
                    ExerciseDetailSet newDetailSet = ExerciseDetailSet.builder()
                            .setIdx(j + 1)
                            .setTime(Integer.parseInt(totalArr[j]))
                            .exerciseRoutineDetail(newRoutineDetail)
                            .build();
                    //arr추가
                    newRoutineDetail.addDetailSet(newDetailSet);

                }
            }
            //arr추가
            newRoutine.addRoutineDetail(newRoutineDetail);
        }
        exerciseInfo.addExerciseRoutine(newRoutine);

        try {
            //exerciseRepository.save(exerciseInfo);
            //이게 문제가 될까????
            exerciseRoutineRepository.save(newRoutine);
        }catch (Exception e){
            throw new BaseException(FAILED_PATCH_DAILY_WEIGHT);
        }
        System.out.println(exerciseInfo.getExerciseRoutines().get(0).getName());

        return newRoutine.getId();
    }

    /**
     * 루틴 삭제
     */
    public String deleteRoutine(long exerciseId, long routineId) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);
        if (exerciseInfo.getUser().getId() != jwtService.getUserId()) {
            throw new BaseException(INVALID_USER);
        }
        List<ExerciseRoutine> routineList = exerciseInfo.getExerciseRoutines();
        ExerciseRoutine routine = null;
        int i=0;
        for(i=0;i<routineList.size();i++){
            if(routineList.get(i).getId() == routineId){
                routine = routineList.get(i);
            }
        }
        if(i == routineList.size()-1 || routine == null)
            throw new BaseException(FAILED_FIND_DELETE_ROUTINE);

        routine.setExerciseInfo(null);

        exerciseInfo.getExerciseRoutines().remove(routine);
        exerciseRoutineRepository.delete(routine);


        return "\""+routine.getName()+"\""+"루틴이 삭제되었습니다";
    }


    /**
     * 운동 안한상태로 초기화
     */
    public void resetRoutineDone(ExerciseRoutine routine){
        routine.setDone("N");
        exerciseRoutineRepository.save(routine);
    }


}
