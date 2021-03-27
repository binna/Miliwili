package com.app.miliwili.utils;

import com.ibm.icu.util.ChineseCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChineseCalendarUtil {

    public static String getDateByString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 음력 -> 양력 변환
     *
     * @param yyyyMMdd String
     * @return yyyy-MM-dd String
     */
    public static String convertLunarToSolar(String date) {
        ChineseCalendar chineseCalendar = new ChineseCalendar();
        Calendar calendar = Calendar.getInstance();

        chineseCalendar.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
        chineseCalendar.set(ChineseCalendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        chineseCalendar.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        calendar.setTimeInMillis(chineseCalendar.getTimeInMillis());
        return getDateByString(calendar.getTime());
    }

    /**
     * 양력 -> 음력
     *
     * @param yyyyMMdd String
     * @return yyyy-MM-dd String
     */
    public static String converSolarToLunar(String date) {
        ChineseCalendar chineseCalendar = new ChineseCalendar();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        chineseCalendar.setTimeInMillis(calendar.getTimeInMillis());

        int y = chineseCalendar.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
        int m = chineseCalendar.get(ChineseCalendar.MONTH) + 1;
        int d = chineseCalendar.get(ChineseCalendar.DAY_OF_MONTH);

        StringBuffer ret = new StringBuffer();
        ret.append(String.format("%04d", y)).append("-");
        ret.append(String.format("%02d", m)).append("-");
        ret.append(String.format("%02d", d));

        return ret.toString();
    }
}