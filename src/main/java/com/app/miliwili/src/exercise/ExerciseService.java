package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.exercise.dto.PostExerciseFirstWeightReq;
import com.app.miliwili.src.exercise.model.ExerciseInfo;
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
}
