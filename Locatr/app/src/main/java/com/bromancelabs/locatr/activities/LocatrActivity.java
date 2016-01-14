package com.bromancelabs.locatr.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.locatr.activities.SingleFragmentActivity;
import com.bromancelabs.locatr.fragments.LocatrFragment;

public class LocatrActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return LocatrFragment.newInstance();
    }
}
