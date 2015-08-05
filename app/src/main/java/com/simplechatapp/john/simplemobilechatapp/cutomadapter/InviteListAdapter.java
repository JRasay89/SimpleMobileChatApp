package com.simplechatapp.john.simplemobilechatapp.cutomadapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.InvitesActivity;
import com.simplechatapp.john.simplemobilechatapp.R;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Private class AcceptInvite
     */
    private class AcceptInvite extends AsyncTask<String, Void, String> {
        private final String TAG = AcceptInvite.class.getName();

        private ClientHttpRequest clientHttpRequest;


        public AcceptInvite(Activity activity, ArrayList<String> inviteList, InviteListAdapter inviteListAdapter) {
            clientHttpRequest = new ClientHttpRequest();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            String username = args[0];
            String friend = args[1];
            //Create params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", "accept_invite"));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("friend", friend));

            JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
            Log.d(TAG, "Response: " + jsonObject.toString());

            try {
                final boolean error = jsonObject.getBoolean("error");

                if (!error) {
                    removeAcceptedInvite(friend);
                    final String success_msg = jsonObject.getString("success_msg");
                    invitesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(invitesActivity, success_msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    final String error_msg = jsonObject.getString("error_msg");
                    invitesActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(invitesActivity, error_msg, Toast.LENGTH_LONG).show();
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
        }

        private void removeAcceptedInvite(final String friend) {
            invitesActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Before Remove");
                    inviteList.remove(friend);
                    for (int i = 0; i < inviteList.size(); i++) {
                        Log.d(TAG, "INVITELIST ITEMS: " + i + "= " + inviteList.get(i));
                    }
                    Log.d(TAG, "After Remove");
                    invitesActivity.getInviteListAdapter().notifyDataSetChanged();
                    Log.d(TAG, "After data set changed!");

                }
            });
        }
    }
}
