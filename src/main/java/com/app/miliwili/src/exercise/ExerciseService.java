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

        ExerciseInfo exerciseInfo = ExerciseInfo.builder()
                .user(user)
                .firstWeight(param.getFirstWeight())
                .goalWeight(param.getGoalWeight())
                .build();

        exerciseRepository.save(exerciseInfo);

        return exerciseInfo.getId();
    }
}
