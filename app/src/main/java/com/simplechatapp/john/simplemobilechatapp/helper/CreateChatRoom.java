package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.ChatActivity;
import com.simplechatapp.john.simplemobilechatapp.CreateChatRoomActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.other.ChatRoom;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 7/4/2015.
 */
public class CreateChatRoom extends AsyncTask<String, Void, String> {

    private static final String TAG = CreateChatRoom.class.getSimpleName();

    private CreateChatRoomActivity createChatRoomActivity;
    private ClientHttpRequest clientHttpRequest;
    private ProgressDialog pDialog;
    private ChatRoom chatRoom;

    public CreateChatRoom(Activity activity) {
        this.createChatRoomActivity = (CreateChatRoomActivity) activity;
        this.chatRoom = new ChatRoom();
        this.clientHttpRequest = new ClientHttpRequest();
        pDialog = new ProgressDialog(createChatRoomActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Creating Room...");

        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... args) {
        String username = args[0];
        String chatRoomName = args[1];
        String friendsToInvite = args[2];
        String numOfUsers = args[3];

        //Log.d(TAG, "Value of Friends: " + friendsToInvite);

        //Create params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "create_chatRoom"));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("chatRoomName", chatRoomName));
        params.add(new BasicNameValuePair("friends", friendsToInvite));
        params.add(new BasicNameValuePair("numOfUsers", numOfUsers));

        Log.d(TAG, "BEFORE RESPONSE");
        JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
        Log.d(TAG, "Response: " + jsonObject);
        try {
            final boolean error = jsonObject.getBoolean("error");

            if (!error) {

                JSONObject chatRoom = jsonObject.getJSONObject("chatRoom");
                //Log.d(TAG, "chatRoom: " + chatRoom.toString());

                int roomID = chatRoom.getInt("roomID");
                String roomName = chatRoom.getString("room_name");

                this.chatRoom.setRoomID(roomID);
                this.chatRoom.setRoomName(roomName);
                //Log.d(TAG, "chatRoomOBJID: " + this.chatRoom.getRoomID() + " chatRoomOBJNAME: " + this.chatRoom.getRoomName());

                //Display the success message as a Toast
                final String success_message = jsonObject.getString("success_msg");
                createChatRoomActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(createChatRoomActivity, success_message, Toast.LENGTH_LONG).show();
                    }
                });

                //Start new intent
                //Start the Chat activity and pass the roomID and roomName
                Intent intent = new Intent(createChatRoomActivity, ChatActivity.class);
                Bundle extras = new Bundle();

                extras.putInt("roomID", this.chatRoom.getRoomID());
                extras.putString("roomName", this.chatRoom.getRoomName());
                intent.putExtras(extras);

                createChatRoomActivity.startActivity(intent);
                createChatRoomActivity.finish();

            }
            else {
                final String error_msg = jsonObject.getString("error_msg");
                createChatRoomActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(createChatRoomActivity, error_msg, Toast.LENGTH_LONG).show();
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

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}
