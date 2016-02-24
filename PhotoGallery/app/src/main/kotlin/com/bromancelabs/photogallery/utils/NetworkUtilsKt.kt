package com.bromancelabs.photogallery.utils

import android.content.Context
import android.net.ConnectivityManager

fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isAvailable
            && cm.activeNetworkInfo.isConnected
}