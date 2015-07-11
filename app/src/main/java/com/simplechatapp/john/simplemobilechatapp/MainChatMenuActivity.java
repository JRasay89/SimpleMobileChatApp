package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
import com.simplechatapp.john.simplemobilechatapp.manager.SessionManager;

/**
 * Created by John on 5/27/2015.
 */
public class MainChatMenuActivity extends Activity {

    private SQLiteHandler databaseHandler;
    private SessionManager sessionManager;

    private Button chatsButton;
    private Button friendsButton;
    private Button searchButton;
    private Button invitesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        databaseHandler = new SQLiteHandler(this);
        sessionManager = new SessionManager(this);

        //Open ChatRoomsActivity
        chatsButton = (Button) findViewById(R.id.mainMenu_ChatsButton);
        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainChatMenuActivity.this, ChatRoomsActivity.class);
                startActivity(intent);
            }
        });

        //Open FriendsActivity
        friendsButton = (Button) findViewById(R.id.mainMenu_FriendsButton);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainChatMenuActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

        //Open SearchActivity
        searchButton  = (Button) findViewById(R.id.mainMenu_SearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainChatMenuActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        //Open InvitesActivity
        invitesButton = (Button) findViewById(R.id.mainMenu_InvitesButton);
        invitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainChatMenuActivity.this, InvitesActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        //Delete the user
        databaseHandler.deleteUsers();
        sessionManager.setLogin(false);

        //Open LoginActivity
        Intent intent = new Intent(MainChatMenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
