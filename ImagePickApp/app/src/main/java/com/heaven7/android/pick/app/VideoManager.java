package com.heaven7.android.pick.app;

import android.app.Activity;
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
import com.heaven7.core.util.MainWorker;
import com.heaven7.java.base.util.SparseArrayDelegate;
import com.heaven7.java.base.util.SparseFactory;

import java.lang.ref.WeakReference;

import lib.vida.video.TextureVideoView2;

/**
 */
public class VideoManager implements VideoManageDelegate, ViewPager.OnPageChangeListener {

    private static final String TAG = "VideoManager";
    private WeakReference<TextureVideoView2> mWeakView;

    private final MediaCallback0 mCallback;
    private final SparseArrayDelegate<TextureVideoView2> mMap = SparseFactory.newSparseArray(10);

    public VideoManager(Context context) {
        this.mCallback = new MediaCallback0(context);
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
    public void onBindItem(View v, int index, IImageItem data) {
        // Logger.d(TAG, "setMediaData: " + data.getFilePath());
        TextureVideoView2 view = (TextureVideoView2) v;
        view.setVideoURI(FileProviderHelper.getUriForFile(v.getContext(), data.getFilePath()));
        view.setTag(index);
        mMap.put(index, view);
    }

    @Override
    public void pauseVideo(View v, int position, IImageItem data) {
        Logger.d(TAG, "pauseVideo");
        TextureVideoView2 view = (TextureVideoView2) v;
        view.pause();
    }

    @Override
    public void resumeVideo(View v, int position, IImageItem data) {
        Logger.d(TAG, "resumeVideo");
        TextureVideoView2 view = (TextureVideoView2) v;
        view.resume();
    }

    @Override
    public void onDestroyItem(View v, int position, IImageItem data) {
        Logger.d(TAG, "destroyVideo");
        TextureVideoView2 view = (TextureVideoView2) v;
        view.stop();
        mMap.remove(position);
    }

    @Override
    public void releaseVideo(View v, int position, IImageItem data) {
        Logger.d(TAG, "releaseVideo");
        release((TextureVideoView2) v);
    }

    @Override
    public void onDetach(Activity activity) {
        mMap.clear();
        mCallback.release();
    }

    @Override
    public void onAttach(Activity activity) {

    }

    @Override
    public void setPrimaryItem(View v, int actualPosition, IImageItem data) {
        Logger.d(TAG, "setPrimaryItem","pos: " + actualPosition + " ,path = " + data.getFilePath());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
       /* if(positionOffset > 0.5f){
            position++;
        }*/
    }
    @Override
    public void onPageSelected(int position) {
        Logger.d(TAG, "onPageSelected", "position = " + position);
        TextureVideoView2 view = mMap.get(position);
        if(view == null){
            //first time, not prepared.
            MainWorker.postDelay(200, new Runnable() {
                @Override
                public void run() {
                    onPageSelected(position);
                }
            });
        }else {
            pauseLast(view);
            mWeakView = new WeakReference<>(view);
            view.start();
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
        //mScrollState = state;
    }
    private void pauseLast(TextureVideoView2 next){
        if(mWeakView != null){
            TextureVideoView2 view = mWeakView.get();
            if(view != null && view != next){
                Logger.d(TAG, "", "start pause: pos = " + view.getTag());
                //view.setVideoURI(null);
                view.stop();
            }
        }
    }

    private void release(TextureVideoView2 view){
        if(view != null){
            view.cancel();
            view.release();
        }
    }

    private class MediaCallback0 extends TextureVideoView2.Callback{

        private final PowerManager mPM;
        private PowerManager.WakeLock mWakeLock;

        public MediaCallback0(Context context) {
            this.mPM = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
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
                mWakeLock = null;
            }
        }
    }
}
