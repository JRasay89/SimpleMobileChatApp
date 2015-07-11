package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.helper.AuthenticateClient;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
import com.simplechatapp.john.simplemobilechatapp.manager.SessionManager;

/**
 * Created by John on 5/18/2015.
 */
public class LoginActivity extends Activity {

    //SQLiteHandler
    private SQLiteHandler databaseHandler;

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
}
