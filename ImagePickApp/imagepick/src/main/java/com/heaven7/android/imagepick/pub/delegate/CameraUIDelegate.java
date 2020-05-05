package com.heaven7.android.imagepick.pub.delegate;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.google.android.cameraview.CameraView;
import com.heaven7.android.imagepick.pub.module.CameraParameter;

/**
 * the camera ui delegate
 * @since 2.0.0
 */
public abstract class CameraUIDelegate {

    private Provider provider;

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    public Provider getProvider() {
        return provider;
    }
    public Activity getActivity(){
        return getProvider().getActivity();
    }
    public Resources getResources(){
        return getProvider().getActivity().getResources();
    }
    public CameraParameter getParameter(){
        return getProvider().getParameter();
    }

    public abstract int getLayoutId();
    /**
     * called on initialize
     * @param owner the life owner
     * @param view the root view
     * @param intent the intent
     */
    public abstract void initialize(LifecycleOwner owner, View view, Intent intent);
    /**
     * set camera enabled or not
     * @param enabled true if enabled
     */
    public abstract void setCameraEnabled(boolean enabled);
    /**
     * called after processing image and want to display it.
     * @param path the image path
     */
    public abstract void applyImageFile(String path);

    /**
     * called on reach max image count
     * @param reach true if reach
     */
    public void setReachMax(boolean reach){

    }
    /**
     * called when no images. often called by onResume.
     */
    public void onEmptyImage() {

    }

    public interface Provider{
        Activity getActivity();
        CameraParameter getParameter();
        CameraView getCameraView();
        boolean isImageProcessing();
        void finishCamera();
    }
}
