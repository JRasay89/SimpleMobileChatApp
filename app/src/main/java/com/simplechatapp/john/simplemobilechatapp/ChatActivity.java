package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;


/**
 * Created by John on 7/2/2015.
 */
public class ChatActivity extends Activity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private TextView myChatRoomName;

    private int roomID;
    private String chatRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        roomID = extras.getInt("roomID");
        chatRoomName = extras.getString("roomName");
        Log.d(TAG, "roomID: " + roomID + " " + "roomName: " + chatRoomName);
        myChatRoomName = (TextView) findViewById(R.id.chat_myChatRoomName);
        myChatRoomName.setText("Welcome to " + chatRoomName + " room!");
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
}
