package com.head_first.aashi.sms.controller.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.model.Message;
import com.head_first.aashi.sms.utils.DialogBoxDisplayHandler;

import java.util.ArrayList;

/**
 * Created by Aashish Indorewala on 06-May-17.
 */

public abstract class SMSSenderActivity extends AppCompatActivity {
    private static final int SINGLE_SMS_LENGTH = 160;
    private static final int MULTI_PART_MESSAGE_UNIT_LENGTH = 153;
    private static final String SMS_SENT = "SMS_SENT";
    private static final int SMS_SENT_CONFIRMATION = 1;

    private SentMessageReceiver sentMessageReceiver;

    @Override
    public void onResume() {
        super.onResume();
        if(sentMessageReceiver == null){
            sentMessageReceiver = new SentMessageReceiver();
            registerReceiver(sentMessageReceiver, new IntentFilter(SMS_SENT));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(sentMessageReceiver != null){
            unregisterReceiver(sentMessageReceiver);
            sentMessageReceiver = null;
        }
    }

    public boolean sendSMS(@NonNull Message message){
        if(!message.getMessageText().isEmpty()){
            if(sentMessageReceiver == null){
                sentMessageReceiver = new SentMessageReceiver();
                registerReceiver(sentMessageReceiver, new IntentFilter(SMS_SENT));
            }
            ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
            for(int i = 0; i < (message.getMessageText().length() / SINGLE_SMS_LENGTH )+ 1; i++){
                pendingIntents.add(PendingIntent.getBroadcast(this, SMS_SENT_CONFIRMATION,
                        new Intent(SMS_SENT), 0));
            }
            message.setSentByCurrentDevice(true);
            sentMessageReceiver.setMessage(message);
            SmsManager smsManager = SmsManager.getDefault();
            if(message.getMessageText().length() > SINGLE_SMS_LENGTH){
                smsManager.sendMultipartTextMessage(message.getSentTo(), null, convertMessageIntoList(message.getMessageText()), pendingIntents, null);
            }
            else{
                smsManager.sendTextMessage(message.getSentTo(), null, message.getMessageText(),
                        PendingIntent.getBroadcast(this, SMS_SENT_CONFIRMATION,
                                new Intent(SMS_SENT), 0), null);
            }
            return true;
        }
        return false;
    }

    private ArrayList<String> convertMessageIntoList(@NonNull String message){
        ArrayList<String> wholeMessage = new ArrayList<>();
        if(message.length() < MULTI_PART_MESSAGE_UNIT_LENGTH){
            wholeMessage.add(message);
        }
        else{
            for (int start = 0; start < message.length(); start += MULTI_PART_MESSAGE_UNIT_LENGTH) {
                wholeMessage.add(message.substring(start, Math.min(message.length(), start + MULTI_PART_MESSAGE_UNIT_LENGTH)));
            }
        }
        return wholeMessage;
    }

    private class SentMessageReceiver extends BroadcastReceiver{
        private Message message;

        public void setMessage(@NonNull Message message){
            this.message = message;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:{
                    MessageHistoryDatabase.getInstance(context).addMessageToDatabase(message);
                    if(SMSSenderActivity.this instanceof SMSChatActivity){
                        ((SMSChatActivity)SMSSenderActivity.this).refreshScreen();
                    }
                    else if(SMSSenderActivity.this instanceof NewMessageActivity){
                        ((NewMessageActivity)SMSSenderActivity.this).onMessageSent();
                    }
                    Toast.makeText(context, R.string.smsSentConfirmation, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:{
                    Toast.makeText(context, R.string.smsNotSent, Toast.LENGTH_SHORT).show();
                }
            }
            DialogBoxDisplayHandler.dismissProgressDialog();
        }
    }

}
