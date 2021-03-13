package com.app.miliwili.utils;

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




}