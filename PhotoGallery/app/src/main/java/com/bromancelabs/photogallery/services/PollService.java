package com.bromancelabs.photogallery.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bromancelabs.photogallery.fragments.PhotoGalleryFragment;
import com.bromancelabs.photogallery.utils.NetworkUtils;

public class PollService extends IntentService {
    private static final String TAG = PollService.class.getSimpleName();

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
}
