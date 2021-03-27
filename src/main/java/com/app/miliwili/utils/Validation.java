package com.app.miliwili.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    public static boolean isRegexDate(String target) {
        String regex = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
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
}