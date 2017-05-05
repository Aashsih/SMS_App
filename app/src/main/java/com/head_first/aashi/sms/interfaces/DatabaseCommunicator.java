package com.head_first.aashi.sms.interfaces;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.head_first.aashi.sms.model.Message;

import java.util.List;

/**
 * Created by Aashish Indorewala on 05-May-17.
 */

public interface DatabaseCommunicator {
    public boolean addMessageToDatabase(Message message);
    public List<String> getAllDistinctContacts(@NonNull String currentDevicePhoneNumber);
    public List<Message> getListOfMessagesExchangedBetweenPhoneNumbers(@NonNull String phoneNumber1, @NonNull String phoneNumber2);
}
