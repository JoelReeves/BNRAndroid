package com.bromancelabs.photogallery.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class CustomGridLayoutManager extends GridLayoutManager {
    private int mMinItemWidth;

    public CustomGridLayoutManager(Context context, int minItemWidth) {
        super(context, 1);
        mMinItemWidth = minItemWidth;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mMinItemWidth > 0) {
            int spanCount = getWidth() / mMinItemWidth;

            if (spanCount < 1) {
                spanCount = 1;
            }
            setSpanCount(spanCount);
        }
        super.onLayoutChildren(recycler, state);
    }
}
