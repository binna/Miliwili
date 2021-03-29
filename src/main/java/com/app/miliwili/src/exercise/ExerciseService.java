package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.exercise.dto.*;
import com.app.miliwili.src.exercise.model.*;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.Validation;
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
    private final ExerciseReportRepository exerciseReportRepository;
    private final ExerciseWeightRepository exerciseWeightRepository;
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

        //오늘 이미 썼다면--> 더이상 못씀
        List<ExerciseWeightRecord> weightRecords;
        try {
            weightRecords= exerciseWeightRepository.findExerciseWeightRecordsByExerciseInfo_IdAndStatusAndExerciseDate(exerciseId,
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
    public String modifyDailyWeight(PatchExerciseDailyWeightReq param, long exerciseId) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        LocalDate targetDate = LocalDate.parse(param.getDayDate(), DateTimeFormatter.ISO_DATE);

        List<ExerciseWeightRecord> targetWeightRecord;
        try {
            targetWeightRecord = exerciseWeightRepository.findExerciseWeightRecordsByExerciseInfo_IdAndStatusAndExerciseDate
                    (exerciseId, "Y", targetDate);
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


        saveExerciseRoutine(param, exerciseInfo, newRoutine);

        return newRoutine.getId();
    }


    /**
     * 루틴 수정
     */
    public String modifyRoutine(PostExerciseRoutineReq param, long exerciseId, long routineId) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if (exerciseInfo.getUser().getId() != jwtService.getUserId()) {
            throw new BaseException(INVALID_USER);
        }

        List<ExerciseRoutine> routineList = exerciseInfo.getExerciseRoutines();
        ExerciseRoutine routine = null;
        for(ExerciseRoutine r : routineList){
            if(r.getId() == routineId){
                routine = r;
                break;
            }
        }
        if(routine == null)
            throw new BaseException(NOT_FOUND_ROUTINE);


        routine.setName(param.getRoutineName());
        routine.setBodyPart(param.getBodyPart());
        routine.setRepeaDay(param.getRepeatDay());
        routine.getRoutineDetails().clear();

        saveExerciseRoutine(param, exerciseInfo, routine);

        return "success";

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
        try {
            exerciseRoutineRepository.delete(routine);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_DELETE_ROUTINE);
        }

        return "\""+routine.getName()+"\""+"루틴이 삭제되었습니다";
    }

    /**
     * 운동 리포트 생성
     */
    public Long createExerciseReport(Long exerciseId, Long routineId, PostExerciseReportReq param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        ExerciseRoutine routine = exerciseProvider.findRoutine(routineId, exerciseInfo);
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
        routine.setDone("Y");
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
    public String deleteExerciseReport(Long exerciseId, Long routineId, String reportDate) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        ExerciseRoutine routine = exerciseProvider.findRoutine(routineId, exerciseInfo);

        ExerciseReport report = null;
        LocalDate date = LocalDate.parse(reportDate, DateTimeFormatter.ISO_DATE);
        for(ExerciseReport r: routine.getReports()){
            if(r.getDateCreated().toLocalDate().equals(date)){
                report = r;
                break;
            }
        }
        if(report == null)
            throw new BaseException(FAILED_GET_REPORT);

        if(routine.getDone().equals("Y"))
            routine.setDone("N");

        report.setStatus("N");
        report.setExerciseRoutine(null);
        routine.getReports().remove(report);

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
    public String modifyExerciseReport(Long exerciseId, Long routineId, PatchExerciseReportReq param) throws BaseException{
        ExerciseInfo exerciseInfo = exerciseProvider.getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        ExerciseRoutine routine = exerciseProvider.findRoutine(routineId, exerciseInfo);

        ExerciseReport report = null;
        LocalDate date = LocalDate.parse(param.getReportDate(), DateTimeFormatter.ISO_DATE);
        for(ExerciseReport r: routine.getReports()){
            if(r.getDateCreated().toLocalDate().equals(date)){
                report = r;
                break;
            }
        }
        if(report == null)
            throw new BaseException(NOT_FOUNT_REPORT);
        if(report.getStatus().equals("N"))
            throw new BaseException(NOT_FOUNT_REPORT);

        report.setReportText(param.getReportText());
        try {
            exerciseReportRepository.save(report);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_MODIFY_REPORT);
        }
        return "success";


    }
    /**
     * 루틴 안한상태로 초기화
     */
    public void resetRoutineDone(ExerciseRoutine routine) throws BaseException{
        routine.setDone("N");
        try {
            exerciseRoutineRepository.save(routine);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_CHANGE_ROUTINE_STATUS);
        }
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



}
