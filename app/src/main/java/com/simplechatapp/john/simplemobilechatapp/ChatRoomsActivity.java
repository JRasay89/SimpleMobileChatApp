package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.ChatRoomListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
import com.simplechatapp.john.simplemobilechatapp.other.ChatRoom;
import com.simplechatapp.john.simplemobilechatapp.other.Method;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
       new RetrieveChatRooms().execute(currentUser);

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


    private class RetrieveChatRooms extends AsyncTask<String, Void, String> {

        private final String TAG = RetrieveChatRooms.class.getSimpleName();

        private ClientHttpRequest clientHttpRequest;

        public RetrieveChatRooms() {

            clientHttpRequest = new ClientHttpRequest();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            String username = args[0];

            //Create params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", "retrieve_chatRooms"));
            params.add(new BasicNameValuePair("username", username));

            Log.d(TAG, "Before Response");
            JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
            Log.d(TAG, "Response: " + jsonObject.toString());

            try {
                boolean error = jsonObject.getBoolean("error");

                if (!error) {
                    JSONArray chatRooms = jsonObject.getJSONArray("chatrooms");
                    Log.d(TAG, "JSONARRAY: " + chatRooms.toString());
                    updateChatRoomList(chatRooms);
                }
                else {
                    final String errorMsg = jsonObject.getString("error_msg");
                    ChatRoomsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatRoomsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        private void updateChatRoomList(final JSONArray chatRooms) {
            ChatRoomsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        for (int i = 0; i < chatRooms.length(); i++) {
                            ChatRoom chatRoom = new ChatRoom(chatRooms.getJSONObject(i).getInt("roomID"), chatRooms.getJSONObject(i).getString("roomName"));
                            chatRoomList.add(chatRoom);
                            chatRoomListAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
