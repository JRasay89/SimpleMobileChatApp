package com.simplechatapp.john.simplemobilechatapp.cutomadapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simplechatapp.john.simplemobilechatapp.ChatRoomsActivity;
import com.simplechatapp.john.simplemobilechatapp.R;
import com.simplechatapp.john.simplemobilechatapp.other.ChatRoom;

import java.util.ArrayList;

/**
 * Created by John on 7/3/2015.
 */
public class ChatRoomListAdapter extends BaseAdapter {

    private ChatRoomsActivity chatRoomActivity;
    private ArrayList<ChatRoom> chatRoomList;
    private LayoutInflater layoutInflater;

    public ChatRoomListAdapter(Activity activity, ArrayList<ChatRoom> chatRoomList) {
        this.chatRoomActivity = (ChatRoomsActivity) activity;
        this.chatRoomList = chatRoomList;
        this.layoutInflater = LayoutInflater.from(chatRoomActivity);

    }

    @Override
    public int getCount() {
        return chatRoomList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatRoomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String chatRoom = chatRoomList.get(position).getRoomName();

        convertView = layoutInflater.inflate(R.layout.chat_room_list, null);

        TextView myChatRoomName = (TextView) convertView.findViewById(R.id.chatRoomList_myChatRoomName);

        myChatRoomName.setText(chatRoom);

        return convertView;
    }
}
