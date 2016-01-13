package com.bromancelabs.sunset.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.sunset.fragments.SunsetFragment;

public class SunsetActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SunsetFragment.newInstance();
    }
}
