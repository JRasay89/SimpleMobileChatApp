package com.simplechatapp.john.simplemobilechatapp.helper;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by JohnBen on 8/3/2015.
 */
public class MyProgressDialog {

    private ProgressDialog progressDialog;

    public MyProgressDialog(Context context) {

        progressDialog = new ProgressDialog(context);
    }

    public void setMessage(String message) {
        progressDialog.setMessage(message);
    }
    public void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
