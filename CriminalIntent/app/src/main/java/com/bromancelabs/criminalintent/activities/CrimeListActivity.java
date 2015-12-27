package com.bromancelabs.criminalintent.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.criminalintent.fragments.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
