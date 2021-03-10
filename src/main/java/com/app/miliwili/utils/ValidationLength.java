package com.app.miliwili.utils;

import org.springframework.stereotype.Service;

@Service
public class ValidationLength {

    public static boolean isFullString(String target){
        boolean isEmpty=true;

        if(target.length() == 0 || target == null)
            isEmpty = false;

        return isEmpty;

    }



}
