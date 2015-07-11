package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.InvitesActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.InviteListAdapter;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 6/20/2015.
 */
public class RetrieveInvites extends AsyncTask<String, Void, String> {

    private static final String TAG = RetrieveInvites.class.getSimpleName();

    private ClientHttpRequest clientHttpRequest;
    private InvitesActivity invitesActivity;
    private ArrayList<String> inviteList;
    private InviteListAdapter inviteListAdapter;
    private ProgressDialog pDialog;

    public RetrieveInvites(Activity activity, ArrayList<String> inviteList, InviteListAdapter inviteListAdapter) {
        this.clientHttpRequest = new ClientHttpRequest();
        this.invitesActivity = (InvitesActivity) activity;
        this.inviteList = inviteList;
        this.inviteListAdapter = inviteListAdapter;
        pDialog = new ProgressDialog(invitesActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Retrieving Invites...");
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... args) {
        String username = args[0];

        //Create params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "retrieve_invites"));
        params.add(new BasicNameValuePair("username", username));

        JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
        Log.d(TAG, "Response: " + jsonObject.toString());

        try {
            boolean error = jsonObject.getBoolean("error");
            if (!error) {
                JSONArray invites = jsonObject.getJSONArray("invite_senders");
                updateInviteList(invites);
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
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    /**
     * Update the list of invites
     * @param invites is a JsonArray containing the invites that will be displayed on the screen
     */
    private void updateInviteList(final JSONArray invites) {
        invitesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < invites.length(); i++) {
                    try {
                        inviteList.add(invites.getString(i));
                        inviteListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
