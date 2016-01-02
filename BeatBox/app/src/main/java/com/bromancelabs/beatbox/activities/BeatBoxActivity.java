package com.bromancelabs.beatbox.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.beatbox.fragments.BeatBoxFragment;

public class BeatBoxActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }
}
