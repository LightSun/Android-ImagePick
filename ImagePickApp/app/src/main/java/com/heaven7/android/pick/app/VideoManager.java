package com.heaven7.android.pick.app;

import android.content.Context;
import android.view.View;

import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.core.util.Logger;

import java.lang.ref.WeakReference;

import lib.vida.video.TextureVideoView;

/**
 */
public class VideoManager implements VideoManageDelegate {

    private static final String TAG = "VideoManager";
    private WeakReference<TextureVideoView> mWeakView;

    @Override
    public boolean isVideoView(View view, IImageItem data) {
        return view instanceof TextureVideoView;
    }

    @Override
    public View createVideoView(Context context, IImageItem data) {
        return new TextureVideoView(context);
    }

    @Override
    public void setMediaData(Context context, View v, IImageItem data) {
        Logger.d(TAG, "setMediaData: " + data.getFilePath());
        TextureVideoView view = (TextureVideoView) v;
        view.setVideoURI(FileProviderHelper.getUriForFile(context, data.getFilePath()));
    }

    @Override
    public void pauseVideo(Context context, View videoView) {
        Logger.d(TAG, "pauseVideo");
        TextureVideoView view = (TextureVideoView) videoView;
        view.pause();
    }

    @Override
    public void resumeVideo(Context context, View videoView) {
        Logger.d(TAG, "resumeVideo");
        TextureVideoView view = (TextureVideoView) videoView;
        view.resume();
    }

    @Override
    public void destroyVideo(Context context, View videoView) {
        Logger.d(TAG, "destroyVideo");
        TextureVideoView view = (TextureVideoView) videoView;
        view.stop();
    }

    @Override
    public void startPlay(Context context, View v, IImageItem data) {
        Logger.d(TAG, "startPlay: " + data.getFilePath());
        TextureVideoView view = (TextureVideoView) v;
        //view.setVideoURI(FileProviderHelper.getUriForFile(context, data.getFilePath()));
        pauseLast(view);
        mWeakView = new WeakReference<>(view);
        view.start();
    }

    private void pauseLast(TextureVideoView next){
        if(mWeakView != null){
            TextureVideoView view = mWeakView.get();
            if(view != null && view != next){
                view.stop();
            }
        }
    }
}
