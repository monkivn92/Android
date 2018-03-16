package com.vdroid.criminalintent;

/**
 * Created by vuongpv on 3/8/18.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends  Fragment
{
    private RecyclerView mCrimeRecyclerView;

    private CrimeAdapter mAdapter;

    private int current_pos_clicked_item = 0;

    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean("subtitle");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        mCrimeRecyclerView.setLayoutManager(

                new LinearLayoutManager( getActivity() )

        );


        updateUI();

        return view;
    }

    private void updateUI()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null)
        {
            mAdapter = new CrimeAdapter(crimes);

            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.notifyItemChanged(current_pos_clicked_item);
        }

    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super( inflater.inflate(R.layout.list_item_crime, parent, false) );
            itemView.setOnClickListener(this); //itemView inharited from ViewHolder class

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getmText());
            mDateTextView.setText(mCrime.getmDate().toString());

            if(mCrime.getmSolved() != null)
            {
                mSolvedImageView.setVisibility(crime.getmSolved() ? View.VISIBLE : View.GONE);
            }

        }

        @Override
        public void onClick(View view)
        {
            //Toast.makeText(getActivity(), mCrime.getmText() + " clicked!", Toast.LENGTH_SHORT).show();
            current_pos_clicked_item = this.getAdapterPosition();
            //Log.d("POS", String.valueOf(current_pos_clicked_item));
            //Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getmId());
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getmId());
            startActivity(intent);
        }

    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>
    {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes)
        {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from( getActivity() );
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position)
        {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount()
        {
            return mCrimes.size();
        }
    } //end class

    @Override
    public void onResume()
    {
        super.onResume();
        updateUI();
        updateSubtitle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("subtitle", mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);

        if (mSubtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getmId());
                startActivity(intent);
                return true;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        int crimeCount = crimeLab.getCrimes().size();

        //String subtitle = getString(R.string.subtitle_format, crimeCount);

        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible)
        {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        activity.getSupportActionBar().setSubtitle(subtitle);
    }


}
