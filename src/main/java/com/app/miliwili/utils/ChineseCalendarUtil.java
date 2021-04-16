package com.app.miliwili.utils;

import com.ibm.icu.util.ChineseCalendar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class ChineseCalendarUtil {
    
    /**
     * 디데이 구분 생일, 양력계산기
     * 양력 -> 양력(지난 X, 다가올 날짜)
     * 음력 -> 양력(지난 X, 다가올 날짜)
     * 
     * @param date yyyyMMdd
     * @param choiceCalendar
     * @return String
     */
    public static String convertSolar(String date, String choiceCalendar) {
        if (choiceCalendar.equals("S")) {
            LocalDate targetDate = LocalDate.parse(LocalDate.now().getYear() + "-"+ date);

            if (targetDate.isAfter(LocalDate.now()) || targetDate.isEqual(LocalDate.now())) {
                return targetDate.format(DateTimeFormatter.ISO_DATE);
            }

            return LocalDate.now().plusYears(1).getYear() + "-" + date;
        }

        LocalDate targetDate = LocalDate.parse(convertLunarToSolar(LocalDate.now().getYear() + date.replaceAll("-", "")));
        if (targetDate.isAfter(LocalDate.now()) || targetDate.isEqual(LocalDate.now())) {
            return targetDate.format(DateTimeFormatter.ISO_DATE);
        }

        targetDate = LocalDate.parse(convertLunarToSolar(LocalDate.now().plusYears(1).getYear() + date.replaceAll("-", "")));
        return targetDate.format(DateTimeFormatter.ISO_DATE);
    }




    private static String getDateByString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private static String convertLunarToSolar(String date) {
        ChineseCalendar chineseCalendar = new ChineseCalendar();
        Calendar calendar = Calendar.getInstance();

        chineseCalendar.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
        chineseCalendar.set(ChineseCalendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        chineseCalendar.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        calendar.setTimeInMillis(chineseCalendar.getTimeInMillis());
        return getDateByString(calendar.getTime());
    }
}