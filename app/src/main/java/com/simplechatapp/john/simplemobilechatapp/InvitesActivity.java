package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.simplechatapp.john.simplemobilechatapp.cutomadapter.InviteListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.RetrieveInvites;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by John on 6/20/2015.
 */
public class InvitesActivity extends Activity {

    private SQLiteHandler databaseHandler;
    private HashMap<String, String> myUser;
    private String currentUser;

    private ArrayList<String> inviteList;
    private InviteListAdapter inviteListAdapter;
    private ListView myInviteListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize database handler
        databaseHandler = new SQLiteHandler(this);
        //Get the user
        myUser = databaseHandler.getUser();
        //Get the username of the current user
        currentUser = myUser.get("name").toString();


        inviteList = new ArrayList<String>();
        inviteListAdapter = new InviteListAdapter(this, currentUser, inviteList);
        myInviteListView = (ListView) findViewById(R.id.invites_myInviteListView);
        myInviteListView.setAdapter(inviteListAdapter);

        //Retrieve the invites
        retrieveInvites();
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

    private void retrieveInvites() {
        new RetrieveInvites(this, inviteList, inviteListAdapter).execute(currentUser);
    }

    /**
     * Get the InviteListAdapter
     * @return the InviteListAdapter
     */
    public InviteListAdapter getInviteListAdapter () {
        return this.inviteListAdapter;
    }
}
