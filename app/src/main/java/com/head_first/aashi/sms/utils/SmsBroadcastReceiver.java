package com.head_first.aashi.sms.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.controller.activity.SMSChatActivity;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.model.Message;

/**
 * Created by Aashish Indorewala on 06-May-17.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String SMS_PDUS = "pdus";
    private static final String FORMAT = "format";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if(intentExtras != null){
            Object[] smsPdus = (Object[]) intentExtras.get(SMS_PDUS);
            if(smsPdus.length == 0){
                return;
            }
            String messageBody = "";
            String sentBy = null;
            for(int i = 0; i < smsPdus.length; i++){
                String format = intentExtras.getString(FORMAT);
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) smsPdus[i], format);
                if(i == 0){
                    //sentBy = smsMessage.getOriginatingAddress();
                    sentBy = smsMessage.getOriginatingAddress();
                }
                messageBody += smsMessage.getMessageBody().toString();
            }
            MessageHistoryDatabase.getInstance(context).addMessageToDatabase(new Message(sentBy
                    ,PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.currentDevicePhoneNumber), null)
                    , messageBody, false));
            Intent smsChatActivityIntent = new Intent(context, SMSChatActivity.class);
            //smsChatActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            smsChatActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            smsChatActivityIntent.putExtra(context.getResources().getString(R.string.otherContactPhoneNumber), sentBy);
            context.startActivity(smsChatActivityIntent);

//            Intent smsChatActivityIntent = new Intent();
//            smsChatActivityIntent.setAction(SMSChatActivity.SMS_RECEIVED);
//            context.sendBroadcast(smsChatActivityIntent);
        }
    }
}

