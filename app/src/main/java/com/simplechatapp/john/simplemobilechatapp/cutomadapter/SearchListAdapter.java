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


import com.simplechatapp.john.simplemobilechatapp.R;
import com.simplechatapp.john.simplemobilechatapp.SearchActivity;
import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.helper.ClientHttpRequest;
import com.simplechatapp.john.simplemobilechatapp.helper.MyProgressDialog;
import com.simplechatapp.john.simplemobilechatapp.other.Method;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                new AddInviteClient(searchActivity).execute(currentUSer, (String) myInviteButton.getTag());
            }
        });

        myUsernameText.setText(username);
        return convertView;
    }

    /**
     * Private class AddInviteClient
     */
    private class AddInviteClient extends AsyncTask<String, Void, String> {
        private final String TAG = AddInviteClient.class.getSimpleName();

        private ClientHttpRequest clientHttpRequest;
        private MyProgressDialog progressDialog;

        public AddInviteClient(Activity activity) {
            clientHttpRequest = new ClientHttpRequest();
            progressDialog = new MyProgressDialog(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Sending invite");
            progressDialog.showDialog();
        }


        @Override
        protected String doInBackground(String... args) {
            String username = (String) args[0];
            //The person to be invited as a friend
            String friend = (String) args[1];

            //Create params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tag", "friend_invite"));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("friend", friend));

            JSONObject jsonObject = clientHttpRequest.makeHttpRequest(AppConfig.URL_LOGIN, Method.POST, params);
            Log.d(TAG, "Response: " + jsonObject.toString());

            try {
                boolean error = jsonObject.getBoolean("error");
                if (!error) {
                    final String success_msg = jsonObject.getString("success_msg");
                    searchActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(searchActivity, success_msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    final String error_msg = jsonObject.getString("error_msg");
                    searchActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(searchActivity, error_msg, Toast.LENGTH_LONG).show();
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
    }
}
