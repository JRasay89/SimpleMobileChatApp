package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.cutomadapter.SearchListAdapter;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
import com.simplechatapp.john.simplemobilechatapp.helper.SearchUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by John on 6/13/2015.
 */
public class SearchActivity extends Activity {

    private final static String TAG = SearchActivity.class.getSimpleName();

    private SQLiteHandler databaseHandler;
    private HashMap<String, String> myUser;
    private String currentUser;

    private EditText mySearchText;
    private Button mySearchButton;

    private SearchListAdapter searchListAdapter;
    private ArrayList<String> usernameList;
    private ListView mySearchResultListView;

    //Search Result List
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize database handler
        databaseHandler = new SQLiteHandler(this);
        //Get the user
        myUser = databaseHandler.getUser();
        //Get the username of the current user
        currentUser = myUser.get("name").toString();

        //Search result list
        usernameList = new ArrayList<String>();
        searchListAdapter = new SearchListAdapter(this, currentUser, usernameList);
        mySearchResultListView = (ListView) findViewById(R.id.search_mySearchResultListView);
        mySearchResultListView.setAdapter(searchListAdapter);

        //Initialize EditText
        mySearchText = (EditText) findViewById(R.id.search_mySearchText);

        //Initialize Button
        mySearchButton = (Button) findViewById(R.id.search_mySearchButton);
        mySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mySearchText.getText().toString();

                //Check for empty data in the search box
                if (username.trim().length() <= 0) {
                    Toast.makeText(SearchActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                }
                else {
                    searchUser(username);
                }

            }
        });
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
    private void searchUser(String username) {
        new SearchUser(this, currentUser, usernameList, searchListAdapter).execute(username);
    }
}
