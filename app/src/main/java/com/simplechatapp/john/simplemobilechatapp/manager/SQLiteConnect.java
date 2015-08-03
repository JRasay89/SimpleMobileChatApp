package com.simplechatapp.john.simplemobilechatapp.manager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JohnBen on 8/3/2015.
 */
public class SQLiteConnect {
    private static SQLiteConnect sqLiteConnect = null;
    private SQLiteHandler sqLiteHandler = null;
    private Map<String, String> myUser = null;

    private SQLiteConnect(Context context) {
        sqLiteHandler = new SQLiteHandler(context);
    }

    public static SQLiteConnect getInstance(Context context) {
        if (sqLiteConnect == null) {
            sqLiteConnect = new SQLiteConnect(context);
        }
        return sqLiteConnect;
    }

    public void addUser(String username, String password) {
        sqLiteHandler.addUsers(username, password);
        myUser = new HashMap<>();
        myUser = sqLiteHandler.getUser();

    }

    public String getUser() {
        return myUser.get("name").toString();
    }

    public void deleteUser() {
        sqLiteHandler.deleteUsers();
    }
}
