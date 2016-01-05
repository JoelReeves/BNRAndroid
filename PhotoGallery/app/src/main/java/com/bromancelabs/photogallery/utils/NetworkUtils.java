package com.bromancelabs.photogallery.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public final class NetworkUtils {

    private NetworkUtils() {}

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }
}
