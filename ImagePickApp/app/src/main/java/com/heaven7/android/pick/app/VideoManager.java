package com.heaven7.android.pick.app;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.core.util.Logger;

import java.lang.ref.WeakReference;

import lib.vida.video.TextureVideoView;

/**
 */
public class VideoManager implements VideoManageDelegate, ViewPager.OnPageChangeListener {

    private static final String TAG = "VideoManager";
    private WeakReference<TextureVideoView> mWeakView;
    private int mCurrentItem = -1;

    @Override
    public boolean isVideoView(View view, IImageItem data) {
        return view instanceof TextureVideoView;
    }

    @Override
    public View createVideoView(Context context, ViewGroup parent, IImageItem data) {
        TextureVideoView videoView = (TextureVideoView) LayoutInflater.from(context)
                .inflate(R.layout.item_texture_video, parent, false);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.pause();
                }else if(videoView.isPaused()){
                    videoView.resume();
                }
            }
        });
        return videoView;
    }

    @Override
    public void setMediaData(Context context, View v, IImageItem data) {
        // Logger.d(TAG, "setMediaData: " + data.getFilePath());
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
        reset();
        TextureVideoView view = (TextureVideoView) videoView;
        view.stop();
    }

    @Override
    public void setCurrentPosition(int position) {
        if(mCurrentItem != position){
            mCurrentItem = position;
            Logger.d(TAG, "setCurrentPosition", "currentPos = " + mCurrentItem + " ,pos = " + position);
        }
    }
    @Override
    public void setPrimaryItem(View v, int actualPosition, IImageItem data) {
        Logger.d(TAG, "setPrimaryItem: " + data.getFilePath());
        if(!isVideoView(v, data)){
            Logger.d(TAG, "not video view.");
            return;
        }
        /*
         * 1, 首次进入时，这个会调用2次
         * 2, 点击pager item 时也会调用
         */
        if(mCurrentItem == actualPosition){
            Logger.d(TAG, "setPrimaryItem", "mCurrentItem == actualPosition.");
            return;
        }
        mCurrentItem = actualPosition;

        TextureVideoView view = (TextureVideoView) v;
       // view.setVideoURI(FileProviderHelper.getUriForFile(v.getContext(), data.getFilePath()));
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
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void reset(){
        mCurrentItem = -1;
    }

    private class MediaPlayerCallback0 extends TextureVideoView.MediaPlayerCallbackAdapter {

        @Override
        public void onPrepared(MediaPlayer mp, int startPos) {

        }
        @Override
        public void onStopped(MediaPlayer mp) {

        }
        @Override
        public void onCompletion(MediaPlayer mp) {

        }
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        }
        @Override
        public void onPaused(MediaPlayer mp) {

        }
    }
}
