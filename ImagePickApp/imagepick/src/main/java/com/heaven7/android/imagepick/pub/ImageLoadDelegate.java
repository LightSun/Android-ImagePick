package com.heaven7.android.imagepick.pub;

import android.app.Activity;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;

import com.heaven7.android.imagepick.pub.module.IImageItem;
import com.heaven7.android.imagepick.pub.module.ImageOptions;

public interface ImageLoadDelegate {

    void loadImage(LifecycleOwner owner, ImageView iv, IImageItem item, ImageOptions options);

    void pauseRequests(Activity activity);

    void resumeRequests(Activity activity);
}
