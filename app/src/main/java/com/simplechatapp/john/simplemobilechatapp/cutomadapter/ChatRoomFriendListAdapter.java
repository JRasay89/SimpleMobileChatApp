package com.simplechatapp.john.simplemobilechatapp.cutomadapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.simplechatapp.john.simplemobilechatapp.CreateChatRoomActivity;
import com.simplechatapp.john.simplemobilechatapp.R;

import java.util.ArrayList;

/**
 * Created by John on 7/3/2015.
 */
public class ChatRoomFriendListAdapter extends BaseAdapter {
    private static final String TAG = ChatRoomFriendListAdapter.class.getSimpleName();

    private CreateChatRoomActivity createChatRoomActivity;
    private ArrayList<String> friendList;
    private LayoutInflater layoutInflater;

    public ChatRoomFriendListAdapter(Activity activity, ArrayList<String> friendList) {
        this.createChatRoomActivity = (CreateChatRoomActivity) activity;
        this.friendList = friendList;
        this.layoutInflater = LayoutInflater.from(createChatRoomActivity);
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String friendName = friendList.get(position);

        convertView = layoutInflater.inflate(R.layout.chat_room_friend_list, null);

        TextView myFriendNameText = (TextView) convertView.findViewById(R.id.createChatRoomList_myFriendNameText);
        myFriendNameText.setText(friendName);

        final Button myInviteFriendButton = (Button) convertView.findViewById(R.id.createChatRoomList_myInviteFriendButton);
        myInviteFriendButton.setTag(R.id.TAG_IS_INVITED, false);
        myInviteFriendButton.setTag(R.id.TAG_FRIEND_NAME, friendName);
        myInviteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((boolean) myInviteFriendButton.getTag(R.id.TAG_IS_INVITED))) {
                    createChatRoomActivity.getfriendsToInviteList().add((String) myInviteFriendButton.getTag(R.id.TAG_FRIEND_NAME));
                    myInviteFriendButton.setTag(R.id.TAG_IS_INVITED, true);
                    myInviteFriendButton.setText("Uninvite");
                    Log.d(TAG, "Friend Invited");
                } else {
                    createChatRoomActivity.getfriendsToInviteList().remove((String) myInviteFriendButton.getTag(R.id.TAG_FRIEND_NAME));
                    myInviteFriendButton.setText("Invite");
                    myInviteFriendButton.setTag(R.id.TAG_IS_INVITED, false);
                    Log.d(TAG, "Friend Uninvited");
                }

            }
        });

        return convertView;
    }
}
