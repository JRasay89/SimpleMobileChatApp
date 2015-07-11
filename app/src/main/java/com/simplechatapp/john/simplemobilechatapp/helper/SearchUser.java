package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.SearchActivity;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.SearchListAdapter;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
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
public class SearchUser extends AsyncTask<String, Void, String> {

    private static final String TAG = SearchUser.class.getSimpleName();

    //The username of the current user of the app
    private String currentUser;

    //Use to make http request
    private ClientHttpRequest clientHttpRequest;

    //Reference to SearchActivity
    SearchActivity searchActivity;

    // Progress Dialog
    private ProgressDialog pDialog;

    private ArrayList<String> usernameList;
    private SearchListAdapter searchListAdapter;

    public SearchUser(Activity activity, String currentUser, ArrayList<String> usernameList, SearchListAdapter searchListAdapter) {
        searchActivity = (SearchActivity) activity;
        this.currentUser = currentUser;

        clientHttpRequest = new ClientHttpRequest();
        pDialog = new ProgressDialog(searchActivity);
        this.usernameList = usernameList;
        this.searchListAdapter = searchListAdapter;
        Log.d(TAG, "onCreate" );


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
        pDialog.setMessage("Searching...");
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
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
                searchActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(searchActivity, errorMsg, Toast.LENGTH_LONG).show();
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
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    /**
     * Updates the ListView
     * @param usernames is a JSONArray containing the usernames that will be displayed on the screen
     */
    private void updateSearchList(final JSONArray usernames) {
        searchActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!usernameList.isEmpty()){
                        usernameList.clear();
                        searchListAdapter.notifyDataSetChanged();
                    }
                    for (int i = 0; i < usernames.length(); i++) {
                        //Skip if it is user's own username
                        if (!usernames.getString(i).equals(currentUser))
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
