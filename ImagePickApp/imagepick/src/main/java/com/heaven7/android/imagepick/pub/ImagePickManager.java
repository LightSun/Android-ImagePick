package com.heaven7.android.imagepick.pub;

import com.heaven7.android.imagepick.ImagePickDelegateImpl;

/**
 * the image pick manager
 * @author heaven7
 */
public final class ImagePickManager {

    private static ImagePickManager sInstance;
    private ImagePickDelegate mDelegate = ImagePickDelegateImpl.getDefault();

    public static ImagePickManager get(){
        if(sInstance == null){
            sInstance = new ImagePickManager();
        }
        return sInstance;
    }
    public ImagePickDelegate getImagePickDelegate(){
        return mDelegate;
    }
}
