package com.head_first.aashi.sms.data_handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.head_first.aashi.sms.interfaces.DatabaseCommunicator;
import com.head_first.aashi.sms.model.Message;
import com.head_first.aashi.sms.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aashish Indorewala on 04-May-17.
 */

public class MessageHistoryDatabase extends SQLiteOpenHelper implements DatabaseCommunicator{

    private static final String DATABASE_NAME = "MessageHistoryDatabase";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "MessageHistory";
    private  static final String PRIMARY_KEY_COLUMN_NAME = "Id";
    private  static final String SENT_BY_COLUMN_NAME = "SenderPhoneNumber";
    private  static final String SENT_TO_COLUMN_NAME = "ReceiverPhoneNumber";
    private  static final String MESSAGE_COLUMN_NAME = "Message";

    private static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String TABLE_CREATE_QUERY =
            "CREATE TABLE " + TABLE_NAME +
            "("+ PRIMARY_KEY_COLUMN_NAME +" int NOT NULL, "+ SENT_BY_COLUMN_NAME + " varchar(12) NOT NULL, "
            + SENT_TO_COLUMN_NAME + " varchar(12) NOT NULL, "
            + MESSAGE_COLUMN_NAME +" varchar(1000) NOT NULL,"
            +" PRIMARY KEY (Id));";
    private static final String GET_ROW_COUNT = "SELECT COUNT(*) FROM " + TABLE_NAME + "";
    private static final String GET_ORDERED_DISTINCT_SENT_BY_PHONE_NUMBERS = "SELECT MAX("+ PRIMARY_KEY_COLUMN_NAME +"), " + SENT_BY_COLUMN_NAME
            + " FROM " + TABLE_NAME
            + " GROUP BY " + SENT_BY_COLUMN_NAME + ";";
    private static final String GET_ORDERED_DISTINCT_SENT_TO_PHONE_NUMBERS = "SELECT MAX("+ PRIMARY_KEY_COLUMN_NAME +"), " + SENT_TO_COLUMN_NAME
            + " FROM " + TABLE_NAME
            + " GROUP BY " + SENT_TO_COLUMN_NAME + ";";
    private static final String GET_MESSAGES_BETWEEN_PHONE_NUMBERS =
            "SELECT " + SENT_BY_COLUMN_NAME + "," + SENT_TO_COLUMN_NAME + "," + MESSAGE_COLUMN_NAME
            + " FROM " + TABLE_NAME
            + " WHERE (" + SENT_BY_COLUMN_NAME + " = ? AND " + SENT_TO_COLUMN_NAME + " = ?)"
                + " OR (" + SENT_TO_COLUMN_NAME + " = ? AND " + SENT_BY_COLUMN_NAME + " = ?)"
            + "ORDER BY " + PRIMARY_KEY_COLUMN_NAME;

    private static DatabaseCommunicator databaseCommunicator;

    private MessageHistoryDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseCommunicator getInstance(@NonNull Context context){
        if(databaseCommunicator == null){
            databaseCommunicator = new MessageHistoryDatabase(context);
        }
        return databaseCommunicator;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_QUERY);
        onCreate(db);
    }

    @Override
    public boolean addMessageToDatabase(Message message){
        if(message.getSentBy() == null || !StringUtil.isNumeric(message.getSentBy())
            || message.getSentTo() == null || !StringUtil.isNumeric(message.getSentTo())
            || message.getMessageText() == null){
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRIMARY_KEY_COLUMN_NAME, getRowCount());
        contentValues.put(SENT_BY_COLUMN_NAME, message.getSentBy());
        contentValues.put(SENT_TO_COLUMN_NAME, message.getSentTo());
        contentValues.put(MESSAGE_COLUMN_NAME, message.getMessageText());
        getWritableDatabase().insert(TABLE_NAME, null, contentValues);
        return true;
    }

    @Override
    public List<String> getAllOrderedDistinctContacts(@NonNull String currentDevicePhoneNumber){
        List<String> listOfOrderedDistinctContacts = new ArrayList<>();
        if(!StringUtil.isNumeric(currentDevicePhoneNumber)){
            throw new IllegalArgumentException("The current device phone ()"+ currentDevicePhoneNumber +" number should be numeric");
        }
        Map<Integer, String> idToPhoneNumberMap = new HashMap<>();
        Set<String> contactList = new HashSet<>();
        Cursor cursor = getReadableDatabase().rawQuery(GET_ORDERED_DISTINCT_SENT_BY_PHONE_NUMBERS, null);
        if(cursor != null){
            while(cursor.moveToNext()){
                String phoneNumber = cursor.getString(1);
                if(!currentDevicePhoneNumber.equalsIgnoreCase(phoneNumber)){
                    contactList.add(phoneNumber);
                    idToPhoneNumberMap.put(cursor.getInt(0), phoneNumber);
                }
            }
        }
        cursor = getReadableDatabase().rawQuery(GET_ORDERED_DISTINCT_SENT_TO_PHONE_NUMBERS, null);
        if(cursor != null){
            while(cursor.moveToNext()){
                String phoneNumber = cursor.getString(1);
                if(!currentDevicePhoneNumber.equalsIgnoreCase(phoneNumber) && !contactList.contains(phoneNumber)){
                    contactList.add(phoneNumber);
                    idToPhoneNumberMap.put(cursor.getInt(0), phoneNumber);
                }
            }
        }
        List<Integer> sortedIds = new ArrayList<>(idToPhoneNumberMap.keySet());
        Collections.sort(sortedIds);
        for(Integer id : sortedIds){
            listOfOrderedDistinctContacts.add(idToPhoneNumberMap.get(id));
        }
        return listOfOrderedDistinctContacts;
    }

    @Override
    public List<Message> getListOfMessagesExchangedBetweenPhoneNumbers(@NonNull String phoneNumber1, @NonNull String phoneNumber2){
        List<Message> messageList = new ArrayList<>();
        if(!StringUtil.isNumeric(phoneNumber1) || !StringUtil.isNumeric(phoneNumber2)){
            throw new IllegalArgumentException("The provided phone numbers should be numeric");
        }
        Cursor cursor = getReadableDatabase().rawQuery(GET_MESSAGES_BETWEEN_PHONE_NUMBERS, new String[] {phoneNumber1, phoneNumber2, phoneNumber1, phoneNumber2});
        if(cursor != null){
            while(cursor.moveToNext()){
                messageList.add(new Message(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            }
        }
        return messageList;
    }

    private int getRowCount(){
        int rowCount = -1;
        Cursor cursor = getReadableDatabase().query(true, TABLE_NAME, new String[]{"COUNT("+ PRIMARY_KEY_COLUMN_NAME +")"}, null, null, null, null, null, null);
        if(cursor != null){
            while(cursor.moveToNext()){
                rowCount = cursor.getInt(0);
            }
        }
        else{
            throw new SQLiteAbortException();
        }
        return rowCount;
    }

    private String getSQLStringRepresentationOfArray(@NonNull Object[] array){
        String string = "";
        for(int i = 0; i < array.length; i++){
            if(i != 0){
                string += ",";
            }
            string += "\'" + array[i].toString() + "\'";
        }
        return string;
    }

    //The following methods were created for testing purpose
    private void addMockData(){
        addMessageToDatabase(new Message("021","15555215554","Hi I am a stranger"));
        addMessageToDatabase(new Message("15555215554","021","I will block you now"));
        addMessageToDatabase(new Message("021","15555215554","My name is barry allen and i am the fastest man alive"));
        addMessageToDatabase(new Message("15555215554","021","Ohk! then we can talk"));
        addMessageToDatabase(new Message("021","15555215554","So what do you think about my show?"));
        addMessageToDatabase(new Message("15555215554","021","Get Faster you idiot!"));
        addMessageToDatabase(new Message("15555215554","022","Get Faster you idiot!"));
        addMessageToDatabase(new Message("15555215554","022","Get Faster you idiot!"));
        addMessageToDatabase(new Message("022","15555215554","Get Faster you idiot!"));
        addMessageToDatabase(new Message("022","15555215554","Get Faster you idiot!"));
        addMessageToDatabase(new Message("055","15555215554","Get Faster you idiot!"));
    }

    private void getAllDatabaseInfo(){
        List<Message> messageList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while(cursor.moveToNext()){
            messageList.add(new Message(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
    }
}
