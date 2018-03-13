package com.vdroid.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by vuongpv on 3/8/18.
 */

public class CrimeFragment extends Fragment
{
    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        UUID crimeId = (UUID) getArguments().getSerializable("crime_id");

        mCrime = CrimeLab.get( getActivity() ).getCrime(crimeId);

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
        mDateButton.setEnabled(false);

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


}
