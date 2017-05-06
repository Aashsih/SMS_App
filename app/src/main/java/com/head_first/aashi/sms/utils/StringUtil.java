package com.head_first.aashi.sms.utils;

import android.support.annotation.NonNull;

/**
 * Created by Aashish Indorewala on 04-May-17.
 */

public class StringUtil {
    public static boolean isNumeric(String string){
        if(string == null){
            return false;
        }
        else{

            return string != null && string.matches("[-+]?\\d*\\.?\\d+");
        }
    }

    public static String convertToNzNumber(@NonNull String number){
        if(number.isEmpty()){
            return number;
        }
        if(number.charAt(0) == '0'){
            return "+64" + number.substring(1);

        }
        return number;
    }
}
