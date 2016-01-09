package com.bromancelabs.photogallery.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import com.bromancelabs.photogallery.fragments.PhotoGalleryFragment;
import com.bromancelabs.photogallery.utils.NetworkUtils;

public class PollService extends IntentService {
    private static final String TAG = PollService.class.getSimpleName();
    private static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!NetworkUtils.isNetworkAvailable(this)){
            return;
        }

        String lastResultId = QueryPreferences.getLastResultId(this);
        sendMessage(lastResultId);
    }

    private void sendMessage(String lastResultId) {
        Intent intent = new Intent(PhotoGalleryFragment.POLL_INTENT);
        intent.putExtra(PhotoGalleryFragment.POLL_KEY_ID, lastResultId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }
}
