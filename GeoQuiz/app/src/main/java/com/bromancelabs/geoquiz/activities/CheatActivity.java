package com.bromancelabs.geoquiz.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bromancelabs.geoquiz.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bromancelabs.geoquiz.activities.answer_is_true";

    @Bind(R.id.tv_answer) TextView mAnswerTextView;

    private boolean mAnswerIsTrue;

    public static Intent newIntent(Context context, boolean answerIsTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
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
    }
}
