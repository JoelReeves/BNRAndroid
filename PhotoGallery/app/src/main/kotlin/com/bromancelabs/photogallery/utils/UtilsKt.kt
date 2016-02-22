package com.bromancelabs.photogallery.utils

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog

object Dialog {
    fun showDialog(activity: Activity, @StringRes title: Int, @StringRes message: Int): Dialog? {
        var dialog: Dialog? = null

        if (!activity.isFinishing) {
            dialog = AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setNeutralButton(android.R.string.ok, null)
                    .setMessage(message)
                    .create()
            dialog.show()
        }

        return dialog
    }
}

fun showProgressDialog(activity: Activity) : Dialog? {
    if (activity.isFinishing) return null

    val dialog = ProgressDialog(activity)
    dialog.show()

    return dialog
}

object Network {
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isAvailable
                && cm.activeNetworkInfo.isConnected
    }
}

object SnackBar {
    private fun createSnackBar(activity: Activity, @StringRes text: Int): Snackbar {
        return Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT)
    }

    fun showPlainSnackBar(activity: Activity, @StringRes text: Int) {
        createSnackBar(activity, text).show()
    }

    fun showStyledBar(activity: Activity, @StringRes text: Int, @ColorRes textColor: Int, @ColorRes backgroundColor: Int) {
        val snackbar = createSnackBar(activity, text)
        snackbar.setActionTextColor(ContextCompat.getColor(activity, textColor))
        snackbar.view.setBackgroundColor(ContextCompat.getColor(activity, backgroundColor))
        snackbar.show()
    }
}