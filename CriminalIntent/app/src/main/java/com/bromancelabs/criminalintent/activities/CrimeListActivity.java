package com.bromancelabs.criminalintent.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.criminalintent.R;
import com.bromancelabs.criminalintent.fragments.CrimeFragment;
import com.bromancelabs.criminalintent.fragments.CrimeListFragment;
import com.bromancelabs.criminalintent.models.Crime;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        
    }
}
