package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.SearchListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.helper.MyProgressDialog;
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
 * Created by John on 6/13/2015.
 */
public class SearchActivity extends Activity {

    private final String TAG = SearchActivity.class.getSimpleName();

    private SQLiteConnect sqLiteConnect;

    private EditText mySearchText;
    private Button mySearchButton;

    private SearchListAdapter searchListAdapter;
    private ArrayList<String> usernameList;
    private ListView mySearchResultListView;

    //Search Result List
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sqLiteConnect = SQLiteConnect.getInstance(getApplication());

        //Search result list
        usernameList = new ArrayList<String>();
        searchListAdapter = new SearchListAdapter(this, sqLiteConnect.getUser(), usernameList);
        mySearchResultListView = (ListView) findViewById(R.id.search_mySearchResultListView);
        mySearchResultListView.setAdapter(searchListAdapter);

        //Initialize EditText
        mySearchText = (EditText) findViewById(R.id.search_mySearchText);

        //Initialize Button
        mySearchButton = (Button) findViewById(R.id.search_mySearchButton);
        mySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mySearchText.getText().toString();

                //Check for empty data in the search box
                if (username.trim().length() <= 0) {
                    Toast.makeText(SearchActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                }
                else {
                    searchUser(username);
                }

            }
        });
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
    private void searchUser(String username) {
        new SearchUser().execute(username);
    }

    /**
     * Private Class SearchUser
     */
    public class SearchUser extends AsyncTask<String, Void, String> {
        private final String TAG = SearchUser.class.getSimpleName();

        //Use to make http request
        private ClientHttpRequest clientHttpRequest;

        // Progress Dialog
        private MyProgressDialog progressDialog;


        public SearchUser() {
            clientHttpRequest = new ClientHttpRequest();
            progressDialog = new MyProgressDialog(SearchActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            progressDialog.setMessage("Searching...");
            progressDialog.showDialog();

        }

        @Override
        protected String doInBackground(String... args) {
            //Get the given arguments
            String username = args[0];
            Log.d(TAG, "Username: " + username);

            //Create params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", "search"));
            params.add(new BasicNameValuePair("username", username));

            Log.d(TAG, "Before sending params");

            JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_LOGIN, Method.POST, params);

            Log.d(TAG, "Response: " + jsonObject.toString());

            try {
                boolean error = jsonObject.getBoolean("error");

                if (!error) {
                    JSONArray usernames = jsonObject.getJSONArray("usernames");

                    updateSearchList(usernames);

                }
                else {
                    //Display error message
                    final String errorMsg = jsonObject.getString("error_msg");
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, errorMsg, Toast.LENGTH_LONG).show();
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
            Log.d(TAG, "onPostExecute");
            progressDialog.hideDialog();
        }

        /**
         * Updates the ListView
         * @param usernames is a JSONArray containing the usernames that will be displayed on the screen
         */
        private void updateSearchList(final JSONArray usernames) {
            SearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!usernameList.isEmpty()){
                            usernameList.clear();
                            searchListAdapter.notifyDataSetChanged();
                        }
                        for (int i = 0; i < usernames.length(); i++) {
                            //Skip if it is user's own username
                            if (!usernames.getString(i).equals(sqLiteConnect.getUser()))
                            {
                                usernameList.add(usernames.getString(i));
                                searchListAdapter.notifyDataSetChanged();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
