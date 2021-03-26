package com.app.miliwili.src.exercise;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponseStatus;
import com.app.miliwili.src.exercise.dto.*;
import com.app.miliwili.src.exercise.model.*;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseProvider {
    private final ExerciseSelectRepository exerciseSelectRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseWeightRepository exerciseWeightRepository;
    private final ExerciseRoutineRepository exerciseRoutineRepository;
    private final ExerciseReportRepository exerciseReportRepository;
    private final JwtService jwtService;


    /**
     * ExerciseId로 ExerciseInfo Return
     */
    public ExerciseInfo getExerciseInfo(long exerciseId) throws BaseException{
        return exerciseRepository.findByIdAndStatus(exerciseId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_EXERCISEINFO));
    }

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
            exerciseDailyWeightList = exerciseWeightRepository.findTop5ByExerciseInfo_IdAndStatusOrderByExerciseDateDesc(exerciseId,"Y");
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
     * 체중 기록 조회
     */
    @Transactional
    public GetExerciseWeightRecordRes retrieveExerciseWeightRecord(Integer viewMonth, Integer viewYear, Long exerciseId) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);
        List<ExerciseWeightRecord> allRecordList = exerciseInfo.getWeightRecords();
        List<ExerciseWeightRecord> exerciseWeightList = new ArrayList<>();


        if (exerciseInfo.getUser().getId() != jwtService.getUserId()) {
            throw new BaseException(INVALID_USER);
        }

        //생성 날짜 오름차순 정렬
        Collections.sort(allRecordList, new Comparator<ExerciseWeightRecord>() {
            @Override
            public int compare(ExerciseWeightRecord o1, ExerciseWeightRecord o2) {
                return o1.getExerciseDate().compareTo(o2.getExerciseDate());
            }
        });

        //지정한 달의 모든 몸무게 정보 가져오기
        for(int i=0;i<allRecordList.size();i++){
            ExerciseWeightRecord record = allRecordList.get(i);
            if(record.getExerciseDate().getYear() == viewYear && record.getExerciseDate().getMonthValue() == viewMonth){
                exerciseWeightList.add(record);
            }
        }

        //최근 5개월 가져와서 평균내기
        //month에 들어갈애들
        List<String> monthWeightMonth = new ArrayList<>();
        List<Double> monthWeight =new ArrayList<>();

        int idx=1;
        int continueIndx=1;
        int nowMonth = LocalDate.now().getMonthValue();
        int nowYear = LocalDate.now().getYear();

        int wantMonth = nowMonth ;
        int wantYear = nowYear;

        int lastIdx=0;
        while(idx <= 5 && continueIndx <=3) {
            List<ExerciseWeightRecord> monthWeightList = new ArrayList<>();
            double sum = 0.0;

            if (wantMonth == 0) {
                wantYear--;
                wantMonth = 12;
            }

            for (int i = 0; i < allRecordList.size(); i++) {
                ExerciseWeightRecord record = allRecordList.get(i);
                if (record.getExerciseDate().getYear() == wantYear && record.getExerciseDate().getMonthValue() == wantMonth) {
                    monthWeightList.add(record);
                }

            }
            if (monthWeightList.size() == 0) {
                wantMonth--;
                continueIndx++;
                continue;
            }

            monthWeightMonth.add(wantMonth + "월");

            for (int k = 0; k < monthWeightList.size(); k++) {
                sum += monthWeightList.get(k).getWeight();
            }
            double avg = sum / (monthWeightList.size());
            monthWeight.add(Math.round(avg * 100) / 100.0);
            System.out.println(idx);
            idx++;
            wantMonth--;

        }
        for(int i=0;i<monthWeight.size();i++){
            System.out.println(monthWeight.get(i));
        }
        for(int i=0;i<monthWeight.size();i++){
            System.out.println(monthWeightMonth.get(i));
        }

        GetExerciseWeightRecordRes getExerciseWeightRecordRes = GetExerciseWeightRecordRes.builder()
                .goalWeight(exerciseInfo.getGoalWeight())
                .monthWeight(monthWeight)
                .monthWeightMonth(monthWeightMonth)
                .dayWeightDay(dayWeightDayList(viewYear,viewMonth))
                .dayWeight(dayWeightListWeight((dayweightList(viewYear, viewMonth,exerciseWeightList)),viewYear,viewMonth))
                .dayDif(dayWeightListDif(exerciseInfo.getGoalWeight(),dayweightList(viewYear, viewMonth,exerciseWeightList),viewYear,viewMonth))
                .build();

        return getExerciseWeightRecordRes;
    }

    //몇월 몇일인지 출력
    public List<String> dayWeightDayList( int year, int month){
        List<String> dayList= new ArrayList<>();
        LocalDate standardMonth = LocalDate.of(year,month,1);
        int moveDay = 1;
        int monthInt = standardMonth.getMonthValue();
        LocalDate moveMonth = standardMonth;

        while(moveMonth.getMonthValue() == standardMonth.getMonthValue()){
            dayList.add(monthInt+"월"+moveMonth.getDayOfMonth()+"일");
            try {
                moveMonth = LocalDate.of(year, month, ++moveDay);
            }catch (Exception e){
                break;
            }
        }
        return dayList;
    }

    //몇월 몇일에 몸무게가 얼마였는지 출력  --> int형 --> 이후 차이 계산을 위해  --> 얘는 그대로 쓰이는데 없음
    public List<Double> dayweightList(int year, int month, List<ExerciseWeightRecord> recordList){
        List<Double> dayWeightList = new ArrayList<>();
        int index=0;
        int moveDay = 1;
        boolean isEndIndx=false;
        LocalDate standardMonth = LocalDate.of(year,month,1);
        LocalDate moveMonth = standardMonth;
        if(recordList.size()==0){
            String montStr = (month<10) ? ("0"+month) : (month+"");
            LocalDate inputDate = LocalDate.parse((year+"-"+montStr+"-01"),DateTimeFormatter.ISO_DATE);
            int lastDate = inputDate.lengthOfMonth();
            for(int i=0;i<lastDate ; i++){
                dayWeightList.add(0.0);
            }
            return dayWeightList;
        }
        while(moveMonth.getMonthValue() == standardMonth.getMonthValue()){
            if(isEndIndx == true) {
                dayWeightList.add(0.0);
                try {
                    moveMonth = LocalDate.of(year, month, ++moveDay);
                }catch (Exception e){
                    break;
                }
                continue;
            }
            if(moveMonth.isEqual(recordList.get(index).getExerciseDate())){
                dayWeightList.add(recordList.get(index).getWeight());
                if(index == recordList.size()-1) {
                    isEndIndx = true;
                }else{
                    index++;
                }
            }else{
                dayWeightList.add(0.0);
            }

            try {
                moveMonth = LocalDate.of(year, month, ++moveDay);
            }catch (Exception e){
                break;
            }
        }

        return dayWeightList;
    }

    //dayWeight변환
    public List<String> dayWeightListWeight(List<Double> weightList, int year, int month){
        System.out.println("ListWeight");

        List<String> changedList = new ArrayList<>();

        for(int i=0;i<weightList.size();i++){
            if(weightList.get(i) == 0.0){
                changedList.add("정보 없음");
            }else {
                changedList.add((weightList.get(i)).toString());
            }
        }
        return changedList;
    }
    //차이 변환
    public List<Double> dayWeightListDif(double goalWeight, List<Double> weightList, int year, int month){
        List<Double> changedList = new ArrayList<>();
        if(weightList.size()==0){
            LocalDate inputDate = LocalDate.parse((year+"-"+month+"-1"),DateTimeFormatter.ISO_DATE);
            int lastDate = inputDate.lengthOfMonth();
            for(int i=0;i<lastDate ; i++){
                changedList.add(0.0);
            }
            return changedList;

        }
            for (int i = 0; i < weightList.size(); i++) {
                if (weightList.get(i) == 0.0) {
                    changedList.add(0.0);
                } else {
                    changedList.add(Math.round((weightList.get(i) - goalWeight) * 100) / 100.0);
                }
            }

        return changedList;
    }


    /**
     * 사용자의 전체 루틴 조회
     */
    public List<MyRoutineInfo> retrieveAllRoutineList(long exerciseId) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);

        if (exerciseInfo.getUser().getId() != jwtService.getUserId()) {
            throw new BaseException(INVALID_USER);
        }

        List<ExerciseRoutine> routineList= exerciseInfo.getExerciseRoutines();
        List<MyRoutineInfo> myAllRoutines = new ArrayList<>();
        for(int i=0; i< routineList.size(); i++){
            ExerciseRoutine routine = routineList.get(i);
            MyRoutineInfo myRoutineInfo = MyRoutineInfo.builder()
                    .routineName(routine.getName())
                    .routineRepeatDay(repeatDayChange(routine.getRepeaDay()))
                    .routineId(routine.getId())
                    .build();
            myAllRoutines.add(myRoutineInfo);
        }
        return myAllRoutines;
    }

    /**
     * 특정 날짜 routine조회
     */
    public List<RoutineInfo> retrieveDateRoutine(long exerciseId, String targetDate) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        List<ExerciseRoutine> routineList = exerciseInfo.getExerciseRoutines();
        List<RoutineInfo> returnList = new ArrayList<>();

        LocalDate target = LocalDate.parse(targetDate, DateTimeFormatter.ISO_DATE);
        String dayofWeekStr = Integer.toString(target.getDayOfWeek().getValue());
        //1:일 2:월 3:화 4:수 5:목 6:금 7:토

        LocalDateTime targetReportDate = LocalDateTime.parse(target+"T00:00:00");
        LocalDateTime targetReportLastDate = LocalDateTime.parse(target+"T23:59:59");

        for(int i=0;i<routineList.size();i++){
            ExerciseRoutine routine = routineList.get(i);
            Boolean isDone = (routine.getDone().equals("Y")) ? true : false;

            //그 날짜에 루틴의 운동 기록이 있다면 true처리 
            List<ExerciseReport> reports = routine.getReports();
            for(ExerciseReport r : reports){
                if(r.getDateCreated().isAfter(targetReportDate) && r.getDateCreated().isBefore(targetReportLastDate))
                    isDone = true;
            }

            if(routine.getRepeaDay().contains(dayofWeekStr) || routine.getRepeaDay().equals("8")){
                RoutineInfo routineInfo = RoutineInfo.builder()
                        .routineName(routine.getName())
                        .routineRepeatDay(repeatDayChange(routine.getRepeaDay()))
                        .isDoneRoutine(isDone)
                        .routineId(routine.getId())
                        .build();
                returnList.add(routineInfo);
            }
        }

        return returnList;

    }
    /**
     * 오늘 운동 완료 처리된 루틴들 조회
     */
    public List<ExerciseRoutine> getCompleteRoutine() throws BaseException{
        List<ExerciseRoutine> routineList = new ArrayList<>();
        try {
            routineList = exerciseRoutineRepository.findAllByStatusAndAndDone("Y", "Y");
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_FIND_GET_ROUTINE);
        }
        return routineList;
    }

    /**
     * 운동 루틴 상세 조회 --> 루틴 수정전에 보여지기 위해
     */
    public GetExerciseRoutineRes retrieveRoutineDetailForPatchRoutine(long exerciseId, long routineId) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }

        ExerciseRoutine routine = findRoutine(routineId, exerciseInfo);

        List<ExerciseRoutineDetail> detailList = routine.getRoutineDetails();
        List<ExerciseDetailRes> detailResList = new ArrayList<>();

        for( ExerciseRoutineDetail detail: detailList){
            List<ExerciseDetailSet> setList = detail.getDetailSets();
            List<ExerciseDetailSetRes> setResList = new ArrayList<>();

            if(detail.getIsSame().equals("Y")){              //전세트 동일
                ExerciseDetailSetRes setRes = ExerciseDetailSetRes.builder()
                        .setStr(detail.getSetCount()+"세트")
                        .weight((setList.get(0).getSetWeight()))
                        .count(setList.get(0).getSetCount())
                        .time(setList.get(0).getSetTime())
                        .build();
                setResList.add(setRes);
            }else {                                 //전세트 동일 아님
                for (int i = 0; i < setList.size(); i++) {
                    ExerciseDetailSetRes setRes = ExerciseDetailSetRes.builder()
                            .setStr((i + 1) + "세트")
                            .weight(setList.get(i).getSetWeight())
                            .count(setList.get(i).getSetCount())
                            .time(setList.get(i).getSetTime())
                            .build();
                    setResList.add(setRes);
                }
            }
            ExerciseDetailRes detailRes = ExerciseDetailRes.builder()
                    .exerciseName(detail.getName())
                    .exerciseType(detail.getRoutineTypeId())
                    .setCount(detail.getSetCount())
                    .isSetSame((detail.getIsSame().equals("Y")) ? true: false)
                    .setDetailList(setResList)
                    .build();
            detailResList.add(detailRes);
        }


        GetExerciseRoutineRes exerciseRoutineRes = GetExerciseRoutineRes.builder()
                .routineName(routine.getName())
                .bodyPart(routine.getBodyPart())
                .repeatDay(changeRepeatDayStrtoArrayList(routine.getRepeaDay()))
                .detailResList(detailResList)
                .build();

        return exerciseRoutineRes;
    }



    /**
     * 루틴 상세 정보 조회 --> 운동 시작 때 필요
     */
    public GetStartExerciseRes retrieveRoutineInfoForStartExercise(long exerciseId, long routineId) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }
        ExerciseRoutine routine = findRoutine(routineId, exerciseInfo);

        List<ExerciseRoutineDetail> detailList = routine.getRoutineDetails();
        List<GetStartExerciseDetailRes> detailResList = new ArrayList<>();

        for( ExerciseRoutineDetail detail: detailList){
            List<ExerciseDetailSet> setList = detail.getDetailSets();
            List<GetStartExerciseDetailSetRes> setResList = new ArrayList<>();

            if(detail.getIsSame().equals("Y")){              //전세트 동일
                setDetailSetToRes(detail, setList, setResList, true,0, detail.getSetCount());
            }else {                                 //전세트 동일 아님
                for (int i = 0; i < setList.size(); i++) {
                    setDetailSetToRes(detail, setList, setResList,false, i, detail.getSetCount());

                }
            }
            GetStartExerciseDetailRes detailRes = GetStartExerciseDetailRes.builder()
                    .exerciseName(detail.getName())
                    .setInfoList(setResList)
                    .build();
            detailResList.add(detailRes);
        }


        GetStartExerciseRes exerciseRoutineRes = GetStartExerciseRes.builder()
                .routineName(routine.getName())
                .repeatDay(repeatDayChange(routine.getRepeaDay()))
                .exerciseList(detailResList)
                .build();

        return exerciseRoutineRes;
    }



    /**
     * 운동 리포트 조회
     */
    public GetExerciseReportRes retrieveExerciseReport(Long exerciseId, Long routineId, String reportDate) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }
        ExerciseRoutine routine = findRoutine(routineId, exerciseInfo);
        if(routine.getDone().equals("N"))
            throw new BaseException(FAILED_GET_REPORT_DONE);


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

        String[] doneSplit = report.getExerciseStatus().split("#");

        List<ExerciseRoutineDetail> detailList = routine.getRoutineDetails();
        List<ReportExercise> exerciseList = new ArrayList<>();

        for( int k=0;k<detailList.size();k++){
            ExerciseRoutineDetail detail = detailList.get(k);
            List<ExerciseDetailSet> setList = detail.getDetailSets();
            List<GetStartExerciseDetailSetRes> setResList = new ArrayList<>();

            if(detail.getIsSame().equals("Y")){              //전세트 동일
                setDetailSetToRes(detail, setList, setResList, true,0, detail.getSetCount());
            }else {                                 //전세트 동일 아님
                for (int i = 0; i < setList.size(); i++) {
                    setDetailSetToRes(detail, setList, setResList,false, i, detail.getSetCount());
                }
            }

            int doneSetInt = Integer.parseInt(doneSplit[k]);
            int setCount = detail.getSetCount();
            boolean isDone = (doneSetInt==setCount) ? true:false;
            String doneOrNone = (isDone)? "완료":"미완";
            String exerciseStatus = "("+doneSetInt+"/"+setCount+") "+doneOrNone;
            ReportExercise reportExercise = ReportExercise.builder()
                    .exerciseName(detail.getName())
                    .exerciseStatus(exerciseStatus)
                    .doneSet(Integer.parseInt(doneSplit[k]))
                    .isDone(isDone)
                    .setList(setResList)
                    .build();
            System.out.println(reportExercise.getExerciseName());
            exerciseList.add(reportExercise);

        }

        GetExerciseReportRes getExerciseReportRes = GetExerciseReportRes.builder()
                .totalTime(report.getTotalTime())
                .reportDate(date.getMonthValue()+"월 "+date.getDayOfMonth()+"일")
                .reportText(report.getReportText())
                .exerciseList(exerciseList)
                .build();

        return getExerciseReportRes;
    }

    /**
     * 캘린더 운동 기록 조회
     */
    public List<String> retrieveCalendarReport(Long exerciseId, Integer viewYear, Integer viewMonth) throws BaseException{
        ExerciseInfo exerciseInfo = getExerciseInfo(exerciseId);

        if(exerciseInfo.getUser().getId() != jwtService.getUserId()){
            throw new BaseException(INVALID_USER);
        }
        List<String> dateList = new ArrayList<>();
        List<ExerciseRoutine> routineList = exerciseInfo.getExerciseRoutines();

        String startDateStr = viewYear+"-" + ((viewMonth>=10) ? viewMonth : ("0"+viewMonth))+"-01";
        int targetLocallength = LocalDate.parse(startDateStr,DateTimeFormatter.ISO_DATE).lengthOfMonth(); //월의 마지막 날짜 계산
        LocalDateTime targetDate = LocalDateTime.parse(startDateStr+"T00:00:00");

        String starEndtDateStr = viewYear+"-" + ((viewMonth>=10) ? viewMonth : ("0"+viewMonth))+ "-"+targetLocallength;
        LocalDateTime targetLastDate = LocalDateTime.parse(starEndtDateStr+"T23:59:59");

        for(int i=0; i<routineList.size() ; i++){
            List<ExerciseReport> reports;
            try {
                 reports= exerciseReportRepository.findExerciseReportByExerciseRoutine_IdAndStatusAndDateCreatedBetween(routineList.get(i).getId(),
                        "Y", targetDate, targetLastDate);
            }catch (Exception e){
                throw new BaseException(FAILED_GET_REPORT);
            }
            System.out.println(reports.size()+"reports");


            for(int j=0; j<reports.size();j++){
                dateList.add(reports.get(j).getDateCreated().toLocalDate().toString());
//                System.out.println(dateList.get(j));
            }

        }

        return dateList;
    }



    /***
     * *****************************************************데이터 정제
     */


    /**
     * 운동 시작을 위한 루틴 조회 --> ExerciseDetailSet 데이터 정제를 위해
     * @param detail
     * @param setList
     * @param setResList
     * @param index --> -1 이면 전 세트 동일 , 아니면 for문에 돌아가는 i값받아오기
     * @param setCount --> 총 세트
     */
    private void setDetailSetToRes(ExerciseRoutineDetail detail, List<ExerciseDetailSet> setList,
                                   List<GetStartExerciseDetailSetRes> setResList, boolean isSame,int index, int setCount) {
        int setCountInt = (isSame == true) ? setCount : index+1;
        if(detail.getRoutineTypeId() == 1) {
            String weightStr = Double.toString(setList.get(index).getSetWeight()*10);
            char lastStr = weightStr.charAt(weightStr.length()-1);
            String changedWeight = (lastStr == '0') ? setList.get(index).getSetWeight()+"kg" : (setList.get(index).getSetWeight().intValue())+"kg";

            GetStartExerciseDetailSetRes setRes = GetStartExerciseDetailSetRes.builder()
                    .setCount(setCountInt)
                    .weight(doubleWeightToString(setList.get(index).getSetWeight()))
                    .count(setList.get(index).getSetCount()+"개")
                    .time(-1+"")
                    .build();
            setResList.add(setRes);
        }else if(detail.getRoutineTypeId() ==2){
            GetStartExerciseDetailSetRes setRes = GetStartExerciseDetailSetRes.builder()
                    .setCount(setCountInt)
                    .weight(-1+"")
                    .count(setList.get(index).getSetCount()+"개")
                    .time(-1+"")
                    .build();
            setResList.add(setRes);
        }else{
            GetStartExerciseDetailSetRes setRes = GetStartExerciseDetailSetRes.builder()
                    .setCount(setCountInt)
                    .weight(-1+"")
                    .count(-1+"")
                    .time(secToTimeFormat(setList.get(index).getSetTime()))
                    .build();
            setResList.add(setRes);
        }
    }
    /**
     * Double형 Weight --> 만약 딱 나눠떨어지는 double이라면 그냥 int형태처럼 return
     * 4.0 --> 4kg
     */
    private String doubleWeightToString(Double weight) {
        Double weightMulti = weight*10;
        String weightStr = Integer.toString(weightMulti.intValue());
        char lastStr = weightStr.charAt(weightStr.length()-1);
        String changedWeight = (lastStr != '0') ? weight+"kg" : (weight.intValue())+"kg";
        return changedWeight;
    }

    /**
     * repeatDay String --> 보기 이쁜 String
     */
    public String repeatDayChange(String str){
        String[] arr = str.split("#");
        String changedStr = "";
        for(int j=0;j<arr.length;j++){
            switch (arr[j]){
                case "1":
                    changedStr+="월,";
                    break;
                case "2":
                    changedStr+="화,";
                    break;
                case "3":
                    changedStr+="수,";
                    break;
                case "4":
                    changedStr+="목,";
                    break;
                case "5":
                    changedStr+="금,";
                    break;
                case "6":
                    changedStr+="토,";
                    break;
                case "7":
                    changedStr+="일,";
                    break;
                case "8":
                    changedStr+="매일,";
                    break;
                default:
                    break;

            }
        }
        String resultStr = changedStr.substring(0,changedStr.length()-1);

        return resultStr;
    }
    /**
     * repeatDayStr -> ArrayList
     */
    public List<Integer> changeRepeatDayStrtoArrayList(String repeatDay){
        List<Integer> changedList= new ArrayList<>();
        String[] splitArr = repeatDay.split("#");
        for(String str: splitArr){
            changedList.add(Integer.parseInt(str));
        }

        return changedList;
    }


//
//    /**
//     * 생성 날짜로 exerciseWeightRecord찾기
//     */
//    public ExerciseWeightRecord getExerciseWiehgtRecord(long exerciseId, LocalDateTime targetDate, LocalDateTime targetNextDate) throws BaseException{
//       return exerciseWeightRepository.findExerciseWeightRecordsByExerciseInfo_IdAndStatusAndDateCreatedBetween
//               (exerciseId, "Y", targetDate, targetNextDate)
//                    .orElseThrow(() -> null);
//    }



    /**
     *RoutineId로 루틴 찾기
     */
    @NotNull
    public ExerciseRoutine findRoutine(long routineId, ExerciseInfo exerciseInfo) throws BaseException {
        ExerciseRoutine routine = null;
        for(ExerciseRoutine r: exerciseInfo.getExerciseRoutines()){
            if(r.getId() == routineId){
                routine = r;
                break;
            }
        }
        if(routine == null)
            throw new BaseException(NOT_FOUND_ROUTINE);
        return routine;
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
            LocalDate day = ExerciseWeightRecord.getExerciseDate();
            int monthValue = day.getMonthValue();
            int dayValue = day.getDayOfMonth();

            String monthStr = (monthValue < 10) ? ("0"+monthValue) : Integer.toString(monthValue);
            String dayStr = (dayValue < 10) ? ("0"+dayValue) : Integer.toString(dayValue);
            return monthStr+"/"+dayStr;
        }).collect(Collectors.toList());

        return changedList;
    }

    public String secToTimeFormat(int sec){
        int hour, min;
        min = sec / 60;
        hour = min / 60;
        sec = sec % 60;
        min = min % 60;
        return hour+"시간 "+min+"분 "+sec+"초 ";

    }
}



