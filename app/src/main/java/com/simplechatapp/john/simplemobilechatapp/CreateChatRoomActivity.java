package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.ChatRoomFriendListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.helper.MyProgressDialog;
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
        new RetrieveChatRoomFriends().execute(currentUser);
    }

    private void createChatRoom() {
        try {
            JSONArray jsonArray = new JSONArray(friendsToInviteList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("friends", jsonArray);
            Log.d(TAG, "JsonObject:" + jsonObject.toString());

            new CreateChatRoom().execute(currentUser, myRoomNameText.getText().toString(), jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Priveate Class RetrieveChatRoomFriends
     */
    private class RetrieveChatRoomFriends extends AsyncTask<String, Void, String> {
        private final String TAG = RetrieveChatRoomFriends.class.getSimpleName();

        private ClientHttpRequest clientHttpRequest;

        public RetrieveChatRoomFriends() {

            this.clientHttpRequest = new ClientHttpRequest();

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
            params.add(new BasicNameValuePair("tag", "retrieve_Friends"));
            params.add(new BasicNameValuePair("username", username));

            JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
            Log.d(TAG, "Response: " + jsonObject.toString());

            try {
                boolean error = jsonObject.getBoolean("error");

                if (!error) {
                    JSONArray friends = jsonObject.getJSONArray("friends");
                    updateFriendsList(friends);
                }
                else {
                    final String error_msg = jsonObject.getString("error_msg");
                    CreateChatRoomActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateChatRoomActivity.this, error_msg, Toast.LENGTH_LONG).show();
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

        private void updateFriendsList(final JSONArray friends) {
            CreateChatRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < friends.length(); i++) {
                            friendList.add(friends.getString(i));
                            chatRoomFriendListAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    /**
     * Private Class CreateChatRoom
     */
    private class CreateChatRoom extends AsyncTask<String, Void, String> {

        private final String TAG = CreateChatRoom.class.getSimpleName();

        private ClientHttpRequest clientHttpRequest;
        private MyProgressDialog progressDialog;
        private ChatRoom chatRoom;

        public CreateChatRoom() {
            this.chatRoom = new ChatRoom();
            this.clientHttpRequest = new ClientHttpRequest();
            progressDialog = new MyProgressDialog(CreateChatRoomActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Creating Room...");
            progressDialog.showDialog();
        }

        @Override
        protected String doInBackground(String... args) {
            String username = args[0];
            String chatRoomName = args[1];
            String friendsToInvite = args[2];

            Log.d(TAG, "Value of Friends: " + friendsToInvite);

            //Create params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", "create_chatRoom"));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("roomName", chatRoomName));
            params.add(new BasicNameValuePair("friends", friendsToInvite));

            JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
            Log.d(TAG, "Response: " + jsonObject);
            try {
                boolean error = jsonObject.getBoolean("error");

                if (!error) {

                    JSONObject chatRoom = jsonObject.getJSONObject("chatRoom");
                    Log.d(TAG, "chatRoom: " + chatRoom.toString());

                    int roomID = chatRoom.getInt("roomID");
                    String roomName = chatRoom.getString("roomName");

                    this.chatRoom.setRoomID(roomID);
                    this.chatRoom.setRoomName(roomName);
                    //Log.d(TAG, "chatRoomOBJID: " + this.chatRoom.getRoomID() + " chatRoomOBJNAME: " + this.chatRoom.getRoomName());

                    //Display the success message as a Toast
                    final String success_message = jsonObject.getString("success_msg");
                    CreateChatRoomActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateChatRoomActivity.this, success_message, Toast.LENGTH_LONG).show();
                        }
                    });

                    //Start new intent
                    //Start the Chat activity and pass the roomID and roomName
                    Intent intent = new Intent(CreateChatRoomActivity.this, ChatActivity.class);
                    Bundle extras = new Bundle();

                    extras.putInt("roomID", this.chatRoom.getRoomID());
                    extras.putString("roomName", this.chatRoom.getRoomName());
                    intent.putExtras(extras);

                    CreateChatRoomActivity.this.startActivity(intent);
                    CreateChatRoomActivity.this.finish();

                }
                else {
                    final String error_msg = jsonObject.getString("error_msg");
                    CreateChatRoomActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateChatRoomActivity.this, error_msg, Toast.LENGTH_LONG).show();
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
            progressDialog.hideDialog();
        }
    }

}
