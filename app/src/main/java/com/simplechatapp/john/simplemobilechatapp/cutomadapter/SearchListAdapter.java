package com.simplechatapp.john.simplemobilechatapp.cutomadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.simplechatapp.john.simplemobilechatapp.R;
import com.simplechatapp.john.simplemobilechatapp.SearchActivity;
import com.simplechatapp.john.simplemobilechatapp.helper.AddInviteClient;

import java.util.ArrayList;

/**
 * Created by John on 6/14/2015.
 */
public class SearchListAdapter extends BaseAdapter {
    private SearchActivity searchActivity;
    private String currentUSer;
    private ArrayList<String> usernameList;
    private LayoutInflater layoutInflater;


    public SearchListAdapter(Context context, String currentUser, ArrayList<String> usernameList) {
        searchActivity = (SearchActivity) context;
        this.currentUSer = currentUser;
        this.usernameList = usernameList;
        this.layoutInflater = LayoutInflater.from(searchActivity);
    }

    @Override
    public int getCount() {
        return usernameList.size();
    }

    @Override
    public Object getItem(int position) {
       return usernameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the username
        String username = usernameList.get(position);

        convertView = layoutInflater.inflate(R.layout.search_list, null);
        TextView myUsernameText = (TextView) convertView.findViewById(R.id.search_myUsernameText);
        final Button myInviteButton = (Button) convertView.findViewById(R.id.search_myInviteButton);

        //Display the username on list
        myUsernameText.setText(username);
        //Set a listener on the buttons
        myInviteButton.setTag(username);
        myInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(searchActivity, "TO be implemented" + (String) myInviteButton.getTag(), Toast.LENGTH_LONG).show();
                new AddInviteClient(searchActivity).execute("friend_invite", currentUSer, (String) myInviteButton.getTag());
            }
        });

        myUsernameText.setText(username);
        return convertView;
    }
}
