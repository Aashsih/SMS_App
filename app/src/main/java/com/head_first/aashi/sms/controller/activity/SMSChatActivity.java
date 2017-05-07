package com.head_first.aashi.sms.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.interfaces.DatabaseCommunicator;
import com.head_first.aashi.sms.model.Message;
import com.head_first.aashi.sms.utils.SMSChatListAdapter;
import com.head_first.aashi.sms.utils.StringUtil;

public class SMSChatActivity extends SMSSenderActivity {
    public static final String SMS_RECEIVED = "custom.action.SMSRECEIVEDINFO";

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
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtil.isNumeric(otherContactPhoneNumber)){
                    sendSMS(new Message(PreferenceManager.getDefaultSharedPreferences(SMSChatActivity.this).getString(getResources().getString(R.string.currentDevicePhoneNumber), null),
                            otherContactPhoneNumber, mMessageText.getText().toString()));
                    mMessageText.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mMessageText.getWindowToken(), 0);
                }
            }
        });
        String currentDevicePhoneNumber = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.currentDevicePhoneNumber), null);
        smsChatListAdapter = new SMSChatListAdapter(this,
                databaseCommunicator.getListOfMessagesExchangedBetweenPhoneNumbers(currentDevicePhoneNumber, otherContactPhoneNumber));
        mSMSChat.setAdapter(smsChatListAdapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sms_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.locationItem) {
            //Start the Location Activity here
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshScreen(){
        smsChatListAdapter = new SMSChatListAdapter(this,
                databaseCommunicator.getListOfMessagesExchangedBetweenPhoneNumbers(PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getResources().getString(R.string.currentDevicePhoneNumber), null), otherContactPhoneNumber));
        mSMSChat.setAdapter(smsChatListAdapter);
    }

    private class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SMS_RECEIVED)){
                refreshScreen();
            }
        }
    }
}
