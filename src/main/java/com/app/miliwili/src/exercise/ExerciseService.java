package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.exercise.dto.PatchExerciseGoalWeight;
import com.app.miliwili.src.exercise.dto.PostExerciseFirstWeightReq;
import com.app.miliwili.src.exercise.dto.PostExerciseRoutineReq;
import com.app.miliwili.src.exercise.dto.PostExerciseWeightReq;
import com.app.miliwili.src.exercise.model.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.User;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
        User user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

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
     *
     *
     */
    public String createRoutine(PostExerciseRoutineReq param, long exerciseId) throws BaseException {
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if (exerciseInfo.getUser().getId() != jwtService.getUserId()) {
            throw new BaseException(INVALID_USER);
        }

        ExerciseRoutine newRoutine = ExerciseRoutine.builder()
                .name(param.getRoutineName())
                .bodyPart(param.getBodyPart())
                .repeaDay(param.getRepeatDay())
                .exerciseInfo(exerciseInfo)
                .build();


        for (int i = 0; i < param.getDetailName().size(); i++) {
            ExerciseRoutineDetail newRoutineDetail = ExerciseRoutineDetail.builder()
                    .sequence(i + 1)
                    .name(param.getDetailName().get(i))
                    .routineTypeId(param.getDetailType().get(i))
                    .setCount(param.getDetailSet().get(i))
                    .isSame((param.getDetailSetEqual().get(i)) ? "Y" : "N")
                    .build();

            //무게 + 개수
            if (newRoutineDetail.getRoutineTypeId() == 1) {
                String[] totalArr = param.getDetailTypeContext().get(i).split("/");
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
            exerciseRepository.save(exerciseInfo);
        }catch (Exception e){
            e.printStackTrace();
//            throw new BaseException()
        }
        return "yes!";
    }
}
