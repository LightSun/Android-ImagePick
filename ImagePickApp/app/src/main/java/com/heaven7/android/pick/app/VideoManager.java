package com.heaven7.android.pick.app;

import android.content.Context;
import android.view.View;
import android.widget.VideoView;

import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.core.util.Logger;

import java.lang.ref.WeakReference;

/**
 * TODO 滑动过去又回来，需要处理。
 */
public class VideoManager implements VideoManageDelegate {

    private static final String TAG = "VideoManager";
    private WeakReference<VideoView> mWeakView;

    @Override
    public boolean isVideoView(View view) {
        return view instanceof VideoView;
    }

    @Override
    public View createVideoView(Context context, IImageItem data) {
        return new VideoView(context);
    }

    @Override
    public void setMediaData(Context context, View v, IImageItem data) {
        VideoView view = (VideoView) v;
        view.setVideoURI(FileProviderHelper.getUriForFile(context, data.getFilePath()));
    }

    @Override
    public void pauseVideo(Context context, View videoView) {
        Logger.d(TAG, "pauseVideo");
        VideoView view = (VideoView) videoView;
        view.pause();
    }

    @Override
    public void resumeVideo(Context context, View videoView) {
        Logger.d(TAG, "resumeVideo");
        VideoView view = (VideoView) videoView;
        view.resume();
    }

    @Override
    public void destroyVideo(Context context, View videoView) {
        Logger.d(TAG, "destroyVideo");
        VideoView view = (VideoView) videoView;
        view.stopPlayback();
    }

    @Override
    public void startPlay(Context context, View v, IImageItem data) {
        Logger.d(TAG, "startPlay");
        VideoView view = (VideoView) v;
        pauseLast(view);
        mWeakView = new WeakReference<>(view);
        view.start();
    }

    private void pauseLast(VideoView next){
        if(mWeakView != null){
            VideoView view = mWeakView.get();
            if(view != null && view != next){
                view.stopPlayback();
            }
        }
    }
}
