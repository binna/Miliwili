package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.exercise.dto.PostExerciseFirstWeightReq;
import com.app.miliwili.src.exercise.dto.PostExerciseWeightReq;
import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.exercise.model.ExerciseWeightRecord;
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
}
