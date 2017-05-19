package com.head_first.aashi.sms.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.head_first.aashi.sms.R;
import com.head_first.aashi.sms.model.Message;

import java.util.List;

/**
 * Created by Aashish Indorewala on 05-May-17.
 */

public class SMSChatListAdapter extends BaseAdapter {
    private static final int LIST_ITEM_LAYOUT_LEFT = R.layout.smschat_list_text_message_left;
    private static final int LIST_ITEM_LAYOUT_RIGHT = R.layout.smschat_list_text_message_right;

    //Views and context
    private Context context;

    //Data
    private List<Message> messages;

    public SMSChatListAdapter(@NonNull Context context,@NonNull List<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String devicePhoneNumber = (context.getResources().getString(R.string.currentDevicePhoneNumber));
        //if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //if(messages.get(position).getSentBy().equalsIgnoreCase(StringUtil.convertToNzNumber(devicePhoneNumber))){
            if(messages.get(position).isSentByCurrentDevice()){
                convertView = layoutInflater.inflate(LIST_ITEM_LAYOUT_RIGHT, null);
            }
            else{
                convertView = layoutInflater.inflate(LIST_ITEM_LAYOUT_LEFT, null);
            }
        //}
        TextView textView = (TextView) convertView.findViewById(R.id.textMessage);
        textView.setText((messages.get(position)).getMessageText());

        return convertView;
    }
}
