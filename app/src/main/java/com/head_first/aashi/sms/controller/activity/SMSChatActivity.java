package com.head_first.aashi.sms.controller.activity;

import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.interfaces.DatabaseCommunicator;
import com.head_first.aashi.sms.model.Message;
import com.head_first.aashi.sms.utils.SMSChatListAdapter;
import com.head_first.aashi.sms.utils.StringUtil;

import java.util.List;

public class SMSChatActivity extends AppCompatActivity {

    //View
    private ListView mSMSChat;
    private EditText mMessageText;
    private FloatingActionButton mSendMessage;

    //Adapter
    private SMSChatListAdapter smsChatListAdapter;

    //Data
    private DatabaseCommunicator databaseCommunicator;
    private String otherContactPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smschat);
        databaseCommunicator = MessageHistoryDatabase.getInstance(this);
        otherContactPhoneNumber = getIntent().getStringExtra(getResources().getString(R.string.otherContactPhoneNumber));
        if(otherContactPhoneNumber == null || !StringUtil.isNumeric(otherContactPhoneNumber)){
            finish();
        }
        mSMSChat = (ListView) findViewById(R.id.smsChat);
        mMessageText = (EditText) findViewById(R.id.messageText);
        mSendMessage = (FloatingActionButton) findViewById(R.id.sendMessage);
        String currentDevicePhoneNumber = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.currentDevicePhoneNumber), null);
        smsChatListAdapter = new SMSChatListAdapter(this,
                databaseCommunicator.getListOfMessagesExchangedBetweenPhoneNumbers(currentDevicePhoneNumber, otherContactPhoneNumber));
        mSMSChat.setAdapter(smsChatListAdapter);
    }
}
