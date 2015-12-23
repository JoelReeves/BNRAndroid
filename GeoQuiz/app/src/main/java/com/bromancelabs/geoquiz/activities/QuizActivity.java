package com.bromancelabs.geoquiz.activities;

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

    @Bind(R.id.btn_true) Button mTrueButton;
    @Bind(R.id.btn_false) Button mFalseButton;
    @Bind(R.id.btn_next) Button mNextButton;
    @Bind(R.id.tv_question) TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d(TAG, "onCreate(Bundle) called");

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

    @OnClick(R.id.btn_next)
    public void nextButtonClicked() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
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
        int messageResId = 0;
        int snackBarBackgroundColor = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_snackbar;
            snackBarBackgroundColor = R.color.green;
        } else {
            messageResId = R.string.incorrect_snackbar;
            snackBarBackgroundColor = R.color.red;
        }

        SnackBarUtils.showSnackBar(this, messageResId, R.color.white, snackBarBackgroundColor);
    }
}
