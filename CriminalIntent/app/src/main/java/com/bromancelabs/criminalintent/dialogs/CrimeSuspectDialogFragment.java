package com.bromancelabs.criminalintent.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bromancelabs.criminalintent.R;
import com.bromancelabs.criminalintent.utils.PictureUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CrimeSuspectDialogFragment extends DialogFragment {

    @Bind(R.id.iv_suspect_photo) ImageView mSuspectImageView;

    private static final String ARG_CRIME_SUSPECT_IMAGE = "crime_suspect_image";

    public static CrimeSuspectDialogFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_SUSPECT_IMAGE, file);
        CrimeSuspectDialogFragment fragment = new CrimeSuspectDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File photoFile = (File) getArguments().getSerializable(ARG_CRIME_SUSPECT_IMAGE);

        Bitmap image = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);

        ButterKnife.bind(this, view);

        mSuspectImageView.setImageBitmap(image);

        return new AlertDialog.Builder(getActivity())
                .setView(mSuspectImageView)
                .create();
    }
}
