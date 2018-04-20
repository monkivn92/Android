package com.vdroid.transitionlikeios;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity
{
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        if (getIntent() != null)
        {
            mType = getIntent().getIntExtra("type", 0);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        switch (mType)
        {
            case 1:
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case 2:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }
}