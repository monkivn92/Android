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



    private String mSuspect;

    public Crime(UUID id)
    {
        mId = id;
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
    public String getmSuspect()
    {
        return mSuspect;
    }

    public void setmSuspect(String mSuspect)
    {
        this.mSuspect = mSuspect;
    }

    public String getPhotoFilename()
    {
        return "IMG_" + getmId().toString() + ".jpg";
    }


}
