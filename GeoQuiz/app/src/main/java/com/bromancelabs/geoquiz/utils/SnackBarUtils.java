package com.bromancelabs.geoquiz.utils;

import android.app.Activity;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;

public final class SnackBarUtils {

    public static void showSnackBar(@NonNull Activity activity, @StringRes int text, @ColorRes int textColor, @ColorRes int backgroundColor) {
        final Snackbar snackbar = Snackbar.make(
                activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(ContextCompat.getColor(activity, textColor));
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, backgroundColor));
        snackbar.show();
    }
}
