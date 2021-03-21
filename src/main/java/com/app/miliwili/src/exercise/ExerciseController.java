package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.exercise.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/exercises")
public class ExerciseController {
    private final ExerciseProvider exerciseProvider;
    private final ExerciseService exerciseService;


    /**
     * 처음 운동 탭 입장시 call
     * 목표 체중, 현재 체중 입력 --> 입력 안해도 되지만 안할시에는 체중 기록 사용 불가
     * @return BaseResponse<Long>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/first-weights")
    public BaseResponse<Long> postFirstWeight(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody PostExerciseFirstWeightReq param){
        try{
            Long exerciseId = exerciseService.createFistWeight(param);
            return new BaseResponse<>(SUCCESS,exerciseId);
        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * daily 체중 입력
     * @return BaseResponse<String>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/{exerciseId}/weights")
    public BaseResponse<String> postDailyWeight(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody PostExerciseWeightReq param,
                                                @PathVariable Long exerciseId){
        //TODO: 하루에 한번만 입력되도록 검증
        if(param.getDayWeight() == null)
            return new BaseResponse<>(EMPTY_WEIGHT);

        try{
            String returnStr = exerciseService.createDayilyWeight(param,exerciseId);
            return new BaseResponse<>(SUCCESS,returnStr);
        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(FAILED_POST_DAILY_WEIGHT);
        }
    }

    /**
     * 목표체중 수정
     * @return BaseResponse<String>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther vivi
     */
    @ResponseBody
    @PatchMapping("/{exerciseId}/goal-weights")
    public BaseResponse<String> patchGoalWeight(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody PatchExerciseGoalWeight param,
                                                @PathVariable Long exerciseId){
        if(param.getGoalWeight() == null)
            return new BaseResponse<>(EMPTY_WEIGHT);

        try{
            String returnStr = exerciseService.modifyGoalWeight(param,exerciseId);
            return new BaseResponse<>(SUCCESS,returnStr);
        }catch (BaseException e){            e.printStackTrace();
            return new BaseResponse<>(FAILED_PATCH_GOAL_WEIGHT);
        }
    }

    /**
     * 일별 체중 기록 조회
     * @return BaseResponse<GetExerciseDailyWeight>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther vivi
     */
    @ResponseBody
    @GetMapping("/{exerciseId}/daily-weights")
    public BaseResponse<GetExerciseDailyWeightRes> getDailyWeight(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable Long exerciseId){
        try{
            GetExerciseDailyWeightRes exerciseDailyWeightRes= exerciseProvider.retrieveExerciseDailyWeight(exerciseId);
            return new BaseResponse<>(SUCCESS, exerciseDailyWeightRes);
        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 루틴 생성
     * @return BaseResponse<String>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/{exerciseId}/routines")
    public BaseResponse<String> postRoutines(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable Long exerciseId,
                                             @RequestBody PostExerciseRoutineReq param){
        try{
            String result = exerciseService.createRoutine(param,exerciseId);
            return new BaseResponse<>(SUCCESS,result);
        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 체중 기록 조회
     * @return BaseResponse<String>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther vivi
     */
    @ResponseBody
    @GetMapping("/{exerciseId}/weight-records")
    public BaseResponse<GetExerciseWeightRecordRes> getWeightRecords(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable Long exerciseId,
                                             @RequestParam Integer viewMonth, @RequestParam Integer viewYear){

        LocalDate now = LocalDate.now();
        if(viewYear > now.getYear() || (viewYear==now.getYear() && viewMonth>now.getMonthValue()))
            return new BaseResponse<>(INVALID_VIEW_DATE);
        try{
            GetExerciseWeightRecordRes result = exerciseProvider.retrieveExerciseWeightRecord(viewMonth, viewYear,exerciseId);
            System.out.println("outout again");
            return new BaseResponse<>(SUCCESS,result);
        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

}
