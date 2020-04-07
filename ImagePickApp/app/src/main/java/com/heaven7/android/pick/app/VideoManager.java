package com.heaven7.android.pick.app;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.core.util.Logger;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;

import lib.vida.video.TextureVideoView2;

/**
 */
public class VideoManager implements VideoManageDelegate, ViewPager.OnPageChangeListener {

    private static final String TAG = "VideoManager";
    private WeakReference<TextureVideoView2> mWeakView;
    private int mCurrentItem = -1;

    private final MediaCallback0 mCallback;

    public VideoManager(Context context) {
        this.mCallback = new MediaCallback0(context);
    }

    @Override
    public boolean isVideoView(View view, IImageItem data) {
        return view instanceof TextureVideoView2;
    }

    @Override
    public View createVideoView(Context context, ViewGroup parent, IImageItem data) {
        TextureVideoView2 videoView = (TextureVideoView2) LayoutInflater.from(context)
                .inflate(R.layout.item_texture_video2, parent, false);
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
        videoView.setCallback(mCallback);
        return videoView;
    }

    @Override
    public void setMediaData(Context context, View v, IImageItem data) {
        // Logger.d(TAG, "setMediaData: " + data.getFilePath());
        TextureVideoView2 view = (TextureVideoView2) v;
        view.setVideoURI(FileProviderHelper.getUriForFile(context, data.getFilePath()));
    }

    @Override
    public void pauseVideo(Context context, View videoView) {
        Logger.d(TAG, "pauseVideo");
        TextureVideoView2 view = (TextureVideoView2) videoView;
        view.pause();
    }

    @Override
    public void resumeVideo(Context context, View videoView) {
        Logger.d(TAG, "resumeVideo");
        TextureVideoView2 view = (TextureVideoView2) videoView;
        view.resume();
    }

    @Override
    public void destroyVideo(Context context, View videoView) {
        Logger.d(TAG, "destroyVideo");
        mCurrentItem = -1;
        TextureVideoView2 view = (TextureVideoView2) videoView;
        view.stop();
    }

    @Override
    public void releaseVideo(Context context, View videoView) {
        Logger.d(TAG, "releaseVideo");
        mCurrentItem = -1;
        TextureVideoView2 view = (TextureVideoView2) videoView;
        view.cancel();
        view.release();
        mCallback.release();
    }

    @Override
    public void setCurrentPosition(int position) {
        if(mCurrentItem != position){
            Logger.d(TAG, "setCurrentPosition", "currentPos = " + mCurrentItem + " ,pos = " + position);
            mCurrentItem = position;
        }
    }
    @Override
    public void setPrimaryItem(View v, int actualPosition, IImageItem data) {
        Logger.d(TAG, "setPrimaryItem: " + data.getFilePath());
        Logger.d(TAG, "actualPosition: " + actualPosition);
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

        TextureVideoView2 view = (TextureVideoView2) v;
       // view.setVideoURI(FileProviderHelper.getUriForFile(v.getContext(), data.getFilePath()));
        pauseLast(view);
        mWeakView = new WeakReference<>(view);
        view.start();
    }

    private void pauseLast(TextureVideoView2 next){
        if(mWeakView != null){
            TextureVideoView2 view = mWeakView.get();
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

    private class MediaCallback0 extends TextureVideoView2.Callback{

        private final PowerManager mPM;
        private PowerManager.WakeLock mWakeLock;

        public MediaCallback0(Context context) {
            this.mPM = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onPlayComplete(MediaPlayer mediaPlayer, String s) {

        }
        @Override
        public void onMediaStateChanged(MediaPlayer mediaPlayer, byte b) {

        }
        @Override
        public void onPrePrepare(MediaPlayer mp, String filename) {
        }

        @Override
        public void onPrepareComplete(MediaPlayer mp, String filename) {
            if(mWakeLock == null){
                mWakeLock = mPM.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE, MediaCallback0.class.getName());
                mWakeLock.acquire();
            }
        }
        public void release(){
            if(mWakeLock != null){
                mWakeLock.release();
            }
        }
    }
}
