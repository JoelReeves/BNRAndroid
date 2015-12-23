package com.bromancelabs.geoquiz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.bromancelabs.geoquiz.R;
import com.bromancelabs.geoquiz.models.Question;
import com.bromancelabs.geoquiz.utils.SnackBarUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = QuizActivity.class.getSimpleName();
    private static final String KEY_INDEX = "index";
    private static final String CHEATER = "cheater";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int LAST_QUESTION_INDEX = 2;

    @Bind(R.id.tv_question) TextView mQuestionTextView;
    @Bind(R.id.btn_next) Button mNextButton;

    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d(TAG, "onCreate(Bundle) called");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(CHEATER, false);
        }

        ButterKnife.bind(this);

        updateQuestion();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBoolean(CHEATER, mIsCheater);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @OnClick(R.id.btn_cheat)
    public void cheatButtonClicked() {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        startActivityForResult(CheatActivity.newIntent(this, answerIsTrue), REQUEST_CODE_CHEAT);
    }

    @OnClick(R.id.btn_next)
    public void nextButtonClicked() {
        mNextButton.setEnabled(mCurrentIndex != mQuestionBank.length - LAST_QUESTION_INDEX);
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        mIsCheater = false;
        updateQuestion();
    }

    @OnClick(R.id.btn_true)
    public void trueButtonClicked() {
        checkAnswer(true);
    }

    @OnClick(R.id.btn_false)
    public void falseButtonClicked() {
        checkAnswer(false);
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;
        int snackBarBackgroundColor;

        if (mIsCheater) {
            messageResId = R.string.judgment_snackbar;
            snackBarBackgroundColor = R.color.red;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_snackbar;
                snackBarBackgroundColor = R.color.green;
            } else {
                messageResId = R.string.incorrect_snackbar;
                snackBarBackgroundColor = R.color.red;
            }
        }

        SnackBarUtils.showSnackBar(this, messageResId, R.color.white, snackBarBackgroundColor);
    }
}
