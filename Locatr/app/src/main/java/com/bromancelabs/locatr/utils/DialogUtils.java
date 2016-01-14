package com.bromancelabs.locatr.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;

public final class DialogUtils {

    public static Dialog showProgressDialog(@NonNull Activity activity) {
        if (activity.isFinishing()) {
            return null;
        }

        Dialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();

        return progressDialog;
    }
}
