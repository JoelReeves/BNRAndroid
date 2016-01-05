package com.bromancelabs.photogallery.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;

public final class ModalDialogService {

    public static Dialog showDialog(@NonNull Activity activity, int title, int message) {
        if (activity.isFinishing()) {
            return null;
        }

        AppCompatDialog dialog = new AlertDialog.Builder(activity)
            .setTitle(title)
            .setNeutralButton(android.R.string.ok, null)
            .setMessage(message)
            .create();
        dialog.show();

        return dialog;
    }

    public static Dialog showProgressDialog(@NonNull Activity activity) {
        if (activity.isFinishing()) {
            return null;
        }

        Dialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();

        return progressDialog;
    }
}
