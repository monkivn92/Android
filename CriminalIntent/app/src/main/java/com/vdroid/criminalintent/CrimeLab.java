package com.vdroid.criminalintent;

/**
 * Created by vuongpv on 3/8/18.
 */
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab
{
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context)
    {
        if (sCrimeLab == null)
        {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context)
    {
        mCrimes = new ArrayList<>();

        for (int i = 0; i < 10; i++)
        {
            Crime crime = new Crime();
            crime.setmText("Crime #" + i);
            crime.setmSolved(i % 2 == 0); // Every other one

            mCrimes.add(crime);
        }
    }

    public List<Crime> getCrimes()
    {
        return mCrimes;
    }

    public Crime getCrime(UUID id)
    {
        for (Crime crime : mCrimes)
        {
            if (crime.getmId().equals(id))
            {
                return crime;
            }
        }
        return null;
    }

    public void addCrime(Crime c)
    {
        mCrimes.add(c);
    }


}
