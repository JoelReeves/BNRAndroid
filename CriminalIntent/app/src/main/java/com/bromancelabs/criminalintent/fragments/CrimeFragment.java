package com.bromancelabs.criminalintent.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.bromancelabs.criminalintent.R;
import com.bromancelabs.criminalintent.dialogs.DatePickerFragment;
import com.bromancelabs.criminalintent.models.Crime;
import com.bromancelabs.criminalintent.models.CrimeLab;

import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrimeFragment extends Fragment {
    @Bind(R.id.et_crime_title) EditText mTitleEditText;
    @Bind(R.id.btn_crime_date) Button mDateButton;
    @Bind(R.id.chk_crime_solved) CheckBox mSolvedCheckbox;
    @Bind(R.id.btn_crime_suspect) Button mSuspectButton;
    @Bind(R.id.btn_call_suspect) Button mCallSuspectButton;

    private static final String TAG = CrimeFragment.class.getSimpleName();
    private static final String DATE_FORMAT = "EEE, MMM dd";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "dialog_date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;

    private Intent mPickContact;

    private Uri mPhoneNumber;

    private Cursor mCursor;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crime, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mTitleEditText.setText(mCrime.getTitle());
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateDate();

        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallSuspectButton.setEnabled(mCrime.getSuspect() != null);

        if (getActivity().getPackageManager().resolveActivity(mPickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                break;

            case REQUEST_CONTACT:
                if (data != null) {
                    queryCrimeSuspect(data);
                    queryCrimeSuspectPhoneNumber();
                }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.btn_crime_date)
    public void dateButtonClicked() {
        DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
        dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        dialog.show(getFragmentManager(), DIALOG_DATE);
    }

    @OnClick(R.id.btn_crime_suspect)
    public void suspectButtonClicked() {
        startActivityForResult(mPickContact, REQUEST_CONTACT);
    }

    @OnClick(R.id.btn_call_suspect)
    public void callSuspectButtonClicked() {
        startActivity(new Intent(Intent.ACTION_DIAL, mPhoneNumber));
    }

    @OnClick(R.id.btn_crime_report)
    public void sendReportButtonClicked() {
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(getCrimeReport())
                .setSubject(getString(R.string.crime_report_subject))
                .setChooserTitle(getString(R.string.send_report))
                .startChooser();
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = mCrime.isSolved() ? getString(R.string.crime_report_solved) : getString(R.string.crime_report_unsolved);

        String dateString = DateFormat.format(DATE_FORMAT, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        suspect = suspect == null ? getString(R.string.crime_report_no_suspect) : getString(R.string.crime_report_suspect, suspect);

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private void queryCrimeSuspect(Intent intent) {
        Uri contactUri = intent.getData();

        // Specify which database columns you want your query to return values for.
        String[] columns = new String[] {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};

        // Perform your query - the contactUri is like a "where" clause here
        mCursor = getActivity().getContentResolver().query(contactUri, columns, null, null, null);

        if (mCursor != null && mCursor.getCount() > 0) {
            Log.d(TAG, "Cursor display name and ID: " + DatabaseUtils.dumpCursorToString(mCursor));
            try {
                mCursor.moveToFirst();
                String suspect = mCursor.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);

                long contactId = mCursor.getLong(1);
                mCrime.setContactId(contactId);
            } finally {
                mCursor.close();
            }
        }
    }

    private void queryCrimeSuspectPhoneNumber() {
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] fields = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selectClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectParams = {Long.toString(mCrime.getContactId())};

        mCursor = getActivity().getContentResolver().query(contentUri, fields, selectClause, selectParams, null);

        if (mCursor == null || mCursor.getCount() == 0) {
            mCallSuspectButton.setEnabled(false);
        } else {
            Log.d(TAG, "Cursor phone number: " + DatabaseUtils.dumpCursorToString(mCursor));
            try {
                mCursor.moveToFirst();
                String number = mCursor.getString(0);
                mPhoneNumber = Uri.parse("tel:" + number);

                mCallSuspectButton.setEnabled(true);
            }
            finally {
                mCursor.close();
            }
        }
    }
}
