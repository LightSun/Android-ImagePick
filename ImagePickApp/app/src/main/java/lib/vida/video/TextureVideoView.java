package lib.vida.video;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.heaven7.core.util.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This is player implementation based on {@link TextureView}
 * It encapsulates {@link MediaPlayer}.
 *
 * @author heaven7
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureVideoView extends TextureView
        implements TextureView.SurfaceTextureListener,
        Handler.Callback {

    private static final String TAG = "TextureVideoView";
    private static final boolean SHOW_LOGS = BuildConfig.DEBUG;

    private volatile int mCurrentState = STATE_IDLE;
    private volatile int mTargetState = STATE_IDLE;

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private static final int MSG_START  = 0x0001;
    private static final int MSG_PAUSE  = 0x0004;
    private static final int MSG_STOP   = 0x0006;
    private static final int MSG_STOP_WITHOUT_CALLBACK = 0x0007;
    private static final int MSG_SEEK  = 0x0008;

    private Uri mUri;
    private Context mContext;
    private Surface mSurface;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    private final ArrayList<MediaPlayerCallback> mCallbacks = new ArrayList<>();
    private Handler mMainHandler;
    private Handler mVideoHandler;

    private boolean mSoundMute;
    private boolean mHasAudio;

    private int mScaleType = ScaleManager.ScaleType_NONE;
    private int mStartPosition;

    private static final HandlerThread sThread = new HandlerThread("VideoPlayThread");

    static {
        sThread.start();
    }

    public interface MediaPlayerCallback {
        /** called on player prepared, may be playing , may continue play */
        void onPrepared(MediaPlayer mp, int startPos);

        void onStopped(MediaPlayer mp);

        void onCompletion(MediaPlayer mp);

        void onBufferingUpdate(MediaPlayer mp, int percent);

        void onVideoSizeChanged(MediaPlayer mp, int width, int height);

        void onInfo(MediaPlayer mp, int what, int extra);

        void onError(MediaPlayer mp, int what, int extra);

        void onPaused(MediaPlayer mp);
    }

    public static abstract class MediaPlayerCallbackAdapter implements MediaPlayerCallback{

        public void onPrepared(MediaPlayer mp, int startPos){}

        public void onStopped(MediaPlayer mp){}

        public void onPaused(MediaPlayer mp){}

        public void onCompletion(MediaPlayer mp){}

        public void onBufferingUpdate(MediaPlayer mp, int percent){  }

        public void onVideoSizeChanged(MediaPlayer mp, int width, int height){  }

        public void onInfo(MediaPlayer mp, int what, int extra){
            Logger.d("MediaPlayerCallbackAdapter", "onInfo", "what = "
                    + what + " ,extra = " + extra);
        }
        public  void onError(MediaPlayer mp, int what, int extra){
            Logger.d("MediaPlayerCallbackAdapter", "onError", "what = "
                    + what + " ,extra = " + extra);
        }
    }

    public TextureVideoView(Context context) {
        this(context, null);
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextureVideoView, 0, 0);
        if (a == null) {
            return;
        }

        int scaleType = a.getInt(R.styleable.TextureVideoView_scaleType, mScaleType);
        a.recycle();
        mScaleType = scaleType;

        if (!isInEditMode()) {
            mContext = getContext();
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mMainHandler = new Handler();
            mVideoHandler = new Handler(sThread.getLooper(), this);
            setSurfaceTextureListener(this);
        }
    }

    public void setVolume(float val) {
        if(mMediaPlayer != null) {
            mMediaPlayer.setVolume(val, val);
        }
    }

    public void setStartPosition(int startPos) {
        mStartPosition = startPos;
    }

    public void clearMediaPlayerCallbacks() {
        mCallbacks.clear();
    }

    public void addMediaPlayerCallback(MediaPlayerCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    public int getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }

        return -1;
    }

    public int getVideoHeight() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    public int getVideoWidth() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoWidth();
        }
        return 0;
    }

    public void setScaleType(int scaleType) {
        mScaleType = scaleType;
        scaleVideoSize(getVideoWidth(), getVideoHeight());
    }

    public int getScaleType() {
        return mScaleType;
    }

    public void seekTo(int position) {
        if(isInPlaybackState()){
            mVideoHandler.obtainMessage(MSG_SEEK, position).sendToTarget();
        }else{
            Logger.w(TAG, "seekTo", "not in play state. ignore");
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
    public boolean handleMessage(Message msg) {
        Logger.d(TAG, "handleMessage", Thread.currentThread().toString());
        synchronized (TextureVideoView.class) {
            switch (msg.what) {

                case MSG_START:
                    if (SHOW_LOGS) Log.i(TAG, "<< handleMessage MSG_START");
                    openVideo();
                    if (SHOW_LOGS) Log.i(TAG, ">> handleMessage MSG_START");
                    break;


                case MSG_PAUSE:
                    if (SHOW_LOGS) Log.i(TAG, "<< handleMessage MSG_PAUSE");
                    if (mMediaPlayer != null) {
                        mMediaPlayer.pause();
                    }
                    mCurrentState = STATE_PAUSED;
                    callbackPause();
                    if (SHOW_LOGS) Log.i(TAG, ">> handleMessage MSG_PAUSE");
                    break;

                case MSG_STOP:
                    if (SHOW_LOGS) Log.i(TAG, "<< handleMessage MSG_STOP");
                    release(true, true);
                    if (SHOW_LOGS) Log.i(TAG, ">> handleMessage MSG_STOP");
                    break;

                case MSG_STOP_WITHOUT_CALLBACK:
                    if (SHOW_LOGS) Log.i(TAG, "<< handleMessage MSG_STOP_WITHOUT_CALLBACK");
                    release(true, false);
                    if (SHOW_LOGS) Log.i(TAG, ">> handleMessage MSG_STOP_WITHOUT_CALLBACK");
                    break;

                case MSG_SEEK:
                    if(mMediaPlayer != null){
                        mMediaPlayer.seekTo((Integer) msg.obj);
                        //restart
                        if(mCurrentState == STATE_PLAYBACK_COMPLETED){
                            mMediaPlayer.start();
                            mTargetState = STATE_PLAYING;
                            mCurrentState = STATE_PLAYING;
                        }
                        Logger.d(TAG, "handleMessage", "seek ok. position(in mill) = " + msg.obj);
                    }
                    break;

                default:
                    throw new UnsupportedOperationException();
            }
        }
        return true;
    }

    private void openVideo() {
       // Logger.d(TAG, "openVideo", "" + SystemClock.elapsedRealtime());
        if (mUri == null || mSurface == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        if(mTargetState != STATE_PLAYING){
            Logger.i(TAG, "openVideo", "mTargetState("+ mTargetState +") != STATE_PLAYING.");
        }
       /* mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);*/

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false, false);

        try {
            InternalMediaListener listener = new InternalMediaListener();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(listener);
            mMediaPlayer.setOnVideoSizeChangedListener(listener);
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.setOnErrorListener(listener);
            mMediaPlayer.setOnInfoListener(listener);
            mMediaPlayer.setOnBufferingUpdateListener(listener);
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepareAsync();

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            mTargetState = STATE_PREPARING;

            mHasAudio = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                try {
                    MediaExtractor mediaExtractor = new MediaExtractor();
                    mediaExtractor.setDataSource(mContext, mUri, null);
                    MediaFormat format;
                    for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
                        format = mediaExtractor.getTrackFormat(i);
                        String mime = format.getString(MediaFormat.KEY_MIME);
                        if (mime.startsWith("audio/")) {
                            mHasAudio = true;
                            break;
                        }
                    }
                } catch (Exception ex) {
                    // may be failed to instantiate extractor.
                }
            }

        } catch (IOException | IllegalStateException | IllegalArgumentException ex) {
            if (SHOW_LOGS) Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
                    }
                }
            });
        }
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        if (SHOW_LOGS) Log.i(TAG, "setVideoURI " + uri.toString());
        mUri = uri;
    }

    public void start() {
       // Logger.d(TAG, "start", "" + SystemClock.elapsedRealtime());
        mTargetState = STATE_PLAYING;

        if (isInPlaybackState()) {
            mVideoHandler.obtainMessage(MSG_STOP).sendToTarget();
        }

        if (mUri != null && mSurface != null) {
            mVideoHandler.obtainMessage(MSG_START).sendToTarget();
        }else{
            Logger.w(TAG, "start", "start failed.");
        }
    }

    public void start(int startPosition) {
        setStartPosition(startPosition);

        Logger.d(TAG, "start", "startPosition = " + startPosition);
        mTargetState = STATE_PLAYING;
        if (startPosition > 0) {
            mVideoHandler.obtainMessage(MSG_STOP_WITHOUT_CALLBACK).sendToTarget();
        }
        if (mUri != null && mSurface != null) {
            mVideoHandler.obtainMessage(MSG_START).sendToTarget();
        }else{
            Logger.w(TAG, "start", "start failed.");
        }
    }
    public boolean isPaused(){
        return mCurrentState == STATE_PAUSED;
    }

    public void pause() {
        mTargetState = STATE_PAUSED;

        if (isPlaying()) {
            mVideoHandler.obtainMessage(MSG_PAUSE).sendToTarget();
        }
    }

    public void resume() {
        mTargetState = STATE_PLAYING;

        if (!isPlaying()) {
            mVideoHandler.obtainMessage(MSG_START).sendToTarget();
        }
    }

    public void stop() {
        mTargetState = STATE_PLAYBACK_COMPLETED;

        if (isInPlaybackState()) {
            mVideoHandler.obtainMessage(MSG_STOP).sendToTarget();
        }
    }

    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    public void mute() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(0, 0);
            mSoundMute = true;
        }
    }

    public void unMute() {
        if (mAudioManager != null && mMediaPlayer != null) {
            int max = 100;
            int audioVolume = 100;
            double numerator = max - audioVolume > 0 ? Math.log(max - audioVolume) : 0;
            float volume = (float) (1 - (numerator / Math.log(max)));
            mMediaPlayer.setVolume(volume, volume);
            mSoundMute = false;
        }
    }

    public boolean isMute() {
        return mSoundMute;
    }

    public boolean isHasAudio() {
        return mHasAudio;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseSurfaceIfNeed();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //Logger.d(TAG, "onSurfaceTextureAvailable", "" + SystemClock.elapsedRealtime());
        if((getSurfaceTexture() != null && getSurfaceTexture() != surface) || mSurface == null) {
            releaseSurfaceIfNeed();
            mSurface = new Surface(surface);
        }
        // mSurface = new Surface(surface);
        if (mTargetState == STATE_PLAYING) {
            if (SHOW_LOGS) Log.i(TAG, "onSurfaceTextureAvailable start");
            start();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //Logger.d(TAG, "onSurfaceTextureDestroyed", "" + SystemClock.elapsedRealtime());
        releaseSurfaceIfNeed();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //Logger.d(TAG, "onSurfaceTextureUpdated", "");
       // surface.updateTexImage();
    }

    private void callbackPause() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (MediaPlayerCallback callback : mCallbacks) {
                    callback.onPaused(mMediaPlayer);
                }
            }
        });
    }

    // release the media player in any state
    private void release(boolean clearTargetState, boolean shouldCallback) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (clearTargetState) {
                mTargetState = STATE_IDLE;
            }
            if(shouldCallback) {
                mMainHandler.post(() -> {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onStopped(mMediaPlayer);
                    }
                });
            }
//            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
        }
    }

    private void releaseSurfaceIfNeed() {
        if(mSurface != null && mSurface.isValid()){
            mSurface.release();
            mSurface = null;
        }
    }

    private class InternalMediaListener implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener, MediaPlayer.OnVideoSizeChangedListener{

        @Override
        public void onCompletion(final MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onCompletion(mp);
                    }
                }
            });
        }
        @Override
        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
            if (SHOW_LOGS)
                Log.e(TAG, "onError() called with " + "mp = [" + mp + "], what = [" + what + "], extra = [" + extra + "]");
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onError(mp, what, extra);
                    }
                }
            });
            return true;
        }

        @Override
        public void onPrepared(final MediaPlayer mp) {
            if (SHOW_LOGS) Log.i(TAG, "onPrepared " + mUri.toString() + ", duration = " + mp.getDuration());
            if (mTargetState != STATE_PREPARING || mCurrentState != STATE_PREPARING) {
                Logger.w(TAG, "onPrepared", "wrong state");
                return;
            }

            mCurrentState = STATE_PREPARED;
            //continue play or not.
            final int startPos = mStartPosition;
            if (isInPlaybackState() && !isPlaying()) {
                if(mStartPosition > 0){
                    mMediaPlayer.seekTo(mStartPosition);
                    mStartPosition = 0;
                }
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
                mTargetState = STATE_PLAYING;
            }

            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onPrepared(mp, startPos);
                    }
                }
            });
        }

        @Override
        public void onVideoSizeChanged(final MediaPlayer mp, final int width, final int height) {
            scaleVideoSize(width, height);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onVideoSizeChanged(mp, width, height);
                    }
                }
            });
        }

        @Override
        public void onBufferingUpdate(final MediaPlayer mp, final int percent) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onBufferingUpdate(mp, percent);
                    }
                }
            });
        }

        @Override
        public boolean onInfo(final MediaPlayer mp, final int what, final int extra) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (MediaPlayerCallback callback : mCallbacks) {
                        callback.onInfo(mp, what, extra);
                    }
                }
            });
            return true;
        }
    }
}
