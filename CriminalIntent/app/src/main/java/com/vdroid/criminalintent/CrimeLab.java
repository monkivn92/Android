package com.vdroid.criminalintent;

/**
 * Created by vuongpv on 3/8/18.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.vdroid.criminalintent.database.*;


public class CrimeLab
{
    private static CrimeLab sCrimeLab;


    private Context mContext;
    private SQLiteDatabase mDatabase;

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
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();


    }

    public List<Crime> getCrimes()
    {
        return new ArrayList<>();
    }

    public Crime getCrime(UUID id)
    {

        return null;
    }

    public void updateCrime(Crime crime)
    {
        String uuidString = crime.getmId().toString();

        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                                                        new String[] { uuidString });
    }


    private static ContentValues getContentValues(Crime crime)
    {
        ContentValues values = new ContentValues();

        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getmId().toString());

        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getmText());

        values.put(CrimeDbSchema.CrimeTable.Cols.DATE,  crime.getmDate().getTime());

        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.getmSolved() ? 1 : 0);

        return values;
    }

    public void addCrime(Crime c)
    {
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }



}
