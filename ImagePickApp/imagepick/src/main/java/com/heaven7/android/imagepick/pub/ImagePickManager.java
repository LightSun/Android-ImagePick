package com.heaven7.android.imagepick.pub;

import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;

/**
 * the image pick manager
 * @author heaven7
 */
public final class ImagePickManager {

    private static ImagePickManager sInstance;

    private final ImagePickDelegate mDelegate;

    private ImagePickManager(ImagePickDelegate mDelegate) {
        this.mDelegate = mDelegate;
    }

    public static synchronized ImagePickManager get(){
        if(sInstance == null){
            sInstance = new ImagePickManager(ImagePickDelegateImpl.getDefault());
        }
        return sInstance;
    }

    /**
     * get the image pick delegate
     * @return the image delegate
     */
    public ImagePickDelegate getImagePickDelegate(){
        return mDelegate;
    }
}
