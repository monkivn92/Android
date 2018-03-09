package vdroid.com.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity
{
    private Button mTrueBtn, mFalseBtn;

    private Button mNextButton;
    private TextView mQuestionTextView;
    private ImageButton mBackBtn;
    private Button mCheatBtn;

    private Question[] mQuestionBank = new Question[]{

        new Question(R.string.question_australia,  true),
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast,  false),
        new Question(R.string.question_africa,    false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true),

    };

    private int mCurrentIndex = 0;

    private static final String KEY_INDEX = "index";
    private boolean mIsCheater;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null)
        {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
        }

        mTrueBtn = (Button) findViewById(R.id.true_btn);
        mFalseBtn = (Button) findViewById(R.id.false_btn);

        mNextButton = (Button) findViewById(R.id.next_btn);
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);

        mCheatBtn = (Button) findViewById(R.id.cheat_btn);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getmTextResId());



        mFalseBtn.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    checkAnswer(false);
                    //Toast.makeText(QuizActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT).show();
                }
            }
        );

        mTrueBtn.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    checkAnswer(true);
                    //Toast.makeText(QuizActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT).show();
                }
            }
        );

        mNextButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(mCurrentIndex==(mQuestionBank.length-1))
                        {
                            mNextButton.setEnabled(false);
                        }
                        else
                        {
                            mBackBtn.setEnabled(true);

                            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                            mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getmTextResId());
                        }




                    }
                }
        );

        mBackBtn.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(mCurrentIndex==0)
                        {
                            mBackBtn.setEnabled(false);
                        }
                        else
                        {
                            mNextButton.setEnabled(true);

                            mCurrentIndex = (mCurrentIndex > 0 ? mCurrentIndex-1 : mQuestionBank.length - 1 ) % mQuestionBank.length;
                            mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getmTextResId());
                        }



                    }
                }
        );

        mCheatBtn.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent cheat_intent = new Intent(QuizActivity.this, CheatActivity.class);
                        cheat_intent.putExtra("aws_is_true", mQuestionBank[mCurrentIndex].getmAnswerTrue());
                        //startActivity(cheat_intent);
                        startActivityForResult(cheat_intent, 0503);
                    }
                }
        );


    }

    private void checkAnswer(boolean userPressedTrue)
    {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].getmAnswerTrue();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue)
        {
            messageResId = R.string.correct_toast;
        }
        else
        {
            messageResId = R.string.incorrect_toast;
        }

        Toast toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);

        toast.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(KEY_INDEX,  mCurrentIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 0503)
        {
            if (data == null)
            {
                return;
            }
            mIsCheater =  data.getBooleanExtra("extra_answer_shown", false);
        }
    }



}
