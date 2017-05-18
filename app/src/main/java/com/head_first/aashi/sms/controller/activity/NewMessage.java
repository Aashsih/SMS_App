package com.head_first.aashi.sms.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.model.Message;
import com.head_first.aashi.sms.utils.DialogBoxDisplayHandler;
import com.head_first.aashi.sms.utils.StringUtil;

public class NewMessage extends SMSSenderActivity {

    private EditText mMessageText;
    private EditText mSendTo;
    private FloatingActionButton mSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.newMessageActionBarTitle));
        setContentView(R.layout.activity_new_message);
        mMessageText = (EditText) findViewById(R.id.messageText);
        mSendTo = (EditText) findViewById(R.id.sendTo);
        mSendMessage = (FloatingActionButton) findViewById(R.id.sendMessage);
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtil.isNumeric(mSendTo.getText().toString())){
                    sendSMS(new Message(PreferenceManager.getDefaultSharedPreferences(NewMessage.this).getString(getResources().getString(R.string.currentDevicePhoneNumber), null),
                            mSendTo.getText().toString(), mMessageText.getText().toString()));
                    DialogBoxDisplayHandler.showIndefiniteProgressDialog(NewMessage.this);
                }
                else{
                    mSendTo.setError(getResources().getString(R.string.enterNumberErrorMessage));
                }
            }
        });

    }

    public void onMessageSent(){
        finish();
    }
}
