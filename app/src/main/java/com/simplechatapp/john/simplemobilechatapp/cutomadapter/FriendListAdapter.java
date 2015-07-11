package com.simplechatapp.john.simplemobilechatapp.cutomadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.simplechatapp.john.simplemobilechatapp.CreateChatRoomActivity;
import com.simplechatapp.john.simplemobilechatapp.FriendsActivity;
import com.simplechatapp.john.simplemobilechatapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by John on 6/20/2015.
 */
public class FriendListAdapter extends BaseAdapter {

    private FriendsActivity friendsActivity;

    private ArrayList<String> friendList;
    private LayoutInflater layoutInflater;


    public FriendListAdapter(Context context, ArrayList<String> friendList) {
        this.friendsActivity = (FriendsActivity) context;
        this.friendList = friendList;
        this.layoutInflater = LayoutInflater.from(friendsActivity);

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
        String friend = friendList.get(position);

        convertView = layoutInflater.inflate(R.layout.friend_list, null);

        TextView friend_myFriendName = (TextView) convertView.findViewById(R.id.friend_myFriendName);

        friend_myFriendName.setText(friend);


        return convertView;
    }
}
