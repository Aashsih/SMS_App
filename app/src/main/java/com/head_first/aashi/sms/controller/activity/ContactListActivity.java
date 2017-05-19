package com.head_first.aashi.sms.controller.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.interfaces.DatabaseCommunicator;
import com.head_first.aashi.sms.utils.StringUtil;

public class ContactListActivity extends AppCompatActivity{// implements DialogInterface.OnClickListener{

    private static final int PERMISSION_GRANTED_REQUEST_CODE = 0;

    //Views
    private ListView mContactList;
    private FloatingActionButton mCreateNewMessageButton;
    private AlertDialog confirmPhoneNumberAlertDialog;
    //private EditText mPhoneNumberEditText;

    //Adapters
    ArrayAdapter<String> contactListAdapter;

    //Data
    private DatabaseCommunicator databaseCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        databaseCommunicator = MessageHistoryDatabase.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.messageListActionBarTitle);
        mCreateNewMessageButton = (FloatingActionButton) findViewById(R.id.createNewMessage);
        mCreateNewMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newMessageIntent = new Intent(ContactListActivity.this, NewMessageActivity.class);
                startActivity(newMessageIntent);
            }
        });
        mContactList  = (ListView) findViewById(R.id.contactList);
        mContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent smsChatIntent = new Intent(ContactListActivity.this, SMSChatActivity.class);
                smsChatIntent.putExtra(getResources().getString(R.string.otherContactPhoneNumber),contactListAdapter.getItem(position));
                startActivity(smsChatIntent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        requestUserPermissionToReadPhoneState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSION_GRANTED_REQUEST_CODE:{
//            case RECEIVE_SMS_REQUEST_CODE:
//            case SEND_SMS_REQUEST_CODE:
//            case READ_SMS_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(contactListAdapter == null){
                        setupContactList();
                    }
                }
                else{
                    //Toast.makeText(this, getResources().getString(R.string.acceptReadPhoneStatePermission), Toast.LENGTH_SHORT);
                    finish();
                }
                break;
            }

        }
    }

    public void setupContactList(){
        String currentDevicePhoneNumber = getResources().getString(R.string.currentDevicePhoneNumber);
        contactListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, databaseCommunicator.getAllOrderedDistinctContacts(currentDevicePhoneNumber));
        mContactList.setAdapter(contactListAdapter);

    }

    private void requestUserPermissionToReadPhoneState(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.READ_SMS
                        , Manifest.permission.SEND_SMS
                        , Manifest.permission.RECEIVE_SMS},
                        PERMISSION_GRANTED_REQUEST_CODE);
            }
        }
        else{
            setupContactList();
        }
    }

}
