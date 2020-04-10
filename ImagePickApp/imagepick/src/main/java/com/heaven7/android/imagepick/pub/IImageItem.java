package com.heaven7.android.imagepick.pub;

import android.os.Parcelable;

import com.heaven7.adapter.ISelectable;

/**
 * std image item for big-image
 */
public interface IImageItem extends ISelectable, Parcelable {

    String getUrl();

    String getFilePath();

    boolean isVideo();

    boolean isGif();

    void setExtra(Parcelable extra);

    Parcelable getExtra();
}
