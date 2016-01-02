package com.bromancelabs.criminalintent.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bromancelabs.criminalintent.R;
import com.bromancelabs.criminalintent.dialogs.CrimeSuspectDialogFragment;
import com.bromancelabs.criminalintent.dialogs.DatePickerFragment;
import com.bromancelabs.criminalintent.models.Crime;
import com.bromancelabs.criminalintent.models.CrimeLab;
import com.bromancelabs.criminalintent.utils.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrimeFragment extends Fragment {
    @Bind(R.id.et_crime_title) EditText mTitleEditText;
    @Bind(R.id.btn_crime_date) Button mDateButton;
    @Bind(R.id.chk_crime_solved) CheckBox mSolvedCheckbox;
    @Bind(R.id.btn_crime_suspect) Button mSuspectButton;
    @Bind(R.id.ib_crime_camera) ImageButton mPhotoButton;
    @Bind(R.id.iv_crime_photo) ImageView mPhotoView;

    private static final String DATE_FORMAT = "EEE, MMM dd";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "dialog_date";
    private static final String DIALOG_PHOTO = "dialog_photo";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    private Crime mCrime;

    private Intent mPickContact;
    private Intent mCaptureImage;

    private File mPhotoFile;

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted(Crime crime);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
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

        PackageManager packageManager = getActivity().getPackageManager();

        mPickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mTitleEditText.setText(mCrime.getTitle());
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
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
                updateCrime();
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        if (packageManager.resolveActivity(mPickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        boolean canTakePhoto = mPhotoFile != null && mCaptureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            mCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("Crime", "ImageView dimensions: " + mPhotoView.getWidth() + " x " + mPhotoView.getHeight());
                updatePhotoView();
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
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
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                updateCrime();
                break;

            case REQUEST_CONTACT:
                if (data != null) {
                    Uri contactUri = data.getData();

                    // Specify which fields you want your query to return values for.
                    String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};

                    // Perform your query - the contactUri is like a "where" clause here
                    Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

                    if (cursor != null && cursor.getCount() > 0) {
                        try {
                            // Pull out the first column of the first row of data - that is your suspect's name.
                            cursor.moveToFirst();
                            String suspect = cursor.getString(0);
                            mCrime.setSuspect(suspect);
                            updateCrime();
                            mSuspectButton.setText(suspect);
                        } finally {
                            cursor.close();
                        }
                    }
                }

            case REQUEST_PHOTO:
                updatePhotoView();
                updateCrime();
                break;
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

                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    getActivity().finish();
                } else {
                    updateCrime();
                    List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();

                    if (!crimes.isEmpty()) {
                        mCallbacks.onCrimeDeleted(crimes.get(0));
                    } else{
                        getActivity().findViewById(R.id.ll_fragment_crime).setVisibility(View.GONE);
                    }
                }

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

    @OnClick(R.id.btn_crime_report)
    public void sendReportButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
        intent = Intent.createChooser(intent, getString(R.string.send_report));
        startActivity(intent);
    }

    @OnClick(R.id.ib_crime_camera)
    public void photoButtonClicked() {
        startActivityForResult(mCaptureImage, REQUEST_PHOTO);
    }

    @OnClick(R.id.iv_crime_photo)
    public void photoImageClicked() {
        CrimeSuspectDialogFragment dialog = CrimeSuspectDialogFragment.newInstance(mPhotoFile);
        dialog.show(getFragmentManager(), DIALOG_PHOTO);
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

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setEnabled(false);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoView.getWidth(), mPhotoView.getHeight());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setEnabled(true);
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
}
