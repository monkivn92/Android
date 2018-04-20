package com.vdroid.photogallery;

import android.net.Uri;

public class GalleryItem
{
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;

    @Override
    public String toString()
    {
        return mCaption;
    }

    public String getCaption()
    {
        return mCaption;
    }

    public void setCaption(String mCaption)
    {
        this.mCaption = mCaption;
    }

    public String getId()
    {
        return mId;
    }

    public void setId(String mId)
    {
        this.mId = mId;
    }

    public String getUrl()
    {
        return mUrl;
    }

    public void setUrl(String mUrl)
    {
        this.mUrl = mUrl;
    }

    public String getOwner()
    {
        return mOwner;
    }
    public void setOwner(String owner)
    {

        mOwner = owner;
    }
    public Uri getPhotoPageUri()
    {
        return Uri.parse("https://www.flickr.com/photos/")
                        .buildUpon()
                        .appendPath(mOwner)
                        .appendPath(mId)
                        .build();
    }


}
