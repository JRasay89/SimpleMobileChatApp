package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.LoginActivity;
import com.simplechatapp.john.simplemobilechatapp.MainChatMenuActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 5/20/2015.
 */
public class AuthenticateClient extends AsyncTask<String, Void, String> {
    private static final String TAG = AuthenticateClient.class.getSimpleName();

    // Progress Dialog
    private ProgressDialog pDialog;

    //Use to make http request
    private ClientHttpRequest clientHttpRequest;

    //Reference to LoginActivity
    private LoginActivity loginActivity;

    public AuthenticateClient(Activity activity) {
        loginActivity = (LoginActivity) activity;
        pDialog = new ProgressDialog(loginActivity);
        clientHttpRequest = new ClientHttpRequest();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Logging in...");
        showDialog();

    }

    @Override
    protected String doInBackground(String... args) {
        String username = (String) args[0];
        String password = (String) args[1];

        //Create params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "login"));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));

        JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_LOGIN, Method.POST, params);
        Log.d(TAG, "Response: " + jsonObject.toString());
        try {

            boolean error = jsonObject.getBoolean("error");

            if (!error) {

                JSONObject user = jsonObject.getJSONObject("user");
                String clientUsername = user.getString("username");
                String clientPassword = user.getString("password");

                loginActivity.getSessionManager().setLogin(true);
                loginActivity.getDatabaseHandler().addUsers(clientUsername, clientPassword);

                //Open Chat Activity
                Intent intent = new Intent(loginActivity, MainChatMenuActivity.class);
                loginActivity.startActivity(intent);
                loginActivity.finish();
            }
            else {
                //Display error message
                final String errorMsg = jsonObject.getString("error_msg");
                loginActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(loginActivity, errorMsg, Toast.LENGTH_LONG).show();
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
        hideDialog();
    }


    private void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

}
