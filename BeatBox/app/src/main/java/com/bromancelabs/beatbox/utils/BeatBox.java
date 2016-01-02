package com.bromancelabs.beatbox.utils;

import android.content.Context;
import android.content.res.AssetManager;

public class BeatBox {
    private static final String TAG = BeatBox.class.getSimpleName();
    private static final String SOUNDS_FOLDER = "sample_sounds";

    private AssetManager mAssets;
    
    public BeatBox(Context context) {
        mAssets = context.getAssets();
    }
}

