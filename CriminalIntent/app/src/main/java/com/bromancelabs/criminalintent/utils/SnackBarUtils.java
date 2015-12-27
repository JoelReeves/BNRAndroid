package com.bromancelabs.criminalintent.utils;

import android.app.Activity;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;

public final class SnackBarUtils {

    private static Snackbar createSnackBar(@NonNull Activity activity, @StringRes int text) {
        return Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT);
    }

    private static Snackbar createSnackBar(@NonNull Activity activity, String text) {
        return Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT);
    }

    public static void showPlainSnackBar(@NonNull Activity activity, @StringRes int text) {
        createSnackBar(activity, text).show();
    }

    public static void showPlainSnackBar(@NonNull Activity activity, String text) {
        createSnackBar(activity, text).show();
    }

    public static void showStyledBar(@NonNull Activity activity, @StringRes int text, @ColorRes int textColor, @ColorRes int backgroundColor) {
        Snackbar snackbar = createSnackBar(activity, text);
        snackbar.setActionTextColor(ContextCompat.getColor(activity, textColor));
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, backgroundColor));
        snackbar.show();
    }

    public static void showStyledBar(@NonNull Activity activity, String text, @ColorRes int textColor, @ColorRes int backgroundColor) {
        Snackbar snackbar = createSnackBar(activity, text);
        snackbar.setActionTextColor(ContextCompat.getColor(activity, textColor));
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, backgroundColor));
        snackbar.show();
    }
}
