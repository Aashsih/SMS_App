package com.head_first.aashi.sms.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.interfaces.DatabaseCommunicator;
import com.head_first.aashi.sms.model.Message;
import com.head_first.aashi.sms.utils.SMSChatListAdapter;
import com.head_first.aashi.sms.utils.StringUtil;

public class SMSChatActivity extends SMSSenderActivity {
    public static final String SMS_RECEIVED = "custom.action.SMSRECEIVEDINFO";
    private static final int PLACE_PICKER_REQUEST = 2;

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
        databaseCommunicator = MessageHistoryDatabase.getInstance(this);
        otherContactPhoneNumber = getIntent().getStringExtra(getResources().getString(R.string.otherContactPhoneNumber));
        if(otherContactPhoneNumber == null || !StringUtil.isNumeric(otherContactPhoneNumber)){
            finish();
        }
        setTitle(otherContactPhoneNumber);
        setContentView(R.layout.activity_smschat);
        mSMSChat = (ListView) findViewById(R.id.smsChat);
        mMessageText = (EditText) findViewById(R.id.messageText);
        mSendMessage = (FloatingActionButton) findViewById(R.id.sendMessage);
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendSMS();
            }
        });
        String currentDevicePhoneNumber = getResources().getString(R.string.currentDevicePhoneNumber);
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
            startPlacePickerIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(this, data);
                //mMessageText.setText(String.format("Place: %s", place.getName()));
                if(place.getAddress() == null || place.getAddress().toString().isEmpty()){
                    mMessageText.setText(place.getName());
                }
                else{
                    mMessageText.setText(place.getAddress());
                }
                //onSendSMS();
            }
        }
    }

    public void refreshScreen(){
        smsChatListAdapter = new SMSChatListAdapter(this,
                databaseCommunicator.getListOfMessagesExchangedBetweenPhoneNumbers(getResources().getString(R.string.currentDevicePhoneNumber), otherContactPhoneNumber));
        mSMSChat.setAdapter(smsChatListAdapter);
    }

    private void startPlacePickerIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onSendSMS(){
        if(StringUtil.isNumeric(otherContactPhoneNumber)){
            sendSMS(new Message(getResources().getString(R.string.currentDevicePhoneNumber),
                    otherContactPhoneNumber, mMessageText.getText().toString()));
            mMessageText.setText("");
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mMessageText.getWindowToken(), 0);
        }
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
