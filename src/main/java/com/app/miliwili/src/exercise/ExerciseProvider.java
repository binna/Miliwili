package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.exercise.dto.GetExerciseDailyWeightRes;
import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.exercise.model.ExerciseWeightRecord;
import com.app.miliwili.src.user.UserRepository;
import com.app.miliwili.utils.JwtService;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseProvider {
    private final ExerciseSelectRepository exerciseSelectRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseWeightRepository exerciseWeightRepository;
    private final JwtService jwtService;



    /**
     * 일별 체중 조회
     */
    @Transactional
    public GetExerciseDailyWeightRes retrieveExerciseDailyWeight(long exerciseId) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);
        List<ExerciseWeightRecord> exerciseDailyWeightList = null;


        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        try{
            exerciseDailyWeightList = exerciseWeightRepository.findTop5ByExerciseInfo_IdAndStatusOrderByDateCreatedDesc(exerciseId,"Y");
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_GET_DAILY_WEIGHT);
        }

        GetExerciseDailyWeightRes dailyWeightRes = GetExerciseDailyWeightRes.builder()
                .goalWeight(exerciseInfo.getGoalWeight())
                .dailyWeightList(getDailyWeightTodailyWeightList(exerciseDailyWeightList))
                .weightDayList(getDailyWeightTodailyDaytList(exerciseDailyWeightList))
                .build();

        return dailyWeightRes;
    }


    /**
     * for Service createFistWeight
     * 이미 ExerciseInfo가지는 회원이면 true
     * 없는 회원이라면 false
     * 없어야만 첫 체중, 목표체중 등록 가능
     */
    public Boolean isExerciseInfoUser(long userId) throws BaseException{
        List<Long> exerciseIdList = null;
        try{
            exerciseIdList = exerciseSelectRepository.getExerciseInfoByUserId(userId);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }

        if(exerciseIdList.size() == 0)
            return false;
        else
            return true;
    }


    /**
     * ExerciseId로 ExerciseInfo Return
     */
    public ExerciseInfo getExerciseInfo(long exerciseId) throws BaseException{
        return exerciseRepository.findByIdAndStatus(exerciseId, "Y")
            .orElseThrow(() -> new BaseException(NOT_FOUND_EXERCISEINFO));
    }

    /**
     * GetExerciseDailywWeightRes에 들어가는 List들 만들기
     */

    public List<String> getDailyWeightTodailyWeightList(List<ExerciseWeightRecord> dailyWeight){
        List<String> changedList = dailyWeight.stream().map(ExerciseWeightRecord -> {
            double weight = ExerciseWeightRecord.getWeight();
            return Double.toString(weight);
        }).collect(Collectors.toList());

        return changedList;
    }

    public List<String> getDailyWeightTodailyDaytList(List<ExerciseWeightRecord>  dailyWeight){
        List<String> changedList = dailyWeight.stream().map(ExerciseWeightRecord -> {
            LocalDate day = ExerciseWeightRecord.getDateCreated().toLocalDate();
            int monthValue = day.getMonthValue();
            int dayValue = day.getDayOfMonth();

            String monthStr = (monthValue < 10) ? ("0"+monthValue) : Integer.toString(monthValue);
            String dayStr = (dayValue < 10) ? ("0"+dayValue) : Integer.toString(dayValue);
            return monthStr+"/"+dayStr;
        }).collect(Collectors.toList());

        return changedList;
    }

}



