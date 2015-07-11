package com.simplechatapp.john.simplemobilechatapp.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by John on 5/26/2015.
 */
public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();

    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context context;
    int MODE_PRIVATE = 0;

    private static final String MY_PREF_FILENAME = "MyPrefFile";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public SessionManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(MY_PREF_FILENAME, MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();

        Log.d(TAG, "User logged in successfully");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

}
