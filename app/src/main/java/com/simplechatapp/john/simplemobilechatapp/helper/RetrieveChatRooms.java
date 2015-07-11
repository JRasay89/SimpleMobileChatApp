package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.simplechatapp.john.simplemobilechatapp.ChatRoomsActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.ChatRoomListAdapter;
import com.simplechatapp.john.simplemobilechatapp.other.ChatRoom;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 7/3/2015.
 */
public class RetrieveChatRooms extends AsyncTask<String, Void, String> {

    private static final String TAG = RetrieveChatRooms.class.getSimpleName();

    private ChatRoomsActivity chatRoomsActivity;
    private ArrayList<ChatRoom> chatRoomList;
    private ChatRoomListAdapter chatRoomListAdapter;

    private ClientHttpRequest clientHttpRequest;

    public RetrieveChatRooms(Activity activity, ArrayList<ChatRoom> chatRoomList, ChatRoomListAdapter chatRoomListAdapter) {
        this.chatRoomsActivity = (ChatRoomsActivity) activity;
        this.chatRoomList = chatRoomList;
        this.chatRoomListAdapter = chatRoomListAdapter;

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
        chatRoomsActivity.runOnUiThread(new Runnable() {
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
        Log.d(TAG, "chatRoomList: " + chatRoomList.toString());
    }
}
