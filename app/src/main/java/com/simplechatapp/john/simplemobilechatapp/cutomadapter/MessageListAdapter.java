package com.simplechatapp.john.simplemobilechatapp.cutomadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simplechatapp.john.simplemobilechatapp.R;
import com.simplechatapp.john.simplemobilechatapp.other.Message;

import java.util.ArrayList;

/**
 * Created by John on 4/23/2015.
 */
public class MessageListAdapter extends BaseAdapter {
    //Log tag
    private static final String TAG = MessageListAdapter.class.getSimpleName();

    private LayoutInflater layoutInflater;
    private ArrayList<Message> messages;


    public MessageListAdapter(Context context, ArrayList<Message> messages) {
        this.layoutInflater = LayoutInflater.from(context);
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get the messages
        Message message = messages.get(position);

        //If the message is sent by the user of the current session, display the message on the left
        //else display the message on the right
        if (message.isSelf()) {
            convertView = layoutInflater.inflate(R.layout.message_list_left, null);
        }
        else {
            convertView = layoutInflater.inflate(R.layout.message_list_right, null);
        }

        //Get the TextView widgets
        TextView senderText = (TextView) convertView.findViewById(R.id.sender_text);
        TextView messageText = (TextView) convertView.findViewById(R.id.message_text);

        //Set the name and message
        senderText.setText(message.getSender() + ":");
        messageText.setText(message.getMessage());

        return convertView;
    }
}
