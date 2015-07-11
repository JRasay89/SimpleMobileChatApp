package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.helper.RegisterClient;

/**
 * Created by John on 5/18/2015.
 */
public class RegisterActivity extends Activity {

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
                    //Toast.makeText(RegisterActivity.this, "Good Job!", Toast.LENGTH_LONG).show();
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

        new RegisterClient(this).execute(username, password);
    }
}
