package com.bromancelabs.photogallery.utils

import android.app.Activity
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat

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