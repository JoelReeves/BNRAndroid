package com.bromancelabs.geoquiz.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.bromancelabs.geoquiz.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bromancelabs.geoquiz.activities.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bromancelabs.geoquiz.activities.answer_shown";

    @Bind(R.id.tv_answer) TextView mAnswerTextView;
    @Bind(R.id.btn_showAnswer) Button mShowAnswer;

    private boolean mAnswerIsTrue;

    public static Intent newIntent(Context context, boolean answerIsTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        ButterKnife.bind(this);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
    }

    @OnClick(R.id.btn_showAnswer)
    public void showAnswerClicked() {
        mAnswerTextView.setText(mAnswerIsTrue ? R.string.true_button : R.string.false_button);
        setAnswerShownResult(true);

        int cx = mShowAnswer.getWidth() / 2;
        int cy = mShowAnswer.getHeight() / 2;
        float radius = mShowAnswer.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswer, cx, cy, radius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnswerTextView.setVisibility(View.VISIBLE);
                mShowAnswer.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, intent);
    }
}
