package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.SearchActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 6/18/2015.
 */
public class AddInviteClient extends AsyncTask<String, Void, String> {
    private final static String TAG = AddInviteClient.class.getSimpleName();

    private ClientHttpRequest clientHttpRequest;
    private SearchActivity searchActivity;
    private ProgressDialog pDialog;

    public AddInviteClient(Activity activity) {
        clientHttpRequest = new ClientHttpRequest();
        searchActivity = (SearchActivity) activity;
        pDialog = new ProgressDialog(activity);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Sending invite");
        if(!pDialog.isShowing()) {
          pDialog.show();
        }
    }


    @Override
    protected String doInBackground(String... args) {
        //Send friend invite or accepting a friend invite
        String requestType = (String) args[0];
        //The user who is sending the invite
        String username = (String) args[1];
        //The person to be invited as a friend
        String friend = (String) args[2];

        //Create params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", requestType));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("friend", friend));

        Log.d(TAG, "Before Request");
        JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
        Log.d(TAG, "After Request");
        Log.d(TAG, "Response: " + jsonObject.toString());

        try {
            boolean error = jsonObject.getBoolean("error");
            if (!error) {
                final String success_msg = jsonObject.getString("success_msg");
                searchActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(searchActivity, success_msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                final String error_msg = jsonObject.getString("error_msg");
                searchActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(searchActivity, error_msg, Toast.LENGTH_LONG).show();
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
        if(pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}

