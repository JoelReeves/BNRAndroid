package com.bromancelabs.photogallery.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StartupReceiverKt : BroadcastReceiver() {

    companion object {
        val TAG = StartupReceiverKt::class.java.simpleName
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Received broadcast intent: ${intent.action}")

        val isOn = QueryPreferences.isAlarmOn(context)
        PollService.setServiceAlarm(context, isOn)
    }
}