package com.app.miliwili.utils;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponseStatus;
import com.app.miliwili.src.exercise.dto.PostExerciseRoutineReq;
import com.app.miliwili.src.exercise.model.ExerciseRoutineDetail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.app.miliwili.config.BaseResponseStatus.*;

public class Validation {
    public static boolean isRegexDate(String target) {
        String regex = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexBirthdayDate(String target) {
        String regex = "9999-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexDateParam(String target) {
        String regex = "^(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[0-1])$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    public static boolean isRegexMonthParam(String target) {
        String regex = "^(19|20)\\d{2}(0[1-9]|1[012])$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static String getCurrentMonth() {
        String regex = "^[1-9]$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(LocalDate.now().getMonthValue() + "");

        if (matcher.find()) {
            return LocalDate.now().getYear() + "-" + "0" + LocalDate.now().getMonthValue();
        }
        return LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue();
    }

    public static boolean isFullString(String target){
        boolean isEmpty=true;

        if(target.length() == 0 || target == null)
            isEmpty = false;

        return isEmpty;
    }

    public static BigDecimal isBigDecimal(BigDecimal parameter) {
        if (Objects.isNull(parameter)) return null;

        return parameter;
    }

    public static String isString(String parameter) {
        if (Objects.isNull(parameter)) return null;

        return parameter;
    }

    public static Integer isInteger(Integer parameter) {
        if (Objects.isNull(parameter)) return Integer.valueOf(0);

        return parameter;
    }

    public static String isLocalDateAndChangeString(LocalDate parameter) {
        if(Objects.isNull(parameter)) return null;

        return parameter.format(DateTimeFormatter.ISO_DATE);
    }

    public static String getPrintStackTrace(Exception exception) {

        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));

        return errors.toString();
    }

    public static BaseResponseStatus validateForPostExerciseRoutineReq(PostExerciseRoutineReq param){
        if(param.getDetailName().size() != param.getDetailType().size() || param.getDetailType().size() != param.getDetailType().size() ||
                param.getDetailTypeContext().size() != param.getDetailSetEqual().size() || param.getDetailSetEqual().size() != param.getDetailSet().size()){
            return INVALIED_ARRAY_LENGTH;
        }
        if(param.getRepeatDay().contains("8") && param.getRepeatDay().length() != 1) {
            return INVALIED_DETAIL_TYPE;
        }
        String[] repeatDayList = param.getRepeatDay().split("#");
        for(String day: repeatDayList){
            int dayInt = Integer.parseInt(day);
            if(dayInt>8 ||dayInt<1 )
                return INVALIED_REPEATDAY;
        }

        for(int i=0;i<param.getDetailSet().size() ;i++){
            //입력한 세트의 수와 세트 정보의 개수가 다른 처리
            String[] splitArr = param.getDetailTypeContext().get(i).split("/");
            if(param.getDetailSetEqual().get(i) == true){   //전 세트동일이면
                if(splitArr.length != 1)
                    return INVALIED_SAME_SETS;

            }else{                                          //전 세트 동일이 아니면
                if(splitArr.length != param.getDetailSet().get(i))
                    return INVALIED_DETAIL_CONTEXT_ARRAY_LENGTH_AND_SETCOUNT;
            }
            //무게+개수 옵션이 아니면 그냥 단순 숫자배열임을 확인
            if(param.getDetailType().get(i) == 1){
                if(!param.getDetailTypeContext().get(i).contains("#"))
                    return INVALIED_DETAILTYPE;
                for(int j=0; j< splitArr.length ;j++) {
                    String[] weightCountArr = splitArr[j].split("#");
                    if (weightCountArr.length != 2)
                        return INVALIED_STRING_WEIGHTPLUSCOUNT;
                    try{
                        Double parsing = Double.parseDouble(weightCountArr[0]);
                    }catch (Exception e){
                        return INVALIED_DETAILTYPE_WEIGHTCOUNT;
                    }
                }
            }
            else{
                if(param.getDetailTypeContext().get(i).contains("#"))
                    return INVALIED_DETAILTYPE;
                for(int j=0;j<splitArr.length;j++){
                    try{
                        Integer parsing = Integer.parseInt(splitArr[j]);
                    }catch (Exception e){
                        return INVALIED_DETAILTYPE_COUNT_TIME;
                    }
                }
            }
        }
        return VALIDATION_SUCCESS;
    }

    public static BaseResponseStatus validateReportTotalTime(String totalTime){
        String[] timeSplit;
        try{
            timeSplit = totalTime.split(":");
        }catch (Exception e){
            return INVALIED_TOTALTIME_TYPE;
        }
        if(timeSplit.length != 3)
            return INVALIED_TOTALTIME_TYPE;

        return VALIDATION_SUCCESS;
    }

    public static void validateCountLength(ExerciseRoutineDetail newRoutineDetail, String[] totalArr) throws BaseException {
        if(newRoutineDetail.getIsSame().equals("N")) {
            if (newRoutineDetail.getSetCount() != totalArr.length)
                throw new BaseException(INVALID_SETCOUNT);
        }
    }
}