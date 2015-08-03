package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteConnect;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
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
public class LoginActivity extends Activity {

    //SQLiteHandler
    private SQLiteHandler databaseHandler;
    private SQLiteConnect sqLiteConnect;

    //SessionManager
    private SessionManager sessionManager;


    //EditText
    private EditText myUsernameText;
    private EditText myPasswordText;

    //Buttons
    private Button myLoginButton;
    private Button myRegisterLinkButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize database handler
        databaseHandler = new SQLiteHandler(this);
        sqLiteConnect = SQLiteConnect.getInstance(getApplication());

        //Initialize session manager
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainChatMenuActivity.class);
            startActivity(intent);
            finish();
        }

        //Initialize EditText
        myUsernameText = (EditText) findViewById(R.id.login_myUsernameText);
        myPasswordText = (EditText) findViewById(R.id.login_myPasswordText);

        //Intialize Button
        myLoginButton = (Button) findViewById(R.id.login_myLoginButton);
        myRegisterLinkButton = (Button) findViewById(R.id.login_myRegisterLinkButton);

        //Set Login button listenr
        myLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = myUsernameText.getText().toString();
                String password = myPasswordText.getText().toString();

                //Check for empty data in the input box
                //If both username and password are empty
                if (username.trim().length() <= 0 && password.trim().length() <= 0) {
                    Toast.makeText(LoginActivity.this, "Please enter your username and password!", Toast.LENGTH_LONG).show();
                }
                //If username is empty
                else if (username.trim().length() <= 0 && password.trim().length() > 0) {
                    Toast.makeText(LoginActivity.this, "Please enter your username!", Toast.LENGTH_LONG).show();
                }
                //If password is empty
                else if (username.trim().length() > 0 && password.trim().length() <= 0) {
                    Toast.makeText(LoginActivity.this, "Please enter your password!", Toast.LENGTH_LONG).show();
                }
                //All input box is filled out
                else {
                    //Validate the username and password
                    checkLogin(username, password);
                }
            }
        }); //End of login button listener definition

        //Set Register button listener
        myRegisterLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }); //End of register button listener definition



    }

    public SQLiteHandler getDatabaseHandler() {
        return this.databaseHandler;
    }

    public SessionManager getSessionManager() {
        return this.sessionManager;
    }


    /**
     * Verify the username and password by checking the mysql db
     * @param username of the account
     * @param password of the account
     */
    private void checkLogin(String username, String password) {
        new AuthenticateClient(this).execute(username, password);
    }

    private class AuthenticateClient extends AsyncTask<String, Void, String> {
        private final String TAG = AuthenticateClient.class.getSimpleName();


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
                    String currentUser = user.getString("username");
                    String currentUserPassword = user.getString("password");

                    loginActivity.getSessionManager().setLogin(true);
                    sqLiteConnect.addUser(currentUser, currentUserPassword);

                    //Open Chat Activity
                    Intent intent = new Intent(loginActivity, MainChatMenuActivity.class);
                    intent.putExtra("username", currentUser);
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


}
