package com.bromancelabs.criminalintent.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.criminalintent.R;
import com.bromancelabs.criminalintent.fragments.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }
}
