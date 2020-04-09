package com.heaven7.android.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.heaven7.android.util2.MediaHelper;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.WeakHandler;
import com.heaven7.java.base.util.Throwables;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TextureVideoView2 extends TextureView
        implements TextureView.SurfaceTextureListener{

    private final String TAG = "TextureVideoView_"+hashCode();
    private static final int MSG_NONE = 0;
    private static final int MSG_START   = 0x0001;
/*    private static final int MSG_PAUSE   = 0x0002;
    private static final int MSG_RESUME  = 0x0003;
    private static final int MSG_STOP    = 0x0004;
    private static final int MSG_RELEASE = 0x0005;
    private static final int MSG_SEEK    = 0x0006;*/

    private final InternalCallback mInternalCallback = new InternalCallback();
    private final MediaHelper0 mMedia = new MediaHelper0(mInternalCallback);
    private Handler mMainHandler;
    private Handler mWorkHandler;
    private Callback mCallback;

    private AudioManagerCompat.Delegate mAudioMDelegate;

    private final AtomicBoolean mCancelled = new AtomicBoolean(false);
    private final AtomicReference<Runnable> mStartTask = new AtomicReference<>();
    private String mUrl;
    private Surface mSurface;
    private volatile int mPendingMessage = MSG_NONE;
    private int mStartPos;

    private int mScaleType = ScaleManager.ScaleType_FIT_CENTER;
    private boolean mDebug;

    private static final HandlerThread sThread = new HandlerThread("VideoPlayThread");

    static {
        sThread.start();
    }

    public TextureVideoView2(Context context) {
        this(context, null);
    }

    public TextureVideoView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureVideoView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AudioManager mAudioM = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioMDelegate = AudioManagerCompat.create(mAudioM, new MediaPlayer0());

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextureVideoView, 0, 0);
            try {
                mDebug = a.getBoolean(R.styleable.TextureVideoView_debug, false);
                final int scaleType = a.getInt(R.styleable.TextureVideoView_scaleType, mScaleType);
                setScaleType(scaleType);
            } finally {
                a.recycle();
            }
        }
        mMedia.setMediaCallback(mInternalCallback);
        if (!isInEditMode()) {
            mMainHandler = new MainHandler(this);
            mWorkHandler = new InternalHandler(this);
            setSurfaceTextureListener(this);
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setScaleType(int scaleType) {
        if(getVideoWidth() == 0){
            __log("setScaleType", "pending scale. scaleType = " + scaleType);
            mScaleType = scaleType;
        }else {
            mScaleType = scaleType;
            scaleVideoSize(getVideoWidth(), getVideoHeight());
        }
    }

    public int getVideoHeight() {
        if (getMediaPlayer() != null) {
            return getMediaPlayer().getVideoHeight();
        }
        return 0;
    }

    public int getVideoWidth() {
        if (getMediaPlayer() != null) {
            return getMediaPlayer().getVideoWidth();
        }
        return 0;
    }

    public MediaPlayer getMediaPlayer() {
        return mMedia.getMediaPlayer();
    }
    public void setVideoUrl(String url) {
        mUrl = url;
    }
    public void setVideoURI(Uri uri){
        mUrl = uri != null ? uri.toString() : null;
    }
    public boolean isPlaying() {
        return mMedia.getMediaState() == MediaHelper.STATE_PLAYING;
    }

    public boolean isPaused() {
        return mMedia.getMediaState() == MediaHelper.STATE_PAUSED;
    }
    //---------------------------------------------------

    public void seekTo(int positionMesc){
        switch (mMedia.getMediaState()){
            case MediaHelper.STATE_NOT_START:
            case MediaHelper.STATE_RELEASE:
                return;

            case MediaHelper.STATE_BUFFERING:
                __log("seekTo", "STATE_BUFFERING.");
                mStartPos = positionMesc;
                break;
            case MediaHelper.STATE_PAUSED:
            case MediaHelper.STATE_PLAYING:
                __log("seekTo", "STATE_PLAYING start...>>>");
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MediaPlayer player = mMedia.getMediaPlayer();
                        if(player != null){
                            player.seekTo(mStartPos);
                        }
                        __log("seekTo", "STATE_PLAYING end...<<<");
                    }
                });
                break;
        }
    }
    public void start(){
        start(0);
    }
    public void start(int position){
        Throwables.checkNull(mUrl);
        mStartPos = position;
        if (mSurface != null) {
            switch (mMedia.getMediaState()){
                case MediaHelper.STATE_NOT_START:
                    __log("start", "STATE_NOT_START start(" + mStartPos + ")...>>>");
                    Runnable task = new Runnable() {
                        @Override
                        public void run() {
                            if (mCancelled.get()) {
                                return;
                            }
                            mStartTask.set(null);
                            mMedia.initializePlayerIfNeed();
                            mMedia.startPlay(mUrl, mStartPos);
                            __log("start", "STATE_NOT_START("+ mStartPos +") end...<<<");
                        }
                    };
                    mStartTask.set(task);
                    mWorkHandler.post(task);
                    break;

                case MediaHelper.STATE_RELEASE:
                case MediaHelper.STATE_PAUSED:
                case MediaHelper.STATE_BUFFERING:
                    break;
                case MediaHelper.STATE_PLAYING:
                    Logger.w(TAG, "called start()  but is in playing state. ignore this operation.");
                    break;
            }
        }else{
            mPendingMessage = MSG_START;
            Logger.w(TAG, "start", "add Start to pending messages. start failed.");
        }
    }
    public void stop(){
        removeStartTask();
        switch (mMedia.getMediaState()){
            case MediaHelper.STATE_NOT_START:
            case MediaHelper.STATE_RELEASE:
                //do nothing
                break;
            case MediaHelper.STATE_PAUSED:
            case MediaHelper.STATE_PLAYING:
                __log("stop", "STATE_PLAYING start...>>>");
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMedia.stop();
                        lossAudioFocus();
                        __log("stop", "STATE_PLAYING end...<<<");
                    }
                });
                break;

            case MediaHelper.STATE_BUFFERING:
                __log("STATE_BUFFERING", "STATE_BUFFERING.");
                mMedia.setShouldPlayWhenPrepared(false);
                break;

        }
    }
    public void release(){
        //__log("release", "current state is " + MediaHelper.getStateString(mMedia.getMediaState()));
        removeStartTask();
        switch (mMedia.getMediaState()){
            case MediaHelper.STATE_BUFFERING:
                __log("release", "STATE_BUFFERING.");
                mMedia.setShouldPlayWhenPrepared(false);

            case MediaHelper.STATE_NOT_START:
            case MediaHelper.STATE_RELEASE:
            case MediaHelper.STATE_PAUSED:
            case MediaHelper.STATE_PLAYING:
                __log("release", "STATE_PLAYING start...>>>");
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMedia.onDestroy();
                        lossAudioFocus();
                        __log("release", "STATE_PLAYING end...<<<");
                    }
                });
                break;
        }
    }
    public void pause(){
        switch (mMedia.getMediaState()){
            case MediaHelper.STATE_NOT_START:
            case MediaHelper.STATE_RELEASE:
            case MediaHelper.STATE_PAUSED:
                return;
            case MediaHelper.STATE_BUFFERING:
                __log("pause", "STATE_BUFFERING.");
                mMedia.setShouldPlayWhenPrepared(false);
                break;

            case MediaHelper.STATE_PLAYING:
                __log("pause", "STATE_PLAYING start...>>>");
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCancelled.get()){
                            return;
                        }
                        mMedia.pause();
                        lossAudioFocus();
                        __log("pause", "STATE_PLAYING end...<<<");
                    }
                });
                break;
        }
    }
    public void resume(){
        switch (mMedia.getMediaState()){
            case MediaHelper.STATE_NOT_START:
            case MediaHelper.STATE_RELEASE:
            case MediaHelper.STATE_PLAYING:
                //do nothing
                break;
            case MediaHelper.STATE_BUFFERING:
                __log("resume", "STATE_BUFFERING start...>>>");
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCancelled.get()){
                            return;
                        }
                        start(mStartPos);
                        __log("resume", "STATE_BUFFERING end...<<<");
                    }
                });
                break;

            case MediaHelper.STATE_PAUSED:
                __log("resume", "STATE_PAUSED start...>>>");
                mWorkHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCancelled.get()){
                            return;
                        }
                        requestAudioFocus();
                        mMedia.resumePlay();
                        __log("resume", "STATE_PAUSED end...<<<");
                    }
                });
                break;

        }
    }
    public void cancel(){
        if(mCancelled.compareAndSet(false, true)){
            __log("destroy", "");
        }
    }
    //----------------------------------------------------
    private void __log(String method, String msg){
        if(mDebug){
            Logger.d(TAG, method, "pos = " + getTag() + ", " + msg);
        }
    }
    private void lossAudioFocus(){
      //  mAudioM.abandonAudioFocus(onAudioFocusChangeListener);
        mAudioMDelegate.lossAudioFocus();
    }
    public void requestAudioFocus(){
        mAudioMDelegate.requestAudioFocus();
       /* mAudioM.requestAudioFocus(
                onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);*/
    }

    private void removeStartTask(){
        Runnable task = mStartTask.get();
        if(task != null){
            mWorkHandler.removeCallbacks(task);
        }
    }
    private void releaseSurfaceIfNeed() {
        if (mSurface != null && mSurface.isValid()) {
            mSurface.release();
            mSurface = null;
            __log("releaseSurfaceIfNeed", "surface is released.");
        }
    }

    private void scaleVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
            return;
        }

        ScaleManager.Size viewSize = new ScaleManager.Size(getWidth(), getHeight());
        ScaleManager.Size videoSize = new ScaleManager.Size(videoWidth, videoHeight);
        ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
        final Matrix matrix = scaleManager.getScaleMatrix(mScaleType);
        __log("scaleVideoSize", "videoWidth = " + videoWidth + " ,videoHeight = " + videoHeight);
        __log("scaleVideoSize", "matrix = " + matrix);
        if (matrix == null) {
            return;
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            setTransform(matrix);
        } else {
            mMainHandler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    setTransform(matrix);
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        __log("onDetachedFromWindow", "");
        releaseSurfaceIfNeed();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        __log("onAttachedToWindow", "");
        //mCancelled.set(false);
        super.onAttachedToWindow();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if ((getSurfaceTexture() != null && getSurfaceTexture() != surface) || mSurface == null) {
            releaseSurfaceIfNeed();
            mSurface = new Surface(surface);
            __log("onSurfaceTextureAvailable", "create new surface");
        }
        if(mPendingMessage == MSG_START){
            Logger.d(TAG, "onSurfaceTextureAvailable", "handle pending start.");
            mPendingMessage = MSG_NONE;
            start(mStartPos);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        __log("onSurfaceTextureDestroyed", "");
        release();
        releaseSurfaceIfNeed();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class MediaHelper0 extends MediaHelper {

        private final InternalCallback mCallback;
        private volatile boolean shouldPlayWhenPrepared = true;

        private MediaHelper0(InternalCallback callback){
            this.mCallback = callback;
        }
        @Override
        public void initializePlayerIfNeed() {
            super.initializePlayerIfNeed();
            MediaPlayer mp = getMediaPlayer();
            if(Build.VERSION.SDK_INT >= 21){
                mp.setAudioAttributes(new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build());
            }else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mp.setOnVideoSizeChangedListener(mCallback);
            mp.setOnInfoListener(mCallback);
            mp.setOnBufferingUpdateListener(mCallback);
            //mp.setSurface(mSurface);
        }

        @Override
        protected void onPrepared(MediaPlayer mp, int position) {
            if(mCancelled.get()){
                return;
            }
            if(shouldPlayWhenPrepared){
                super.onPrepared(mp, position);
            }else {
                shouldPlayWhenPrepared = true;
            }
        }
        public void setShouldPlayWhenPrepared(boolean shouldPlayWhenPrepared){
            this.shouldPlayWhenPrepared = shouldPlayWhenPrepared;
        }

        @TargetApi(21)
        public void setAudioAttributes(AudioAttributes audioAttrs) {
            MediaPlayer player = getMediaPlayer();
            if(player != null){
                player.setAudioAttributes(audioAttrs);
            }
        }
        public void setStreamType(int type) {
            MediaPlayer player = getMediaPlayer();
            if(player != null){
                player.setAudioStreamType(type);
            }
        }
    }

    private class InternalCallback extends MediaHelper.MediaCallback implements MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {

        @Override
        public void setDataSource(MediaPlayer mp, String url) {
            final Uri uri;
            if(url.startsWith("/")){
                //file
                uri = Uri.fromFile(new File(url));
            }else{
               /* (url.startsWith("https://") || url.startsWith("http://")
                        || url.startsWith("rtsp:")*/
               //https/http/rtsp or content provider uris....etc.
                uri = Uri.parse(url);
            }
            try {
                mp.setDataSource(getContext(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onPlayComplete(MediaPlayer mediaPlayer, String s) {
            if (mCallback != null) {
                mCallback.onPlayComplete(mediaPlayer, s);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if(mCallback != null){
                return mCallback.onError(mp, what, extra);
            }
            return super.onError(mp, what, extra);
        }

        @Override
        public void onPrePrepare(MediaPlayer mp, String filename) {
            __log("onPrePrepare","filename = " + filename);
            if(mSurface != null){
                mp.setSurface(mSurface);
            }else {
                Logger.w(TAG, "onPrePrepare", "no surface.");
            }
            //mp.setLooping(true);
            //mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mAudioMDelegate.setStreamType(AudioManager.STREAM_MUSIC);

            requestAudioFocus();
            if (mCallback != null) {
                mCallback.onPrePrepare(mp, filename);
            }
        }

        @Override
        public void onMediaStateChanged(MediaPlayer mediaPlayer, byte b) {
            if (mCallback != null) {
                 mCallback.onMediaStateChanged(mediaPlayer, b);
            }
        }

        @Override
        public void onPrepareComplete(MediaPlayer mp, String filename) {
             if(mCallback != null){
                 mCallback.onPrepareComplete(mp, filename);
             }
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if(mCallback != null){
                mCallback.onBufferingUpdate(mp, percent);
            }
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if(mCallback != null){
                return mCallback.onInfo(mp, what, extra);
            }
            return false;
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            __log("onVideoSizeChanged", "w = " + width + " ,h = " + height);
            scaleVideoSize(width, height);
        }
    }

    private class MediaPlayer0 implements AudioManagerCompat.IMediaPlayer{

        @Override
        public MediaPlayer getMediaPlayer() {
            return mMedia.getMediaPlayer();
        }
        @Override
        public void setStreamType(int type) {
            mMedia.setStreamType(type);
        }
        @Override
        public void setAudioAttributes(AudioAttributes audioAttrs) {
            mMedia.setAudioAttributes(audioAttrs);
        }
        @Override
        public void pause() {
            TextureVideoView2.this.pause();
        }
        @Override
        public void stop() {
            TextureVideoView2.this.stop();
        }
        @Override
        public void resumeIfNeed() {
            if(isPaused()){
                resume();
            }
        }
    }

    private static class InternalHandler extends WeakHandler<TextureVideoView2>{

        public InternalHandler(TextureVideoView2 view) {
            super(sThread.getLooper(), view);
        }
    }
    private static class MainHandler extends WeakHandler<TextureVideoView2>{

        public MainHandler(TextureVideoView2 view) {
            super(Looper.getMainLooper(), view);
        }
    }


    public abstract static class Callback extends MediaHelper.MediaCallback {

        public abstract void onBufferingUpdate(MediaPlayer mp, int percent);

        public abstract boolean onInfo(MediaPlayer mp, int what, int extra);
    }
}
