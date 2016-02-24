package com.bromancelabs.photogallery.utils

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog

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

fun showProgressDialog(activity: Activity) : Dialog? {
    if (activity.isFinishing) return null

    val dialog = ProgressDialog(activity)
    dialog.show()

    return dialog
}