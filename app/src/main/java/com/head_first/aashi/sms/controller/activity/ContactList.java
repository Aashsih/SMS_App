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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.data_handler.MessageHistoryDatabase;
import com.head_first.aashi.sms.interfaces.DatabaseCommunicator;
import com.head_first.aashi.sms.utils.StringUtil;

public class ContactList extends AppCompatActivity{// implements DialogInterface.OnClickListener{

    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 0;

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
                Intent newMessageIntent = new Intent(ContactList.this, NewMessage.class);
                startActivity(newMessageIntent);
            }
        });
        mContactList  = (ListView) findViewById(R.id.contactList);
        requestUserPermissionToReadPhoneState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_list, menu);
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
            case READ_PHONE_STATE_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setupContactList();
                }
                else{
                    Toast.makeText(this, getResources().getString(R.string.acceptReadPhoneStatePermission), Toast.LENGTH_SHORT);
                }
                break;
            }
        }
    }

    public void setupContactList(){
        String currentDevicePhoneNumber = ((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        if(currentDevicePhoneNumber == null || currentDevicePhoneNumber.isEmpty()){
            showAlertDialogToEnterPhoneNumber();
        }
        else{
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(getResources().getString(R.string.currentDevicePhoneNumber),currentDevicePhoneNumber);
            editor.commit();
            contactListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, databaseCommunicator.getAllDistinctContacts(currentDevicePhoneNumber));
            mContactList.setAdapter(contactListAdapter);
        }
    }

    private void requestUserPermissionToReadPhoneState(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            setupContactList();
        }
    }

    private void showAlertDialogToEnterPhoneNumber(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View confirmPhoneNumerDialogView = inflater.inflate(R.layout.dialog_confirm_phone_number, null);
        dialogBuilder.setView(confirmPhoneNumerDialogView);
        final EditText mPhoneNumberEditText = (EditText) confirmPhoneNumerDialogView.findViewById(R.id.phoneNumber);

        dialogBuilder.setTitle("Welcome to SMS");
        dialogBuilder.setMessage(R.string.requestUserPhoneNumber);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.confirmNumber, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog == confirmPhoneNumberAlertDialog){
                    String currentDevicePhoneNumber = mPhoneNumberEditText.getText().toString();
                    if(currentDevicePhoneNumber != null && StringUtil.isNumeric(currentDevicePhoneNumber)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ContactList.this).edit();
                        editor.putString(getResources().getString(R.string.currentDevicePhoneNumber),currentDevicePhoneNumber);
                        editor.commit();
                        contactListAdapter = new ArrayAdapter<String>(ContactList.this, android.R.layout.simple_list_item_1, databaseCommunicator.getAllDistinctContacts(currentDevicePhoneNumber));
                        mContactList.setAdapter(contactListAdapter);
                        confirmPhoneNumberAlertDialog.dismiss();
                    }
                    else{
                        showAlertDialogToEnterPhoneNumber();
                    }
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.quitApp, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        confirmPhoneNumberAlertDialog = dialogBuilder.create();
        confirmPhoneNumberAlertDialog.show();
    }

//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        if(dialog == confirmPhoneNumberAlertDialog){
//            String currentDevicePhoneNumber = mPhoneNumberEditText.getText().toString();
//            if(currentDevicePhoneNumber != null && StringUtil.isNumeric(currentDevicePhoneNumber)){
//                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ContactList.this).edit();
//                editor.putString(getResources().getString(R.string.currentDevicePhoneNumber),currentDevicePhoneNumber);
//                editor.commit();
//                contactListAdapter = new ArrayAdapter<String>(ContactList.this, R.layout.activity_contact_list, databaseCommunicator.getAllDistinctContacts(currentDevicePhoneNumber));
//                mContactList.setAdapter(contactListAdapter);
//                confirmPhoneNumberAlertDialog.dismiss();
//            }
//            else{
//                showAlertDialogToEnterPhoneNumber();
//            }
//        }
//    }
}
