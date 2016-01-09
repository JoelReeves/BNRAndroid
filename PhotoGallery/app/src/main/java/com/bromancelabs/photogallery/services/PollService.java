package com.bromancelabs.photogallery.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

        Log.i(TAG, "Received an intent: " + intent);
    }
}
