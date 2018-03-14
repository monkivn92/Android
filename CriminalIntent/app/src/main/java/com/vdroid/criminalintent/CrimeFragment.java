package com.vdroid.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by vuongpv on 3/8/18.
 */

public class CrimeFragment extends Fragment
{
    private Crime mCrime;
    private List<Crime> mCrimes;

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    private Button JumpToLastBtn;
    private Button JumpToFirstBtn;

    private ViewPager mViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        UUID crimeId = (UUID) getArguments().getSerializable("crime_id");

        mCrime = CrimeLab.get( getActivity() ).getCrime(crimeId);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        //Log.d("crime_fragment", mCrime.getmText());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v  =  inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);

        JumpToLastBtn = (Button)v.findViewById(R.id.jtlast_btn);
        JumpToFirstBtn = (Button)v.findViewById(R.id.jtfirst_btn);

        mViewPager = (ViewPager) getActivity().findViewById(R.id.crime_view_pager);

         if(mCrime.getmId() == mCrimes.get(0).getmId())
        {
            JumpToFirstBtn.setEnabled(false);
            JumpToLastBtn.setEnabled(true);
        }
        else if(mCrime.getmId() == mCrimes.get( mCrimes.size() -1 ).getmId())
        {
            JumpToFirstBtn.setEnabled(true);
            JumpToLastBtn.setEnabled(false);
        }
        else
        {
            JumpToFirstBtn.setEnabled(true);
            JumpToLastBtn.setEnabled(true);
        }

        mTitleField.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    {

                    }

                    @Override
                    public void afterTextChanged(Editable editable)
                    {

                    }
                }
        );

        mDateButton.setText(mCrime.getmDate().toString());
        //mDateButton.setEnabled(false);

        mTitleField.setText(mCrime.getmText());

        mSolvedCheckBox.setChecked(mCrime.getmSolved());

        mSolvedCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,  boolean isChecked)
                    {
                        mCrime.setmSolved(isChecked);
                    }

                }
        );

        JumpToFirstBtn.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        scrollToItem(0);
                    }
                }
        );

        JumpToLastBtn.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        scrollToItem(mCrimes.size() - 1);
                    }
                }
        );

        mDateButton.setOnClickListener(

                new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view)
                    {
                        FragmentManager frg = getFragmentManager();

                        DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getmDate());

                        dialog.setTargetFragment(CrimeFragment.this, 888);

                        dialog.show(frg, "DialogDate");
                    }
                }

        );



        return v;
    }


    public static CrimeFragment newInstance(UUID crimeId)
    {
        Bundle args = new Bundle();

        args.putSerializable("crime_id", crimeId);

        CrimeFragment fragment = new CrimeFragment();

        fragment.setArguments(args);

        return fragment;

    }

    private void scrollToItem(int pos)
    {
        if(pos >= 0 && pos < mCrimes.size())
        {
            mViewPager.setCurrentItem(pos, true);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }
        if (requestCode == 888)
        {
            Date date = (Date) data.getSerializableExtra("sent_date");
            mCrime.setmDate(date);
            mDateButton.setText(mCrime.getmDate().toString());
        }
    }


}
