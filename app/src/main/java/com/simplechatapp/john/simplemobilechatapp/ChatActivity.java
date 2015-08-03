package com.simplechatapp.john.simplemobilechatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.simplechatapp.john.simplemobilechatapp.config.AppConfig;
import com.simplechatapp.john.simplemobilechatapp.cutomadapter.MessageListAdapter;
import com.simplechatapp.john.simplemobilechatapp.manager.SQLiteHandler;
import com.simplechatapp.john.simplemobilechatapp.other.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by John on 7/2/2015.
 */
public class ChatActivity extends Activity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private SQLiteHandler databaseHandler;
    private HashMap<String, String> myUser;
    private String currentUser;

    private TextView myChatRoomName;
    private EditText myChatText;
    private Button mySendButton;


    private int roomID;
    private String chatRoomName;

    private ArrayList<Message> messageList;
    private ListView myMessageListView;
    private MessageListAdapter messageListAdapter;


    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Enable action bar back navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        roomID = extras.getInt("roomID");
        chatRoomName = extras.getString("roomName");
        Log.d(TAG, "roomID: " + roomID + " " + "roomName: " + chatRoomName);
        myChatRoomName = (TextView) findViewById(R.id.chat_myChatRoomName);
        myChatRoomName.setText("Welcome to " + chatRoomName + " room!");

        //Initialize database handler
        databaseHandler = new SQLiteHandler(this);
        //Get the user
        myUser = databaseHandler.getUser();
        //Get the username of the current user
        currentUser = myUser.get("name").toString();

        //Initialize Message List
        messageList = new ArrayList<>();
        myMessageListView = (ListView) findViewById(R.id.chat_myMessageListView);
        messageListAdapter = new MessageListAdapter(this, messageList);
        myMessageListView.setAdapter(messageListAdapter);


        //Initialize serverConnection and start the thread
        serverConnection = new ServerConnection(currentUser);
        Thread serverThread = new Thread(serverConnection);
        serverThread.start();

        //Initialize chat edittext
        myChatText = (EditText) findViewById(R.id.chat_myChatText);

        //Initialize send button
        mySendButton = (Button) findViewById(R.id.chat_mySendButton);
        mySendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = myChatText.getText().toString();
                serverConnection.setMessageToSend(message);
                myChatText.setText("");

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                serverConnection.disconnect();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //Class to handle the server connection
    private class ServerConnection implements Runnable {

        private String userName;
        private String messageToSend;
        private boolean isConnected;

        private Socket socket;
        private DataOutputStream dataOut;
        private DataInputStream dataIn;



        //Flags to determine the type of response
        //FLAG_NEW = A new client has connected
        //FLAG_MESSAGE = A new message has been sent
        //FLAG_EXIT = A client has logout
        private final String FLAG_NEW = "new";
        private final String FLAG_MESSAGE = "message";
        private final String FLAG_EXIT = "exit";

        public ServerConnection(String userName) {

            this.userName = userName;
            this.isConnected = true;
            this.messageToSend = "";

            socket = null;
            dataOut = null;
            dataIn = null;
        }

        @Override
        public void run() {

            try {
                socket = new Socket(AppConfig.SERVERIPADDRESS, AppConfig.SERVERSOCKETPORT);

                dataOut = new DataOutputStream(socket.getOutputStream());
                dataIn = new DataInputStream(socket.getInputStream());

                //Initial message to send to the server
                sendMessage(userName, roomID, "", FLAG_NEW);


                while (IsConnected()) {
                    try {
                        if (dataIn.available() > 0) {
                            String receivedMessage = dataIn.readUTF();

                            parseMessage(receivedMessage);
                        }

                        if (!messageToSend.equals("")) {

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                sendMessage(userName, roomID, " ", FLAG_EXIT);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //If the data output stream is not null, close the stream
                if (dataOut != null) {
                    try {
                        dataOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //If the data input stream is not null, close the stream
                if (dataIn != null) {
                    try {
                        dataIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //If socket is not null, close the socket
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } //End of finally
        }

        private boolean IsConnected() {
            return this.isConnected;
        }

        private void sendMessage(String sender, int roomID, String messageToSend, String flag) {
            try {
                //Create a JSONObject to send to the server
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("name", sender);
                jsonObj.put("roomID", roomID);
                jsonObj.put("message", messageToSend);
                jsonObj.put("flag", flag);

                //Send to the server
                dataOut.writeUTF(jsonObj.toString());
                dataOut.flush();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        /**
         * Parse the message receive from the server
         * @param receivedMessage received from the server
         */
        private void parseMessage(String receivedMessage) {
            try {
                JSONObject jsonObj = new JSONObject(receivedMessage);

                String flag = jsonObj.getString("flag");

                if (flag.equals(FLAG_NEW)) {

                }

                else if (flag.equals(FLAG_MESSAGE)) {

                }

                else if (flag.equals(FLAG_EXIT)) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } //End of parseMessage

        private void setMessageToSend(String messageToSend) {
            this.messageToSend = messageToSend;
        }
        /**
         * Disconnect the client's connection to the server
         */
        private void disconnect() {
            this.isConnected = false;
        } //End of disconnect
    }
}
