package com.heaven7.android.pick.app;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.android.video.MediaViewCons;
import com.heaven7.android.video.view.MediaPlayerView;
import com.heaven7.android.video.view.TextureVideoView;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;
import com.heaven7.java.base.util.SparseArrayDelegate;
import com.heaven7.java.base.util.SparseFactory;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public class VideoManager implements VideoManageDelegate, ViewPager.OnPageChangeListener {

    private static final String TAG = "VideoManager";
    private WeakReference<TextureVideoView> mWeakView;

    private final MediaCallback0 mCallback;
    private final SparseArrayDelegate<MediaPlayerView> mMap = SparseFactory.newSparseArray(10);
    private Task mTask;
    private int mCurrentPos;

    public VideoManager(Context context) {
        this.mCallback = new MediaCallback0(context);
    }
    private TextureVideoView getTextureVideoView(View view){
        MediaPlayerView playerView = (MediaPlayerView) view;
        return (TextureVideoView) playerView.getVideoView();
    }
    private TextureVideoView getTextureVideoView(int pos){
        MediaPlayerView playerView = mMap.get(pos);
        return playerView != null ? (TextureVideoView) playerView.getVideoView() : null;
    }
    @Override
    public View createVideoView(Context context, ViewGroup parent, IImageItem data) {
        MediaPlayerView playerView = (MediaPlayerView) LayoutInflater.from(context)
                .inflate(R.layout.item_texture_video2, parent, false);
        final TextureVideoView videoView = getTextureVideoView(playerView);
        videoView.setCallback(mCallback);
        playerView.getDelegate().setCallback(new MediaPlayerView.Callback() {
            @Override
            public void pauseVideo(MediaPlayerView mpv) {
                videoView.pause();
            }
            @Override
            public void resumeVideo(MediaPlayerView mpv) {
                videoView.resume();
            }
            @Override
            public void onClickCover(MediaPlayerView mpv) {
                //not need current
            }
        });
        return playerView;
    }

    @Override
    public void onBindItem(View v, int position, IImageItem data) {
        Logger.d(TAG, "onBindItem", "position = " + position);
        TextureVideoView view = getTextureVideoView(v);
        view.setVideoURI(FileProviderHelper.getUriForFile(v.getContext(), data.getFilePath()));
        view.setTag(position);
        mMap.put(position, (MediaPlayerView) v);
    }
    @Override
    public void onDestroyItem(View v, int position, IImageItem data) {
        Logger.d(TAG, "destroyVideo", "position = " + position);
        MediaPlayerView playerView = (MediaPlayerView) v;
        TextureVideoView view = getTextureVideoView(v);
        view.stop();
        playerView.showContent(MediaViewCons.TYPE_VIDEO);
        mMap.remove(position);
    }
    @Override
    public void onDetach(Activity activity) {
        mMap.clear();
        mCallback.release();
    }

    @Override
    public void onAttach(Activity activity) {
        AppCompatActivity ac = (AppCompatActivity) activity;
        ac.getLifecycle().addObserver(new LifecycleListener());
    }

    @Override
    public void setPrimaryItem(View v, int actualPosition, IImageItem data) {
        Logger.d(TAG, "setPrimaryItem","pos: " + actualPosition + " ,path = " + data.getFilePath());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //  Logger.d(TAG, "onPageScrolled", "position = " + position);
        if(positionOffset > 0.5f){
            position++;
        }
        //onPageSelected(position);
    }
    @Override
    public void onPageSelected(int position) {
       // Logger.d(TAG, "onPageSelected", "position = " + position);
        mCurrentPos = position;
        MediaPlayerView playerView = mMap.get(position);
        TextureVideoView view = playerView != null ? (TextureVideoView) playerView.getVideoView() : null;
        if(mTask != null){
            mTask.cancel();
            mTask = null;
        }
        if(view == null){
            //first time, not prepared.
            Logger.d(TAG, "onPageSelected", "position = " + position + " not prepared.");
            mTask = new Task(position);
            MainWorker.postDelay(200, mTask);
        }else {
            //only for video we need start play
            if(playerView.getContentType() == MediaViewCons.TYPE_VIDEO){
                if(!stopLast(view)){
                    mWeakView = new WeakReference<>(view);
                    view.start();
                    Logger.d(TAG, "onPageSelected", " position = " + position + ", start play.");
                }else {
                    Logger.d(TAG, "onPageSelected", " position = " + position + ",duplicate start play.");
                }
            }
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
       // mScrollState = state;
    }
    private boolean stopLast(TextureVideoView next){
        if(mWeakView != null){
            TextureVideoView view = mWeakView.get();
            if(view == null){
                return false;
            }
            if(view != next){
                Logger.d(TAG, "stopLast", "start stop: pos = " + view.getTag());
                view.stop();
            }else {
                return true;
            }
        }
        return false;
    }

    private void release(TextureVideoView view){
        if(view != null){
            view.cancel();
            view.release();
        }
    }
   // private int mScrollState;
    private class Task implements Runnable{
       final int position;
       AtomicBoolean cancelled = new AtomicBoolean(false);
       public Task(int position) {
           this.position = position;
       }
       @Override
       public void run() {
           if(!cancelled.get()){
               onPageSelected(position);
           }
       }
       public void cancel(){
           if(cancelled.compareAndSet(false, true)){
               MainWorker.remove(this);
           }
       }
   }
   private class LifecycleListener implements LifecycleEventObserver{
       @Override
       public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
           switch (event){
               case ON_PAUSE: {
                   MediaPlayerView view = mMap.get(mCurrentPos);
                   if (view != null) {
                       view.performClickType();
                   } else {
                       Logger.d(TAG, "onStateChanged", "ON_PAUSE");
                   }
                   break;
               }
               case ON_RESUME: {
                   MediaPlayerView view = mMap.get(mCurrentPos);
                   if(view != null){
                       view.performClickType();
                   }else {
                       Logger.d(TAG, "onStateChanged", "ON_RESUME");
                   }
                   break;
               }
               case ON_STOP: {
                   TextureVideoView videoView = getTextureVideoView(mCurrentPos);
                   if(videoView != null){
                       videoView.stop();
                   }else {
                       Logger.d(TAG, "onStateChanged", "ON_STOP. no video view.");
                   }
                   break;
               }
               case ON_DESTROY: {
                   MediaPlayerView playerView = mMap.getAndRemove(mCurrentPos);
                   if(playerView != null){
                       TextureVideoView view = (TextureVideoView) playerView.getVideoView();
                       view.cancel();
                       view.release();
                       playerView.showContent(MediaViewCons.TYPE_VIDEO);
                   }
                   break;
               }
           }
       }
   }

    private class MediaCallback0 extends TextureVideoView.Callback{

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
