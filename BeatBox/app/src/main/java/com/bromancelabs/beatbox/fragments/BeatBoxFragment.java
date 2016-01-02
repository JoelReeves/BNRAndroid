package com.bromancelabs.beatbox.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bromancelabs.beatbox.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BeatBoxFragment extends Fragment {
    @Bind(R.id.rv_fragment_beat_box) RecyclerView mRecyclerView;

    private static final int GRID_COLUMNS = 3;

    public BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_beat_box, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID_COLUMNS));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
