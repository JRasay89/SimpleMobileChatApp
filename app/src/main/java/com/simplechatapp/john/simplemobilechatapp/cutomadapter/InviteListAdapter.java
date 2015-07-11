package com.simplechatapp.john.simplemobilechatapp.cutomadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.InvitesActivity;
import com.simplechatapp.john.simplemobilechatapp.R;
import com.simplechatapp.john.simplemobilechatapp.helper.AcceptInvite;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by John on 6/20/2015.
 */
public class InviteListAdapter extends BaseAdapter {

    private InvitesActivity invitesActivity;
    private ArrayList<String> inviteList;
    private LayoutInflater layoutInflater;
    private String currentUser;

    public InviteListAdapter(Context context, String currentUser, ArrayList<String> inviteList) {
        this.invitesActivity = (InvitesActivity) context;
        this.currentUser = currentUser;
        this.inviteList = inviteList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return inviteList.size();
    }

    @Override
    public Object getItem(int position) {
        return inviteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String username = inviteList.get(position);

        convertView = layoutInflater.inflate(R.layout.invites_list, null);
        TextView myUsernameText = (TextView) convertView.findViewById(R.id.invites_myUsernameText);
        final Button myAcceptButton = (Button) convertView.findViewById(R.id.invites_myAcceptButton);

        myUsernameText.setText(username);
        myAcceptButton.setTag(username);
        myAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(invitesActivity, "To be implemented", Toast.LENGTH_LONG).show();
                new AcceptInvite(invitesActivity, inviteList, invitesActivity.getInviteListAdapter()).execute(currentUser, (String) myAcceptButton.getTag());
            }
        });

        return convertView;
    }
}
