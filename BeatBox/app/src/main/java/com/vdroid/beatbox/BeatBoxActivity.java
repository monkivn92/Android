package com.vdroid.beatbox;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BeatBoxActivity extends SingleFragmentActivity
{
    @Override
    protected BeatBoxFragment createFragment()
    {
        return BeatBoxFragment.newInstance();
    }
}
