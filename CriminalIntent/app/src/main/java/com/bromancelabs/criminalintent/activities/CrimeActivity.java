package com.bromancelabs.criminalintent.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.bromancelabs.criminalintent.fragments.CrimeFragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    public static final String EXTRA_CRIME_ID = "com.bromancelabs.criminalintent.activities.crime_id";

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
