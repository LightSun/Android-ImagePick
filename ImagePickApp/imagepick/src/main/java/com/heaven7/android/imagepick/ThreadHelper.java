package com.heaven7.android.imagepick;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

/*public*/ final class ThreadHelper {

    private Handler mBackgroundHandler;

    public Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }
    public void quit(boolean rightNow){
        if (mBackgroundHandler != null) {
            if(rightNow || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
                mBackgroundHandler.getLooper().quit();
            }else {
                mBackgroundHandler.getLooper().quitSafely();
            }
            mBackgroundHandler = null;
        }
    }
    public void quitNow(){
        quit(true);
    }
}
