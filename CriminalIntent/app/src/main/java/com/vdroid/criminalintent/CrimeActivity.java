package com.vdroid.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        UUID crimeId = (UUID) getIntent().getSerializableExtra("crime_id");
        return CrimeFragment.newInstance(crimeId);
    }

    public static Intent newIntent(Context packageContext, UUID crimeId)
    {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra("crime_id", crimeId);
        return intent;
    }

}
