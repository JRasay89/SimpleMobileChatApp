package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.RegisterActivity;
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
public class RegisterClient extends AsyncTask<String, Void, String> {

    private static final String TAG = RegisterClient.class.getSimpleName();

    private ProgressDialog pDialog;
    private ClientHttpRequest clientHttpRequest;
    private SQLiteHandler databaseHandler;
    private RegisterActivity registerActivity;

    public RegisterClient(Activity activity) {
        registerActivity = (RegisterActivity) activity;
        pDialog = new ProgressDialog(registerActivity);
        clientHttpRequest = new ClientHttpRequest();
        databaseHandler = new SQLiteHandler(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "RegisterClient onPreExecute");
        pDialog.setMessage("Registering...");

        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }


    @Override
    protected String doInBackground(String... args) {
        String username = (String) args[0];
        String password = (String) args[1];

        //Create params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "register"));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));

        JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_REGISTER, Method.POST, params);
        Log.d(TAG, "RESPONSE: " + jsonObject.toString());
        try {

            boolean error = jsonObject.getBoolean("error");
            //If no error occured
            if (!error) {

                //Display registration successful message
                final String successMsg = jsonObject.getString("success_msg");
                registerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(registerActivity, successMsg, Toast.LENGTH_LONG).show();
                    }
                });

                JSONObject user = jsonObject.getJSONObject("user");
                String clientUsername = user.getString("username");
                String clientPassword = user.getString("password");

                //Store client name and password in local database
                databaseHandler.addUsers(clientUsername, clientPassword);

                //Open Chat Activity
            }
            else {
                //Display error message
                final String errorMsg = jsonObject.getString("error_msg");
                registerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(registerActivity, errorMsg, Toast.LENGTH_LONG).show();
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
        Log.d(TAG, "RegisterClient onPostExecute");
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

}
