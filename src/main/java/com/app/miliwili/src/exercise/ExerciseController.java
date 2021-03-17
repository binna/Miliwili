package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.config.BaseResponseStatus;
import com.app.miliwili.src.exercise.dto.PostExerciseFirstWeightReq;
import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.user.UserController;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/exercise")
public class ExerciseController {
    private final ExerciseProvider exerciseProvider;
    private final ExerciseService exerciseService;


    /**
     * 처음 운동 탭 입장시 call
     * 목표 체중, 현재 체중 입력 --> 입력 안해도 되지만 안할시에는 체중 기록 사용 불가
     * @return BaseResponse<Void>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/first-weights")
    public BaseResponse<Long> postFirstWeight(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody PostExerciseFirstWeightReq param){

        try{
            Long exerciseId = exerciseService.createFistWeight(param);
            return new BaseResponse<>(SUCCESS,exerciseId);
        }catch (Exception e){
            e.printStackTrace();
            return new BaseResponse<>(FAILED_TO_DELETE_ORDINARY_LEAVE);
        }
    }
}
