package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.InvitesActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.InviteListAdapter;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 6/22/2015.
 */
public class AcceptInvite extends AsyncTask<String, Void, String> {
    private final String TAG = AcceptInvite.class.getName();

    private ClientHttpRequest clientHttpRequest;
    private InvitesActivity invitesActivity;
    private ArrayList<String> inviteList;
    private InviteListAdapter inviteListAdapter;

    public AcceptInvite(Activity activity, ArrayList<String> inviteList, InviteListAdapter inviteListAdapter) {
        clientHttpRequest = new ClientHttpRequest();
        this.invitesActivity = (InvitesActivity) activity;
        this.inviteList = inviteList;
        this.inviteListAdapter = inviteListAdapter;
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
        params.add(new BasicNameValuePair("tag", "accept_invites"));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("friend", friend));

        Log.d(TAG, "Before response");
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
                inviteListAdapter.notifyDataSetChanged();
                Log.d(TAG, "After data set changed!");

            }
        });
    }
}
