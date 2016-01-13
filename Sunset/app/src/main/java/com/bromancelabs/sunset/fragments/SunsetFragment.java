package com.bromancelabs.sunset.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.bromancelabs.sunset.R;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SunsetFragment extends Fragment {
    private static final long ANIMATION_DURATION = 3000;
    private static final long TRANSITION_ANIMATION_DURATION = 1500;

    @Bind(R.id.iv_sun) View mSunView;
    @Bind(R.id.fl_sky) View mSkyView;
    @BindColor(R.color.blue_sky) int mBlueSkyColor;
    @BindColor(R.color.sunset_sky) int mSunsetSkyColor;
    @BindColor(R.color.night_sky) int mNightSkyColor;

    private AnimatorSet mAnimatorSet;

    private boolean isSunRise = false;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sunset, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.ll_sunset)
    public void viewClicked() {
        if (!isSunRise) {
            startSunSetAnimation();
        } else {
            startSunRiseAnimation();
        }
    }

    private void startSunSetAnimation() {
        cancelAnimationSet();

        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet
                .play(createHeightAnimator(mSunView, sunYStart, sunYEnd, ANIMATION_DURATION))
                .with(createBackgroundColorAnimator(mSkyView, mBlueSkyColor, mSunsetSkyColor, ANIMATION_DURATION))
                .before(createBackgroundColorAnimator(mSkyView, mSunsetSkyColor, mNightSkyColor, TRANSITION_ANIMATION_DURATION));
        mAnimatorSet.start();

        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isSunRise = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void startSunRiseAnimation() {
        cancelAnimationSet();

        float sunYStart = mSkyView.getBottom();
        float startYEnd = mSunView.getHeight();

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet
                .play(createHeightAnimator(mSunView, sunYStart, startYEnd, ANIMATION_DURATION))
                .with(createBackgroundColorAnimator(mSkyView, mNightSkyColor, mSunsetSkyColor, ANIMATION_DURATION))
                .before(createBackgroundColorAnimator(mSkyView, mSunsetSkyColor, mBlueSkyColor, TRANSITION_ANIMATION_DURATION));
        mAnimatorSet.start();
    }

    private ObjectAnimator createHeightAnimator(View view, float start, float end, long duration) {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(view, "y", start, end)
                .setDuration(duration);

        heightAnimator.setInterpolator(new AccelerateInterpolator());
        return heightAnimator;
    }

    private ObjectAnimator createBackgroundColorAnimator(View view, int firstColor, int secondColor, long duration) {
        ObjectAnimator objectAnimator = ObjectAnimator
                .ofInt(view, "backgroundColor", firstColor, secondColor)
                .setDuration(duration);

        objectAnimator.setEvaluator(new ArgbEvaluator());
        return objectAnimator;
    }

    private void cancelAnimationSet() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }
}
