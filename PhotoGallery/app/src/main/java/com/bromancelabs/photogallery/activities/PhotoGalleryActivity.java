package com.bromancelabs.photogallery.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.photogallery.fragments.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
