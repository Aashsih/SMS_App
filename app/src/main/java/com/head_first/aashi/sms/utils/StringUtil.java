package com.head_first.aashi.sms.utils;

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
}
