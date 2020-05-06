package com.heaven7.android.pick.app.impl;

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

import com.heaven7.android.imagepick.pub.ImageLoadDelegate;
import com.heaven7.android.imagepick.pub.ImagePickManager;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.android.imagepick.pub.module.IImageItem;
import com.heaven7.android.pick.app.R;
import com.heaven7.android.pick.app.utils.FileProviderHelper;
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
public class VideoManager implements ViewPager.OnPageChangeListener, VideoManageDelegate{

    private static final String TAG = "VideoManager";
    private WeakReference<TextureVideoView> mWeakView;

    private final MediaCallback0 mCallback;
    private final SparseArrayDelegate<MediaPlayerView> mMap = SparseFactory.newSparseArray(10);
    private Task mTask;
    private int mCurrentPos;

    private int mInitContentType = MediaViewCons.TYPE_VIDEO;

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
        TextureVideoView videoView = getTextureVideoView(playerView);
        videoView.setCallback(mCallback);
        playerView.getDelegate().setCallback(new MediaPlayerView.Callback() {
            @Override
            public void pauseVideo(MediaPlayerView mpv) {
                TextureVideoView view = (TextureVideoView) mpv.getVideoView();
                view.pause();
            }
            @Override
            public void resumeVideo(MediaPlayerView mpv) {
                TextureVideoView view = (TextureVideoView) mpv.getVideoView();
                if(view.isPaused()){
                    view.resume();
                }else {
                    view.start();
                }
            }
            @Override
            public void onClickCover(MediaPlayerView mpv) {
                //not need current
            }
        });
        mInitContentType = playerView.getContentType();
        return playerView;
    }

    @Override
    public void onBindItem(View v, int position, int realPos, IImageItem data) {
        Logger.d(TAG, "onBindItem", "position = " + position);
        TextureVideoView view = getTextureVideoView(v);
        view.setVideoURI(FileProviderHelper.getUriForFile(v.getContext(), data.getFilePath()));
        view.setTag(position);
        v.setTag(data);
        mMap.put(position, (MediaPlayerView) v);
    }
    @Override
    public void onDestroyItem(View v, int position, int realPos, IImageItem data) {
        Logger.d(TAG, "destroyVideo", "position = " + position);
        MediaPlayerView playerView = (MediaPlayerView) v;
        TextureVideoView view = getTextureVideoView(v);
        view.stop();
        playerView.setContentType(mInitContentType);
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
        ac.getLifecycle().addObserver(new LifecycleListener(this));
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
            if(!stopLast(view)){
                mWeakView = new WeakReference<>(view);
                //only for video we need start play
                switch (playerView.getContentType()){
                    case MediaViewCons.TYPE_VIDEO:
                        view.start();
                        Logger.d(TAG, "onPageSelected", " position = " + position + ", start play.");
                        break;

                    case MediaViewCons.TYPE_PAUSE:
                        //show cover?
                    case MediaViewCons.TYPE_COVER:
                    case MediaViewCons.TYPE_COVER_PAUSE:{
                        IImageItem item = (IImageItem) playerView.getTag();
                        ImageLoadDelegate delegate = ImagePickManager.get().getImagePickDelegate().getImageLoadDelegate();
                        delegate.loadImage(null, playerView.getCoverView(), item, null);
                        break;
                    }

                    default:
                        throw new UnsupportedOperationException();
                }
            }else {
                Logger.d(TAG, "onPageSelected", " position = " + position + ",duplicate start play.");
            }
            //for cover show cover.
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
                MediaPlayerView playerView = (MediaPlayerView) view.getParent();
                playerView.setContentType(mInitContentType);
            }else {
                return true;
            }
        }
        return false;
    }

   // private int mScrollState;
    private class Task implements Runnable{
       final int position;
       final AtomicBoolean cancelled = new AtomicBoolean(false);
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
   private static class LifecycleListener implements LifecycleEventObserver{

        final WeakReference<VideoManager> mWeakRef;

       public LifecycleListener(VideoManager vm) {
           this.mWeakRef = new WeakReference<>(vm);
       }

       @Override
       public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
           VideoManager vm = mWeakRef.get();
           if(vm == null){
               return;
           }
           final SparseArrayDelegate<MediaPlayerView> mMap = vm.mMap;
           final int mCurrentPos = vm.mCurrentPos;

           switch (event){
               case ON_PAUSE: {
                   MediaPlayerView view = mMap.get(mCurrentPos);
                   if (view != null) {
                       TextureVideoView videoView = vm.getTextureVideoView(view);
                       //non-pause -> paused
                       if(videoView.isPlaying()){
                           view.performClickType();
                       }
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
                   MediaPlayerView view = mMap.get(mCurrentPos);
                   TextureVideoView videoView = vm.getTextureVideoView(mCurrentPos);
                   if(videoView != null){
                       videoView.stop();
                       view.setContentType(vm.mInitContentType);
                   }else {
                       Logger.d(TAG, "onStateChanged", "ON_STOP. no video view.");
                   }
                   break;
               }
               case ON_DESTROY: {
                   source.getLifecycle().removeObserver(this);
                   MediaPlayerView playerView = mMap.getAndRemove(mCurrentPos);
                   if(playerView != null){
                       TextureVideoView view = (TextureVideoView) playerView.getVideoView();
                       view.cancel();
                       view.release();
                       playerView.setContentType(vm.mInitContentType);
                   }
                   break;
               }
           }
       }
   }

    private static class MediaCallback0 extends TextureVideoView.Callback{

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
