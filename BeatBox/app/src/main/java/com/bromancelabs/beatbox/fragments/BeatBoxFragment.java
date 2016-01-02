package com.bromancelabs.beatbox.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bromancelabs.beatbox.R;
import com.bromancelabs.beatbox.utils.BeatBox;
import com.bromancelabs.beatbox.utils.Sound;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BeatBoxFragment extends Fragment {
    @Bind(R.id.rv_fragment_beat_box) RecyclerView mRecyclerView;

    private static final int GRID_COLUMNS = 3;

    private BeatBox mBeatBox;

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBeatBox = new BeatBox(getActivity());
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
        mRecyclerView.setAdapter(new SoundAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SoundHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(SoundHolder soundHolder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    public class SoundHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.btn_list_item_sound) Button mButton;
        private Sound mSound;

        public SoundHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.list_item_sound_button, container, false));
            ButterKnife.bind(this, itemView);
        }

        public void bindSound(Sound sound) {
            mSound = sound;
            mButton.setText(mSound.getName());
        }
    }
}
