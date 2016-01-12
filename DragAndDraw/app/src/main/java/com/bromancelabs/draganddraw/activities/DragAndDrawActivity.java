package com.bromancelabs.draganddraw.activities;

import android.support.v4.app.Fragment;

import com.bromancelabs.draganddraw.fragments.DragAndDrawFragment;

public class DragAndDrawActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}
