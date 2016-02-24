package com.bromancelabs.photogallery.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.Fragment
import android.util.Log
import com.bromancelabs.photogallery.services.PollService

abstract class VisibleFragmentKtKt : Fragment() {

    companion object {
        val TAG = VisibleFragmentKtKt::class.java.simpleName
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollService.ACTION_SHOW_NOTIFICATION)
        activity.registerReceiver(mOnShowNotification, filter, PollService.PRIVATE_PERMISSION, null)
    }

    override fun onStop() {
        super.onStop()
        activity.unregisterReceiver(mOnShowNotification)
    }

    private val mOnShowNotification = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // If we receive this, we're visible, so cancel the notification
            Log.d(TAG, "canceling notification")
            resultCode = Activity.RESULT_CANCELED
        }
    }
}