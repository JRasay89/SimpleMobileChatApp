package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.cutomadapter.ChatRoomFriendListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.CreateChatRoom;
import com.simplechatapp.john.simplemobilechatapp.helper.RetrieveChatRoomFriends;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by John on 7/3/2015.
 */
public class CreateChatRoomActivity extends Activity {
    private static final String TAG = CreateChatRoomActivity.class.getSimpleName();

    private SQLiteHandler databaseHandler;
    private HashMap<String, String> myUser;
    private String currentUser;

    private EditText myRoomNameText;

    private ArrayList<String> friendList;
    private ChatRoomFriendListAdapter chatRoomFriendListAdapter;
    private ListView myFriendListView;

    private Button myCancelButton;
    private Button myFinishButton;

    private ArrayList<String> friendsToInviteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);

        friendsToInviteList = new ArrayList<String>();

        //Initialize database handler
        databaseHandler = new SQLiteHandler(this);
        //Get the user
        myUser = databaseHandler.getUser();
        //Get the username of the current user
        currentUser = myUser.get("name").toString();


        myRoomNameText = (EditText) findViewById(R.id.createChatRoom_myRoomNameText);

        friendList = new ArrayList<>();
        chatRoomFriendListAdapter = new ChatRoomFriendListAdapter(this, friendList);
        myFriendListView = (ListView) findViewById(R.id.createChatRoom_myFriendsListView);
        myFriendListView.setAdapter(chatRoomFriendListAdapter);

        myCancelButton = (Button) findViewById(R.id.createChatRoom_myCancelButton);
        myCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateChatRoomActivity.this, ChatRoomsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        myFinishButton = (Button) findViewById(R.id.createChatRoom_myFinishButton);
        myFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = myRoomNameText.getText().toString();
                if (roomName.trim().length() <= 0) {
                    Toast.makeText(CreateChatRoomActivity.this, "Please enter a name for the room", Toast.LENGTH_LONG).show();
                }
                else if (friendsToInviteList.size() <= 0) {
                    Toast.makeText(CreateChatRoomActivity.this, "Need to invite at least 1 friend to the chat room!", Toast.LENGTH_LONG).show();
                }
                else {
                    createChatRoom();
                }

            }
        });

        retrieveFriends();

    }

    public ArrayList<String> getfriendsToInviteList() {
        return this.friendsToInviteList;
    }

    private void retrieveFriends() {
        new RetrieveChatRoomFriends(this, friendList, chatRoomFriendListAdapter).execute(currentUser);
    }

    private void createChatRoom() {
        try {
            JSONArray jsonArray = new JSONArray(friendsToInviteList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("friends", jsonArray);
            Log.d(TAG, "JsonObject:" + jsonObject.toString());

            new CreateChatRoom(this).execute(currentUser, myRoomNameText.getText().toString(), jsonObject.toString(), String.valueOf(friendsToInviteList.size() + 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
