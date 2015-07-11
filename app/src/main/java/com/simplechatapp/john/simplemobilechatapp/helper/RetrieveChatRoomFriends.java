package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.CreateChatRoomActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.ChatRoomFriendListAdapter;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 7/4/2015.
 */
public class RetrieveChatRoomFriends extends AsyncTask<String, Void, String> {

    private CreateChatRoomActivity createChatRoomActivity;
    private ArrayList<String> friendList;
    private ChatRoomFriendListAdapter chatRoomFriendListAdapter;

    private ClientHttpRequest clientHttpRequest;

    public RetrieveChatRoomFriends(Activity activity, ArrayList<String> friendList, ChatRoomFriendListAdapter chatRoomFriendListAdapter) {
        this.createChatRoomActivity = (CreateChatRoomActivity) activity;
        this.friendList = friendList;
        this.chatRoomFriendListAdapter = chatRoomFriendListAdapter;

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

        try {
            boolean error = jsonObject.getBoolean("error");

            if (!error) {
                JSONArray friends = jsonObject.getJSONArray("friends");
                updateFriendsList(friends);
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
    }

    private void updateFriendsList(final JSONArray friends) {
        createChatRoomActivity.runOnUiThread(new Runnable() {
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
