package com.bromancelabs.criminalintent.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.bromancelabs.criminalintent.R;
import com.bromancelabs.criminalintent.fragments.CrimeFragment;
import com.bromancelabs.criminalintent.models.Crime;
import com.bromancelabs.criminalintent.models.CrimeLab;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CrimePagerActivity extends AppCompatActivity {
    @Bind(R.id.vp_activity_crime_pager) ViewPager mViewPager;

    private List<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        ButterKnife.bind(this);

        mCrimes = CrimeLab.get(this).getCrimes();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
    }
}
