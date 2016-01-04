package com.bromancelabs.nerdlauncher.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.nerdlauncher.fragments.NerdLauncherFragment;

public class NerdLauncherActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}
