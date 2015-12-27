package com.bromancelabs.criminalintent.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.criminalintent.fragments.CrimeFragment;

public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
