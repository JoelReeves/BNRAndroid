package com.tonicdesign.draganddraw.activities;

import android.support.v4.app.Fragment;

import com.tonicdesign.draganddraw.fragments.DragAndDrawFragment;

public class DragAndDrawActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}
