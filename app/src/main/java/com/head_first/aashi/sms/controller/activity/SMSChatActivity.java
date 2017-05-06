package com.head_first.aashi.sms.controller.activity;

import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.interfaces.DatabaseCommunicator;
import com.head_first.aashi.sms.utils.SMSChatListAdapter;
import com.head_first.aashi.sms.utils.StringUtil;

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
}
