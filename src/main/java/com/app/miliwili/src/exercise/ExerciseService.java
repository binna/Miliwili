package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.exercise.dto.*;
import com.app.miliwili.src.exercise.model.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.app.miliwili.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRoutineRepository exerciseRoutineRepository;
    private final ExerciseReportRepository exerciseReportRepository;
    private final ExerciseWeightRepository exerciseWeightRepository;
    private final ExerciseProvider exerciseProvider;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    /**
     * 처음 탭에 입장시
     */
    public Long createFirstEntrance() throws BaseException{
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        if(exerciseProvider.isExerciseInfoUser(user.getId()) == true)
            throw new BaseException(FAILED_CHECK_FIRST_WIEHGT);

        ExerciseInfo exerciseInfo = ExerciseInfo.builder()
                .user(user)
                .weightRecords(new ArrayList<>())
                .build();

        try{
            exerciseRepository.save(exerciseInfo);
        }catch (Exception e){
            throw new BaseException(FAILED_POST_FIRST_ENTRANCE);
        }

        return exerciseInfo.getId();
    }

    /**
     *목표 몸무게, 현재 몸무게 입력 ui
     *
     */

    public String createFistWeight(PostExerciseFirstWeightReq param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        Double goalWeight = (param.getGoalWeight() == -1.0) ? null : param.getGoalWeight();
        Double firstWeight = (param.getFirstWeight() == -1.0) ? null : param.getFirstWeight();

        exerciseInfo.setFirstWeight(firstWeight);
        exerciseInfo.setGoalWeight(goalWeight);

        //첫 몸무게 체중 기록에 기록하기
        if(firstWeight != null) {
            ExerciseWeightRecord firstRecord = ExerciseWeightRecord.builder()
                    .weight(firstWeight)
                    .exerciseDate(LocalDate.now())
                    .exerciseInfo(exerciseInfo)
                    .build();

            try {
                exerciseWeightRepository.save(firstRecord);
            } catch (Exception e) {
                throw new BaseException(FAILED_TO_POST_FIRST_WEIGHT);
            }
        }


        return "Success";
    }

    /**
     * 데일리 몸무게 입력
     */
    public String createDayilyWeight(PostExerciseWeightReq param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        //오늘 이미 썼다면--> 더이상 못씀
        List<ExerciseWeightRecord> weightRecords;
        try {
            weightRecords= exerciseWeightRepository.findExerciseWeightRecordsByExerciseInfo_IdAndStatusAndExerciseDate(exerciseInfo.getId(),
                    "Y", LocalDate.now());
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_TODAY_WEIGHT);
        }
        if(weightRecords.size() != 0){
            throw new BaseException(FAILED_TO_POST_WEIGHT_ONE_DAY);
        }

        ExerciseWeightRecord weightRecord = ExerciseWeightRecord.builder()
                .weight(param.getDayWeight())
                .exerciseInfo(exerciseInfo)
                .exerciseDate(LocalDate.now())
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
    public String modifyDailyWeight(PatchExerciseDailyWeightReq param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        LocalDate targetDate = LocalDate.parse(param.getDayDate(), DateTimeFormatter.ISO_DATE);

        List<ExerciseWeightRecord> targetWeightRecord;
        try {
            targetWeightRecord = exerciseWeightRepository.findExerciseWeightRecordsByExerciseInfo_IdAndStatusAndExerciseDate
                    (exerciseInfo.getId(), "Y", targetDate);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_MONTH_WEIGHT_3);
        }

        if (targetWeightRecord.size() != 0) {
            targetWeightRecord.get(0).setWeight(param.getDayWeight());
            try{
                exerciseWeightRepository.save(targetWeightRecord.get(0));
            }catch (Exception e){
                throw new BaseException(FAILED_TO_MODIFY_WEIGHT);
            }

        }else{
            ExerciseWeightRecord newWiehgtRecord = ExerciseWeightRecord.builder()
                    .weight(param.getDayWeight())
                    .exerciseInfo(exerciseInfo)
                    .exerciseDate(targetDate)
                    .build();
            try {
                exerciseWeightRepository.save(newWiehgtRecord);
            }catch (Exception e){
                throw new BaseException(FAILED_TO_MODIFY_WEIGHT);
            }

        }

        return param.getDayWeight()+"kg으로 수정되었습니다.";
    }

    /**
     * 목표 몸무게 수정
     */
    public String modifyGoalWeight(PatchExerciseGoalWeight param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

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
    public Long createRoutine(PostExerciseRoutineReq param) throws BaseException {
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        ExerciseRoutine newRoutine = ExerciseRoutine.builder()
                .name(param.getRoutineName())
                .bodyPart(param.getBodyPart())
                .repeaDay(param.getRepeatDay())
                .exerciseInfo(exerciseInfo)
                .routineDetails(new ArrayList<>())
                .build();


        saveExerciseRoutine(param, exerciseInfo, newRoutine);

        return newRoutine.getId();
    }


    /**
     * 루틴 수정
     */
    public String modifyRoutine(PostExerciseRoutineReq param, long routineId) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        ExerciseRoutine routine = exerciseProvider.getAvaliableRoutine(routineId, exerciseInfo);

        routine.setName(param.getRoutineName());
        routine.setBodyPart(param.getBodyPart());
        routine.setRepeaDay(param.getRepeatDay());
        routine.getRoutineDetails().clear();

        saveExerciseRoutine(param, exerciseInfo, routine);

        return "success";

    }



    /**
     * 루틴 삭제
     * isDelete = 탈퇴된 회원인지 --> 회원 탈퇴를 위해
     */
    public String deleteRoutine( long routineId , boolean isDeleted) throws BaseException{
        ExerciseInfo exerciseInfo = null;
        if(isDeleted == true)       //루틴 삭제
           exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");
        //exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);
        else{                       //회원 탈퇴할 때 함께 삭제
            exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"N");
        }

        if (exerciseInfo.getUser().getId() != jwtService.getUserId()) {
            throw new BaseException(INVALID_USER);
        }

        ExerciseRoutine routine = exerciseProvider.getAvaliableRoutine(routineId, exerciseInfo);
        routine.setStatus("N");
        for(ExerciseRoutineDetail detail: routine.getRoutineDetails()){
            for(ExerciseDetailSet set: detail.getDetailSets()){
                set.setStatus("N");
            }
            detail.setStatus("N");
        }
        for(ExerciseReport report: routine.getReports()){
            report.setStatus("N");
        }

//        routine.setExerciseInfo(null);
//        exerciseInfo.getExerciseRoutines().remove(routine);

        try {
            exerciseRoutineRepository.save(routine);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_DELETE_ROUTINE);
        }

        return routine.getName()+"루틴이 삭제되었습니다";
    }

    /**
     * 운동 리포트 생성
     */
    public Long createExerciseReport(Long routineId, PostExerciseReportReq param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        ExerciseRoutine routine = exerciseProvider.getAvaliableRoutine(routineId, exerciseInfo);
        String[] statusSplit = param.getExerciseStatus().split("#");
        List<ExerciseRoutineDetail> detailList = routine.getRoutineDetails();
        if(statusSplit.length != detailList.size())
            throw new BaseException(FAILED_GET_REPORT_SETSIZE);
        for(int i=0;i<detailList.size();i++){
            if(detailList.get(i).getSetCount() < Integer.parseInt(statusSplit[i]))
                throw new BaseException(FAILED_GET_REPORT_SETCOUNT);
        }

        ExerciseReport newReport = ExerciseReport.builder()
                .totalTime(param.getTotalTime())
                .exerciseStatus(param.getExerciseStatus())
                .reportText("")
                .exerciseRoutine(routine)
                .build();
        routine.addNewReport(newReport);
        try {
            exerciseReportRepository.save(newReport);
            exerciseRoutineRepository.save(routine);
        }catch (Exception e){
            throw new BaseException(FAILED_POST_REPORT);
        }
        return newReport.getId();
    }

    /**
     * 운동 리포트 삭제
     */
    public String deleteExerciseReport(Long routineId, String reportDate) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        ExerciseRoutine routine = exerciseProvider.getAvaliableRoutine(routineId, exerciseInfo);
        ExerciseReport report  = exerciseProvider.getAvaliableExerciseReport(routine, LocalDate.parse(reportDate, DateTimeFormatter.ISO_DATE));

        report.setStatus("N");

        try{
            exerciseReportRepository.save(report);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_DELETE_REPORT_REPORT);
        }

        try {
            exerciseRoutineRepository.save(routine);
        }catch(Exception e) {
            throw new BaseException(FAILED_TO_DELETE_REPORT_ROUTINE);
        }


        return "success";


    }



    /**
     * 운동 리포트 수정
     */
    public String modifyExerciseReport( Long routineId, PatchExerciseReportReq param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfoByUserId(jwtService.getUserId(),"Y");

        ExerciseRoutine routine = exerciseProvider.getAvaliableRoutine(routineId, exerciseInfo);
        ExerciseReport report  = exerciseProvider.getAvaliableExerciseReport(routine, LocalDate.parse(param.getReportDate(), DateTimeFormatter.ISO_DATE));

        report.setReportText(param.getReportText());
        try {
            exerciseReportRepository.save(report);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_MODIFY_REPORT);
        }
        return "success";


    }


    /**
     * 루틴 저장
     * @param param
     * @param exerciseInfo
     * @param newRoutine
     * @throws BaseException
     */
    private void saveExerciseRoutine(PostExerciseRoutineReq param, ExerciseInfo exerciseInfo, ExerciseRoutine newRoutine) throws BaseException {
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
                Validation.validateCountLength(newRoutineDetail, totalArr);
                for (int j = 0; j < totalArr.length; j++) {
                    String[] weightCount = totalArr[j].split("#");
                    ExerciseDetailSet newDetailSet = ExerciseDetailSet.builder()
                            .setIdx(j + 1)
                            .setWeight(Double.parseDouble(weightCount[0]))
                            .setCount(Integer.parseInt(weightCount[1]))
                            .setTime(-1)
                            .exerciseRoutineDetail(newRoutineDetail)
                            .build();
                    //arr추가
                    newRoutineDetail.addDetailSet(newDetailSet);
                }

            }
            // 개수간
            else if (newRoutineDetail.getRoutineTypeId() == 2) {
                String[] totalArr = param.getDetailTypeContext().get(i).split("/");
                Validation.validateCountLength(newRoutineDetail, totalArr);
                for (int j = 0; j < totalArr.length; j++) {
                    ExerciseDetailSet newDetailSet = ExerciseDetailSet.builder()
                            .setIdx(j + 1)
                            .setCount(Integer.parseInt(totalArr[j]))
                            .setTime(-1)
                            .setWeight(-1.0)
                            .exerciseRoutineDetail(newRoutineDetail)
                            .build();
                    //arr추가
                    newRoutineDetail.addDetailSet(newDetailSet);

                }
            }
            //시간
            else {
                String[] totalArr = param.getDetailTypeContext().get(i).split("/");
                Validation.validateCountLength(newRoutineDetail, totalArr);
                for (int j = 0; j < totalArr.length; j++) {
                    ExerciseDetailSet newDetailSet = ExerciseDetailSet.builder()
                            .setIdx(j + 1)
                            .setTime(Integer.parseInt(totalArr[j]))
                            .setWeight(-1.0)
                            .setCount(-1)
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
    }

    /**
     * exerciseInfo 삭제 --> 회원이 삭제된다면 줄줄이 다 status 바꾸도록
     * --> UserService 에
     */
    public void deleteExerciseInfo(Long userId) throws BaseException{
        ExerciseInfo exerciseInfo = null;
        //운동탭 한번도 방문 안해서 exerciseInfo없는 회원 그냥 RETurn
        try {
            exerciseInfo = exerciseProvider.getExerciseInfoByUserId(userId,"Y");
        }catch (BaseException e){
            if(e.getStatus() == NOT_FOUND_EXERCISEINFO)
                return;
        }
        List<ExerciseRoutine> routine = exerciseInfo.getExerciseRoutines();
        List<ExerciseWeightRecord> weightRecords = exerciseInfo.getWeightRecords();
        exerciseInfo.setStatus("N");
        for(ExerciseRoutine r : routine){
            deleteRoutine( r.getId(), false);
        }
        for(ExerciseWeightRecord weight : weightRecords){
            weight.setStatus("N");
        }

        try{
            exerciseRepository.save(exerciseInfo);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_DELTE_EXERCISE_INFO);
        }

    }


    /**
     * 회원삭제 실패시 롤백을 위해
     */
    public void rollbackExerciseInfo(Long userId) throws BaseException{
        ExerciseInfo exerciseInfo = null;
        try {
            exerciseInfo = exerciseProvider.getExerciseInfoByUserId(userId,"N");
        }catch (BaseException e){
            if(e.getStatus() == NOT_FOUND_EXERCISEINFO)
                return;
        }
        List<ExerciseRoutine> routine = exerciseInfo.getExerciseRoutines();
        List<ExerciseWeightRecord> weightRecords = exerciseInfo.getWeightRecords();

        exerciseInfo.setStatus("Y");

        for(ExerciseRoutine r : routine){
            r.setStatus("Y");
            for(ExerciseRoutineDetail detail: r.getRoutineDetails()){
                for(ExerciseDetailSet set: detail.getDetailSets()){
                    set.setStatus("Y");
                }
                detail.setStatus("Y");
            }
            for(ExerciseReport report: r.getReports()){
                report.setStatus("Y");
            }
        }

        for(ExerciseWeightRecord weight : weightRecords){
            weight.setStatus("Y");
        }

        try{
            exerciseRepository.save(exerciseInfo);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_ROLLBACK_EXERCISE_INFO);
        }

    }

}