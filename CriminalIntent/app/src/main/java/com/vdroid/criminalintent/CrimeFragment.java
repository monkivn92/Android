package com.vdroid.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
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

    private Button mReportButton;

    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_CALL = 11;
    private Button mSuspectButton;
    private Button mShareCompatButton;
    private Button mCallSuspect;

    private String mtel = "";


    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private File mPhotoFile;

    private static final int REQUEST_PHOTO= 2;

    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks
    {
        void onCrimeUpdated(Crime crime);
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        UUID crimeId = (UUID) getArguments().getSerializable("crime_id");

        mCrime = CrimeLab.get( getActivity() ).getCrime(crimeId);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
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

        mReportButton = (Button)  v.findViewById(R.id.crime_report);
        mSuspectButton = (Button)  v.findViewById(R.id.crime_suspect);
        mShareCompatButton = (Button)  v.findViewById(R.id.share_compat_btn);

        mCallSuspect = (Button)  v.findViewById(R.id.call_suspect);

        mPhotoButton = (ImageButton)  v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView)  v.findViewById(R.id.crime_photo);

        if(mCrimes.size() == 0)
        {
            JumpToLastBtn.setVisibility(View.INVISIBLE);
            JumpToFirstBtn.setVisibility(View.INVISIBLE);
        }

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
                        mCrime.setmText(charSequence.toString());
                        updateCrime();
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
                        updateCrime();
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


        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton.setOnClickListener(

                new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view)
                    {
                        startActivityForResult(pickContact,  REQUEST_CONTACT);
                    }
                }

        );

        if (mCrime.getmSuspect() != null)
        {
            mSuspectButton.setText(mCrime.getmSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();

        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
        {
            mSuspectButton.setEnabled(false);
        }



        mReportButton.setOnClickListener(

            new View.OnClickListener()
            {

                 public void onClick(View v)
                 {
                     Intent i = new Intent(Intent.ACTION_SEND);

                     i.setType("text/plain");

                     i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());

                     i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

                     i = Intent.createChooser(i, getString(R.string.send_report));//always show chooser

                     startActivity(i);
                 }
            }
        );

        mShareCompatButton.setOnClickListener(

            new View.OnClickListener()
            {

                 public void onClick(View v)
                 {
                     Intent shareIntent =  ShareCompat.IntentBuilder.from(getActivity())
                                                    .setChooserTitle("ChooserTitle from sharecompat")
                                                    .setType("text/plain")
                                                    .setSubject("This is a subject from sharecompat")
                                                    .setText("This is text from sharecompat")
                                                    .getIntent();
                     if(shareIntent.resolveActivity(getActivity().getPackageManager()) != null)
                     {
                         startActivity(shareIntent);
                     }
                 }
            }
        );


        mCallSuspect.setOnClickListener(

                new View.OnClickListener()
                {

                    public void onClick(View v)
                    {
                        if(mtel != "")
                        {
                            Intent i = new Intent(Intent.ACTION_CALL);
                            i.setData(Uri.parse("tel:" + mtel));
                            startActivity(i);
                        }

                    }
                }
        );

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Uri uri = FileProvider.getUriForFile(getActivity(),
                                "com.vdroid.criminalintent.fileprovider",
                                mPhotoFile);
                        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                        List<ResolveInfo> cameraActivities = getActivity()
                                .getPackageManager().queryIntentActivities(captureImage,
                                        PackageManager.MATCH_DEFAULT_ONLY);

                        for (ResolveInfo activity : cameraActivities)
                        {
                            getActivity().grantUriPermission(activity.activityInfo.packageName,
                                    uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }

                        startActivityForResult(captureImage, REQUEST_PHOTO);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        if (requestCode == 888)
        {

            Date date = (Date) data.getSerializableExtra("sent_date");
            mCrime.setmDate(date);
            mDateButton.setText(mCrime.getmDate().toString());
            updateCrime();
        }
        else if (requestCode == REQUEST_CONTACT && data != null)
        {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for
            String[] queryFields = new String[]
            { ContactsContract.Contacts.DISPLAY_NAME ,
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER };

            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                                .query(contactUri, queryFields, null, null, null);
            try
            {
                // Double-check that you actually got results
                if (c.getCount() == 0)
                {
                    return;
                }

                // Pull out the first column of the first   row of data -
                // that is your suspect's name

                c.moveToFirst();

                String suspect = c.getString(0);

                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1"))
                {
                    Cursor phones = getActivity().getContentResolver().query(

                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,

                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,

                            null, null);

                    phones.moveToFirst();

                    mtel = phones.getString(phones.getColumnIndex("data1"));
                    //Log.e("vvvvvv", mtel);
                }

                mCrime.setmSuspect(suspect);

                mSuspectButton.setText(suspect);

            }
            finally
            {

                c.close();

            }

        }
        else if (requestCode == REQUEST_PHOTO)
        {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.vdroid.criminalintent.fileprovider",
                    mPhotoFile);

            //Log.e("vvvvvv", uri.getPath());

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }


    private String getCrimeReport()
    {
        String solvedString = null;

        if (mCrime.getmSolved())
        {
            solvedString = getString(R.string.crime_report_solved);
        }
        else
        {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getmDate()).toString();

        String suspect = mCrime.getmSuspect();

        if (suspect == null)
        {
            suspect =
                    getString(R.string.crime_report_no_suspect);
        }
        else
        {
            suspect =
                    getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                        mCrime.getmText(), dateString,
                        solvedString, suspect);
        return report;
    }

    private void updatePhotoView()
    {
        if (mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null);
        }
        else
        {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateCrime()
    {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

}
