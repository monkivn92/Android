package vdroid.com.geoquiz;

/**
 * Created by vuongpv on 3/6/18.
 */

public class Question
{
    private int mTextResId;


    private Boolean mAnswerTrue;

    public Question(int textResId, boolean answerTrue)
    {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
    }

    public int getmTextResId()
    {
        return mTextResId;
    }

    public void setmTextResId(int mTextResId)
    {
        this.mTextResId = mTextResId;
    }

    public Boolean getmAnswerTrue()
    {
        return mAnswerTrue;
    }

    public void setmAnswerTrue(Boolean mAnswerTrue)
    {
        this.mAnswerTrue = mAnswerTrue;
    }



}
