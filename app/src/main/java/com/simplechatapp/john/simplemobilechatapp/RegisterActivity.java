package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.helper.MyProgressDialog;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteConnect;
import com.simplechatapp.john.simplemobilechatapp.manager.SessionManager;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 5/18/2015.
 */
public class RegisterActivity extends Activity {

    //SQLiteConnect
    private SQLiteConnect sqLiteConnect;
    //SessionManager
    private SessionManager sessionManager;

    //EditText
    private EditText myUsernameText;
    private EditText myPasswordText;

    //Button
    private Button myRegisterButton;
    private Button myLoginLinkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sqLiteConnect = SQLiteConnect.getInstance(getApplication());
        sessionManager = new SessionManager(this);

        //Intialize EditText
        myUsernameText = (EditText) findViewById(R.id.register_myUsernameText);
        myPasswordText = (EditText) findViewById(R.id.register_myPasswordText);

        //Intialize Button
        myRegisterButton = (Button) findViewById(R.id.register_myRegisterButton);
        myLoginLinkButton = (Button) findViewById(R.id.register_myLoginLinkButton);

        myRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = myUsernameText.getText().toString();
                String password = myPasswordText.getText().toString();

                //Check for empty data in the input box
                //If both username and password are empty
                if (username.trim().length() <= 0 && password.trim().length() <= 0) {
                    Toast.makeText(RegisterActivity.this, "Please enter your username and password!", Toast.LENGTH_LONG).show();
                }
                //If username is empty
                else if (username.trim().length() <= 0 && password.trim().length() > 0) {
                    Toast.makeText(RegisterActivity.this, "Please enter your username!", Toast.LENGTH_LONG).show();
                }
                //If password is empty
                else if (username.trim().length() > 0 && password.trim().length() <= 0) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password!", Toast.LENGTH_LONG).show();
                }
                //All input box is filled out
                else {
                    registerUser(username, password);
                }
            }
        });

        myLoginLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    public void registerUser(String username, String password) {

        new RegisterClient().execute(username, password);
    }


    private class RegisterClient extends AsyncTask<String, Void, String> {
        private final String TAG = RegisterClient.class.getSimpleName();

        private MyProgressDialog progressDialog;
        private ClientHttpRequest clientHttpRequest;


        public RegisterClient() {
            progressDialog = new MyProgressDialog(RegisterActivity.this);
            clientHttpRequest = new ClientHttpRequest();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "RegisterClient onPreExecute");

            progressDialog.setMessage("Registering...");
            progressDialog.showDialog();
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
            Log.d(TAG, "Register Client RESPONSE: " + jsonObject.toString());
            try {

                boolean error = jsonObject.getBoolean("error");
                //If no error occured
                if (!error) {


                    //Display registration successful message
                    final String successMsg = jsonObject.getString("success_msg");
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, successMsg, Toast.LENGTH_LONG).show();
                        }
                    });

                    JSONObject user = jsonObject.getJSONObject("user");
                    String clientUsername = user.getString("username");
                    String clientPassword = user.getString("password");



                    sessionManager.setLogin(true);
                    //Store client name and password in local database
                    sqLiteConnect.addUser(clientUsername, clientPassword);


                    //Open Chat Activity
                    Intent intent = new Intent(RegisterActivity.this, MainChatMenuActivity.class);
                    intent.putExtra("username", clientUsername);
                    RegisterActivity.this.startActivity(intent);
                    RegisterActivity.this.finish();
                }

                else {
                    //Display error message
                    final String errorMsg = jsonObject.getString("error_msg");
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
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
            progressDialog.hideDialog();
        }
    }
}
