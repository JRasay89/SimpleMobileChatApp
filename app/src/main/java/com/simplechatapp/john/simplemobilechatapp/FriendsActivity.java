package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.simplechatapp.john.simplemobilechatapp.cutomadapter.FriendListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.RetrieveFriends;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by John on 6/24/2015.
 */
public class FriendsActivity extends Activity {

    private SQLiteHandler databaseHandler;
    private HashMap<String, String> myUser;
    private String currentUser;

    private ArrayList<String> friendList;
    private ListView myFriendListView;
    private FriendListAdapter friendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);


        //Initialize database handler
        databaseHandler = new SQLiteHandler(this);
        //Get the user
        myUser = databaseHandler.getUser();
        //Get the username of the current user
        currentUser = myUser.get("name").toString();

        friendList = new ArrayList<String>();
        friendListAdapter = new FriendListAdapter(this, friendList);
        myFriendListView = (ListView) findViewById(R.id.friends_myFriendsListView);
        myFriendListView.setAdapter(friendListAdapter);

        retrieveFriends();

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

    private void retrieveFriends() {
        new RetrieveFriends(this, friendList, friendListAdapter).execute(currentUser);
    }
}
