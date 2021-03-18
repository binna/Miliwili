package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseProvider {
    private final ExerciseSelectRepository exerciseSelectRepository;
    private final UserRepository userRepository;


    /**
     * for Service createFistWeight
     * 이미 ExerciseInfo가지는 회원이면 true
     * 없는 회원이라면 false
     * 없어야만 첫 체중, 목표체중 등록 가능
     */
    public Boolean isExerciseInfoUser(Long userId) throws BaseException{
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
}
