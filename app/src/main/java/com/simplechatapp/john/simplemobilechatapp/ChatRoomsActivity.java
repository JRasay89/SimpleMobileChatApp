package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.simplechatapp.john.simplemobilechatapp.cutomadapter.ChatRoomListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.RetrieveChatRooms;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
import com.simplechatapp.john.simplemobilechatapp.other.ChatRoom;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by John on 7/2/2015.
 */
public class ChatRoomsActivity extends Activity {

    private SQLiteHandler databaseHandler;
    private HashMap<String, String> myUser;
    private String currentUser;

    private ArrayList<ChatRoom> chatRoomList;
    private ChatRoomListAdapter chatRoomListAdapter;
    private ListView myChatRoomListView;

    private Button myCreateChatRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize database handler
        databaseHandler = new SQLiteHandler(this);
        //Get the user
        myUser = databaseHandler.getUser();
        //Get the username of the current user
        currentUser = myUser.get("name").toString();

        chatRoomList = new ArrayList<>();
        chatRoomListAdapter = new ChatRoomListAdapter(this, chatRoomList);
        myChatRoomListView = (ListView) findViewById(R.id.chatRoom_myChatRoomListView);
        myChatRoomListView.setAdapter(chatRoomListAdapter);

        myCreateChatRoomButton = (Button) findViewById(R.id.chatRoom_myCreateChatRoomButton);
        myCreateChatRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoomsActivity.this, CreateChatRoomActivity.class);
                startActivity(intent);
                finish();

            }
        });

        retrieveChatRooms();
        initListViewItemListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void retrieveChatRooms() {
       new RetrieveChatRooms(this, chatRoomList, chatRoomListAdapter).execute(currentUser);

    }

    private void initListViewItemListener() {

        myChatRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ChatRoomsActivity.this, ChatActivity.class);
                Bundle extras = new Bundle();

                extras.putInt("roomID", chatRoomList.get(position).getRoomID());
                extras.putString("roomName", chatRoomList.get(position).getRoomName());
                intent.putExtras(extras);

                startActivity(intent);
            }
        });
    }
}
