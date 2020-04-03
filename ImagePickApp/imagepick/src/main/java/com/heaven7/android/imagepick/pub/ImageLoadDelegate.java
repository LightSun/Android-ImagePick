package com.heaven7.android.imagepick.pub;

import android.app.Activity;
import android.widget.ImageView;

public interface ImageLoadDelegate {

    void loadImage(ImageView iv, IImageItem item, ImageOptions options);

    void pauseRequests(Activity activity);

    void resumeRequests(Activity activity);
}
