package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.InviteListAdapter;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.helper.MyProgressDialog;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteConnect;
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
public class InvitesActivity extends Activity {
    private final String TAG = InvitesActivity.class.getSimpleName();

    private SQLiteConnect sqLiteConnect;

    private ArrayList<String> inviteList;
    private InviteListAdapter inviteListAdapter;
    private ListView myInviteListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sqLiteConnect = SQLiteConnect.getInstance(getApplication());


        inviteList = new ArrayList<String>();
        inviteListAdapter = new InviteListAdapter(this, sqLiteConnect.getUser(), inviteList);
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

    public InviteListAdapter getInviteListAdapter() {
        return inviteListAdapter;
    }

    private void retrieveInvites() {
        new RetrieveInvites().execute(sqLiteConnect.getUser());
    }

    private class RetrieveInvites extends AsyncTask<String, Void, String> {
        private final String TAG = RetrieveInvites.class.getSimpleName();

        private ClientHttpRequest clientHttpRequest;

        private MyProgressDialog progressDialog;

        public RetrieveInvites() {
            this.clientHttpRequest = new ClientHttpRequest();

            progressDialog = new MyProgressDialog(InvitesActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Retrieving Invites...");
            progressDialog.showDialog();
        }

        @Override
        protected String doInBackground(String... args) {
            String username = args[0];

            //Create params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", "retrieve_friendInvites"));
            params.add(new BasicNameValuePair("username", username));

            JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_DATABASE_FUNCTIONS, Method.POST, params);
            Log.d(TAG, "Response: " + jsonObject.toString());

            try {
                boolean error = jsonObject.getBoolean("error");
                if (!error) {
                    JSONArray invites = jsonObject.getJSONArray("invites");
                    updateInviteList(invites);
                }
                else {
                    final String error_msg = jsonObject.getString("error_msg");
                    InvitesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InvitesActivity.this, error_msg, Toast.LENGTH_LONG).show();
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
            progressDialog.hideDialog();
        }

        /**
         * Update the list of invites
         * @param invites is a JsonArray containing the invites that will be displayed on the screen
         */
        private void updateInviteList(final JSONArray invites) {
            InvitesActivity.this.runOnUiThread(new Runnable() {
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
}
