package com.vdroid.criminalintent;

/**
 * Created by vuongpv on 3/8/18.
 */

import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new CrimeListFragment();
    }

}
