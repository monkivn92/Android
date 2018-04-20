package com.vdroid.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PhotoPageActivity extends SingleFragmentActivity
{
    public static Intent newIntent(Context context, Uri photoPageUri)
    {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment()
    {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }
}
