package com.bromancelabs.criminalintent.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.criminalintent.R;
import com.bromancelabs.criminalintent.fragments.CrimeFragment;
import com.bromancelabs.criminalintent.fragments.CrimeListFragment;
import com.bromancelabs.criminalintent.models.Crime;

public class CrimeListActivity extends SingleFragmentActivity implements
        CrimeListFragment.Callbacks,
        CrimeFragment.Callbacks {

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
        if (findViewById(R.id.detail_fragment_container) == null) {
            startActivity(CrimePagerActivity.newIntent(this, crime.getId()));
        } else {
            Fragment fragment = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        Fragment fragment = CrimeFragment.newInstance(crime.getId());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, fragment)
                .commit();
    }

    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
