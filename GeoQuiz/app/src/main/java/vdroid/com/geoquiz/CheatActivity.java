package vdroid.com.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity
{
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra("aws_is_true", false);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);

        mShowAnswerButton.setOnClickListener(
                new  View.OnClickListener()
                {
                     @Override
                     public void onClick(View v)
                     {
                         if (mAnswerIsTrue)
                         {
                             mAnswerTextView.setText(R.string.true_btn);
                         }
                         else
                         {
                             mAnswerTextView.setText(R.string.false_btn);
                         }

                         Intent data = new Intent();
                         data.putExtra("extra_ansewr_shown",mAnswerIsTrue);
                         setResult(RESULT_OK, data);

                         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                         {
                             int cx = mShowAnswerButton.getWidth() / 2;
                             int cy = mShowAnswerButton.getHeight() / 2;
                             float radius = mShowAnswerButton.getWidth();
                             Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);

                             anim.addListener(

                                 new AnimatorListenerAdapter()
                                 {
                                      @Override
                                      public void onAnimationEnd(Animator animation)
                                      {
                                          super.onAnimationEnd(animation);
                                          mShowAnswerButton.setVisibility(View.INVISIBLE);
                                      }
                                 }
                             );

                             anim.start();

                         }
                         else
                         {
                             mShowAnswerButton.setVisibility(View.INVISIBLE);
                         }
                     }
                }
        );

    }
}
