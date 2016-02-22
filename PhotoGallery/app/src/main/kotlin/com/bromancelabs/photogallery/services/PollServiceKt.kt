package com.bromancelabs.photogallery.services

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.support.v4.content.LocalBroadcastManager
import com.bromancelabs.photogallery.BuildConfig
import com.bromancelabs.photogallery.fragments.PhotoGalleryFragment
import com.bromancelabs.photogallery.utils.NetworkUtils

class PollServiceKt(name: String = PollServiceKt.TAG) : IntentService(name) {

    companion object {
        val TAG = PollService::class.java.simpleName
        val POLL_INTERVAL = if (BuildConfig.DEBUG) 15000 else AlarmManager.INTERVAL_FIFTEEN_MINUTES
        val ACTION_SHOW_NOTIFICATION = "com.bromancelabs.photogallery.services.SHOW_NOTIFICATION"
        val PRIVATE_PERMISSION = "com.bromancelabs.photogallery.PRIVATE"
        val REQUEST_CODE = "request_code"
        val NOTIFICATION = "notification"

        fun newIntent(context: Context) = Intent(context, PollService::class.java)
    }

    override fun onHandleIntent(intent: Intent) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            return
        }

        sendMessage(QueryPreferences.getLastResultId(this))
    }

    private fun sendMessage(lastResultId: String) {
        val intent = Intent(PhotoGalleryFragment.POLL_INTENT)
        intent.putExtra(PhotoGalleryFragment.POLL_KEY_ID, lastResultId)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun setServiceAlarm(context: Context, isOn: Boolean) {
        val intent = PollServiceKt.newIntent(context)
        val pendingIntent = PendingIntent.getService(context, 0, intent, 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent)
        } else {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        QueryPreferences.setAlarmOn(context, isOn)
    }

    fun isServiceAlarmOn(context: Context): Boolean {
        val intent = PollService.newIntent(context)
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
        return pendingIntent != null
    }
}