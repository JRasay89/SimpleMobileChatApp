package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.FriendListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteConnect;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 6/24/2015.
 */
public class FriendsActivity extends Activity {

    private SQLiteConnect sqLiteConnect;

    private ArrayList<String> friendList;
    private ListView myFriendListView;
    private FriendListAdapter friendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sqLiteConnect = SQLiteConnect.getInstance(getApplication());

        friendList = new ArrayList<String>();
        friendListAdapter = new FriendListAdapter(this, friendList);
        myFriendListView = (ListView) findViewById(R.id.friends_myFriendsListView);
        myFriendListView.setAdapter(friendListAdapter);

        retrieveFriends();

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

    private void retrieveFriends() {
        new RetrieveFriends().execute(sqLiteConnect.getUser());
    }

    private class RetrieveFriends extends AsyncTask<String, Void, String> {
        private final String TAG = RetrieveFriends.class.getSimpleName();

        private ClientHttpRequest clientHttpRequest;


        public RetrieveFriends() {

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
                    FriendsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FriendsActivity.this, error_msg, Toast.LENGTH_LONG).show();
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

        /**
         * Updates the friend list and display them on the ListView
         * @param friends
         */
        private void updateFriendsList(final JSONArray friends) {
            FriendsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < friends.length(); i++) {
                            friendList.add(friends.getString(i));
                            friendListAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
}
