package com.bromancelabs.geoquiz.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.bromancelabs.geoquiz.R;
import com.bromancelabs.geoquiz.utils.SnackBarUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizActivity extends AppCompatActivity {

    @Bind(R.id.btn_true) Button mTrueButton;
    @Bind(R.id.btn_false) Button mFalseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_true)
    public void trueButtonClicked() {
        SnackBarUtils.getSnackBar(this, R.string.incorrect_snackbar, R.color.white, R.color.red);
    }

    @OnClick(R.id.btn_false)
    public void falseButtonClicked() {
        SnackBarUtils.getSnackBar(this, R.string.correct_snackbar, R.color.white, R.color.green);
    }
}
