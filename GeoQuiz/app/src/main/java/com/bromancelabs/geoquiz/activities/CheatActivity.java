package com.bromancelabs.geoquiz.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.bromancelabs.geoquiz.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bromancelabs.geoquiz.activities.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bromancelabs.geoquiz.activities.answer_shown";
    private static final String CHEAT_TEXT = "cheat_text";

    @Bind(R.id.tv_answer) TextView mAnswerTextView;

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

        if (savedInstanceState != null) {
            mAnswerTextView.setText(savedInstanceState.getString(CHEAT_TEXT, ""));
        }

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final String cheatText = mAnswerTextView.getText().toString();

        if (!TextUtils.isEmpty(cheatText)) {
            outState.putString(CHEAT_TEXT, cheatText);
        }
    }

    @OnClick(R.id.btn_showAnswer)
    public void showAnswerClicked() {
        mAnswerTextView.setText(mAnswerIsTrue ? R.string.true_button : R.string.false_button);
        setAnswerShownResult(true);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, intent);
    }
}
