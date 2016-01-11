package com.bromancelabs.photogallery.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.bromancelabs.photogallery.fragments.PhotoPageFragment;

public class PhotoPageActivity extends SingleFragmentActivity {
    private PhotoPageFragment mPhotoPageFragment;

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mPhotoPageFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mPhotoPageFragment;
    }

    @Override
    public void onBackPressed() {
        if (mPhotoPageFragment.canWebViewGoBack()) {
            mPhotoPageFragment.webViewGoBack();
        } else {
            super.onBackPressed();
        }
    }
}
