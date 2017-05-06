package com.head_first.aashi.sms.model;

import android.support.annotation.NonNull;

/**
 * Created by Aashish Indorewala on 04-May-17.
 */

public class Message {
    private String sentBy;
    private String sentTo;
    private String messageText;

    public static boolean isNumeric(String string){
        if(string == null){
            return false;
        }
        else{
            return string != null && string.matches("[-+]?\\d*\\.?\\d+");
        }
    }

    private static String convertToNzNumber(@NonNull String number){
        if(number.isEmpty()){
            return number;
        }
        if(number.charAt(0) == '0'){
            return "+64" + number.substring(1);

        }
        return number;
    }

    public Message(String sentBy, String sentTo, String messageText){
        this.setSentBy(sentBy);
        this.setSentTo(sentTo);
        this.setMessageText(messageText);
    }

    public String getSentBy() {
        return sentBy;
    }

    private void setSentBy(String sentBy) {
        if(isNumeric(sentBy)){
            this.sentBy = convertToNzNumber(sentBy);
        }
    }

    public String getSentTo() {
        return sentTo;
    }

    private void setSentTo(String sentTo) {
        if(isNumeric(sentTo)){
            this.sentTo = convertToNzNumber(sentTo);
        }
    }

    public String getMessageText() {
        return messageText;
    }

    private void setMessageText(String messageText) {
        this.messageText = messageText;
    }


}
