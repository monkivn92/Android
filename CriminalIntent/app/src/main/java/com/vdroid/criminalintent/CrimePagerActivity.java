package com.vdroid.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks
{
    private ViewPager mViewPager;
    private List<Crime> mCrimes;



    public static Intent newIntent(Context packageContext, UUID crimeId)
    {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra("crime_id", crimeId);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra("crime_id");

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);


        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fragmentManager = getSupportFragmentManager();

        mViewPager.setAdapter(
                new FragmentPagerAdapter(fragmentManager)
                {
                    @Override
                    public Fragment getItem(int position)
                    {
                        Crime crime = mCrimes.get(position);
                        return CrimeFragment.newInstance(crime.getmId());
                    }

                    @Override
                    public int getCount()
                    {
                        return mCrimes.size();
                    }
                }
        );

        //mViewPager.setClipToPadding(false);
        mViewPager.setPadding(16, 0, 16, 0);


        for (int i = 0; i < mCrimes.size(); i++)
        {
            if(mCrimes.get(i).getmId().equals(crimeId))
            {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime)
    {
    }


}
