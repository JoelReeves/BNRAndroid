package com.bromancelabs.photogallery.services

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationManagerCompat
import android.util.Log

class NotificationReceiverKt : BroadcastReceiver() {

    companion object {
        val TAG = NotificationReceiverKt::class.java.simpleName
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Received result: $resultCode")

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(requestCode, intent.getParcelableExtra(PollService.NOTIFICATION))
    }
}