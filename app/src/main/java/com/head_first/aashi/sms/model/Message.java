package com.head_first.aashi.sms.model;

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
            this.sentBy = sentBy;
        }
    }

    public String getSentTo() {
        return sentTo;
    }

    private void setSentTo(String sentTo) {
        if(isNumeric(sentTo)){
            this.sentTo = sentTo;
        }
    }

    public String getMessageText() {
        return messageText;
    }

    private void setMessageText(String messageText) {
        this.messageText = messageText;
    }

}
