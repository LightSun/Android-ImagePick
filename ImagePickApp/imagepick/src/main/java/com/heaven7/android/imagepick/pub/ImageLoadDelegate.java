package com.heaven7.android.imagepick.pub;

import android.app.Activity;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;

import com.heaven7.android.imagepick.pub.module.IImageItem;
import com.heaven7.android.imagepick.pub.module.ImageOptions;

/**
 * the image load delegate
 */
public interface ImageLoadDelegate {

    /**
     * load image for png,jpg,gif,video
     * @param owner the owner
     * @param iv the image view
     * @param item the image item
     * @param options the image option.
     */
    void loadImage(LifecycleOwner owner, ImageView iv, IImageItem item, ImageOptions options);

    /**
     * pause requests
     * @param activity the activity
     */
    void pauseRequests(Activity activity);

    /**
     * resume requests
     * @param activity  the activity
     */
    void resumeRequests(Activity activity);
}
