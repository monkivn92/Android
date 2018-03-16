package com.vdroid.criminalintent;

import java.util.Date;
import java.util.UUID;
import com.vdroid.criminalintent.database.*;
/**
 * Created by vuongpv on 3/8/18.
 */

public class Crime
{


    private UUID mId;
    private String mText;
    private Date mDate;
    private Boolean mSolved;

    public Crime()
    {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getmId()
    {
        return mId;
    }

    public String getmText()
    {
        return mText;
    }

    public void setmText(String mText)
    {
        this.mText = mText;
    }

    public Date getmDate()
    {
        return mDate;
    }

    public void setmDate(Date mDate)
    {
        this.mDate = mDate;
    }

    public Boolean getmSolved()
    {
        return mSolved;
    }

    public void setmSolved(Boolean mSolved)
    {
        this.mSolved = mSolved;
    }


}
