package lib.vida.video;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewParent;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.C.ContentType;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.heaven7.android.component.lifecycle.LifeCycleComponent;
import com.heaven7.android.pick.app.R;
import com.heaven7.android.video.MediaViewCons;
import com.heaven7.core.util.Logger;

/**
 * Manages the {@link ExoPlayer}, the IMA plugin and all video playback.
 */
public final class ExoPlayerManager implements AdsMediaSource.MediaSourceFactory , Player.EventListener, LifeCycleComponent {

    private static final Callback DEFAULT = new Callback() {
        @Override
        public Uri getFileUri(Context context, String file) {
            return null;
        }
        @Override
        public void onPlayTimeChanged(long positionUs) {

        }
    };
    private final Callback callback;
    private final DataSource.Factory manifestDataSourceFactory;
    private final DataSource.Factory mediaDataSourceFactory;

    private final Context context;
    private SimpleExoPlayer player;
    private final Object playerView;

    private long contentPosition;
    private boolean mLoop;
    private Uri uri;
    private VideoListener mVideoListener;

    private float volume = 1f;

    public ExoPlayerManager(Context context, Object playerView) {
        this(context, playerView, DEFAULT);
    }
    public ExoPlayerManager(Context context, Object playerView, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.playerView = playerView;
        manifestDataSourceFactory =
                new DefaultDataSourceFactory(
                        context, Util.getUserAgent(context, context.getString(R.string.app_name)));
        mediaDataSourceFactory =
                new DefaultDataSourceFactory(
                        context,
                        Util.getUserAgent(context, context.getString(R.string.app_name)),
                        new DefaultBandwidthMeter());
    }

    public Context getContext(){
        return context;
    }

    public void initPlayer(){
        if(player == null) {
            init(playerView);
        }
    }

    private void init(Object playerView) {
        // Create a default track selector.
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create a player instance.
        // player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        player = newPlayer(context, trackSelector);

        // Bind the player to the view.
        if(playerView != null){
            if(playerView instanceof PlayerView) {
                ((PlayerView) playerView).setPlayer(player);
            }else if(playerView instanceof SurfaceView){
                player.setVideoSurfaceView((SurfaceView) playerView);
            }else if(playerView instanceof TextureView){
                player.setVideoTextureView((TextureView) playerView);
            }else if(playerView instanceof Surface){
                player.setVideoSurface((Surface) playerView);
            }else if(playerView instanceof SurfaceHolder){
                player.setVideoSurfaceHolder((SurfaceHolder) playerView);
            }else if(playerView instanceof PlayerView2){
                ((PlayerView2) playerView).setPlayer(player);
            }
        }
        //player.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        player.addListener(this);
        if(mVideoListener != null){
            player.addVideoListener(mVideoListener);
        }
       // player.setSeekParameters(CLOSEST_SYNC);
    }

    public void setVideoListener(VideoListener vl){
        mVideoListener = vl;
        if(player != null && vl != null){
            player.addVideoListener(vl);
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void setPlayLoop(boolean loop){
        this.mLoop = loop;
    }

    public void seekTo(long timeMs) {
        //mSeekPro.seekTo(timeMs);
        if(player != null){
            player.seekTo(timeMs);
        }
    }

    /** true to play .false to pause. */
    public void setPlayWhenReady(boolean playWhenReady){
        if(player != null) {
            player.setPlayWhenReady(playWhenReady);
        }
    }

    public boolean getPlayWhenReady(){
        return player != null && player.getPlayWhenReady();
    }

    public void prepare(String file) {
        prepare(file, true);
    }

    public void prepare(String file, boolean playWhenReady) {
        prepare(callback.getFileUri(context, file), playWhenReady);
    }

    public void prepareUrl(String url) {
        prepare(Uri.parse(url), true);
    }

    public void prepare(int rawId, boolean playWhenReady){
        //Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + rawId);
        Uri uri = RawResourceDataSource.buildRawResourceUri(rawId);
        prepare(uri,  playWhenReady);
    }

    public void prepare(Uri uri, boolean playWhenReady) {
        this.uri = uri;
        if(player == null){
            Logger.w("ExoCallM", "prepare", "player == null");
            return;
        }
        // Prepare the player with the source.
        player.seekTo(contentPosition);
        player.prepare(buildMediaSource(uri), false, true);
        player.setPlayWhenReady(playWhenReady);
        player.setVolume(volume);
        Logger.d("ExoPlayerManager", "prepare", "uri = " + uri);
    }

    public void setContentPosition(int contentPosition){
        this.contentPosition = contentPosition;
    }

    public void onPause(){
        reset();
    }

    public void onResume(){
        if(player == null) {
            boolean playWhenReady = true;
            if(playerView instanceof PlayerView2){
                if(((PlayerView2) playerView).getState() == PlayerControlLayout.STATE_PAUSE){
                    //for not video mode. just prepare not play
                    playWhenReady = false;
                }
            } else if(playerView instanceof SurfaceView){
                ViewParent parent = ((SurfaceView) playerView).getParent();
                if(parent instanceof MediaSdkPlayerView){
                    MediaSdkPlayerView mspv = (MediaSdkPlayerView) parent;
                    if(mspv.getContentType() == MediaViewCons.TYPE_PAUSE){
                        //no video mode. just prepare not play
                        playWhenReady = false;
                    }
                }
            }
            initPlayer();
            if (uri != null) {
                prepare(uri, playWhenReady);
                player.setVolume(volume);
            }
        }
    }

    public void stop(){
        if(player != null) {
            player.stop();
        }
    }

    public void reset() {
        if (player != null) {
            contentPosition = player.getContentPosition();
            player.release();
            player.removeListener(this);
            if(mVideoListener != null){
                player.removeVideoListener(mVideoListener);
            }
            player = null;
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            player.removeListener(this);
            if(mVideoListener != null){
                player.removeVideoListener(mVideoListener);
            }
            player = null;
        }
        uri = null;
        contentPosition = 0;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if(player != null) {
            player.setVolume(volume);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_ENDED){
            if(mLoop) {
                seekTo(0);
            }
        }else if(playbackState == Player.STATE_READY){
            callback.onPrepared(uri, playWhenReady);
            // Logger.d("ExoPlayerManager", "onPlayerStateChanged", "ready. time = " + System.currentTimeMillis());
            //mSeekPro.startSeekNextIfNeed();
        }
    }

    @Override
    public void onSeekProcessed() {
       // Logger.d("ExoPlayerManager", "onSeekProcessed", "time = " + System.currentTimeMillis());
    }

    @Override
    public MediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri);
    }

    @Override
    public int[] getSupportedTypes() {
        // IMA does not support Smooth Streaming ads.
        return new int[]{C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER};
    }

    // Internal methods.

    private MediaSource buildMediaSource(Uri uri) {
        @ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        manifestDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), manifestDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                //raw
                if(uri.toString().startsWith(RawResourceDataSource.RAW_RESOURCE_SCHEME)){
                    return new ExtractorMediaSource.Factory(new DataSource.Factory() {
                        @Override
                        public DataSource createDataSource() {
                            return new RawResourceDataSource(getContext());
                        }
                    }).createMediaSource(uri);
                }
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

   private SimpleExoPlayer newPlayer(Context context, TrackSelector trackSelector) {
        DefaultRenderersFactory2 factory2 = new DefaultRenderersFactory2(context);
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, factory2, trackSelector, new DefaultLoadControl());
        //after player create. has video render now.
        factory2.getVideoRender().addCallback(callback);
        factory2.getAudioRender().addCallback(callback);
        return player;
    }

    @Override
    public void onLifeCycle(Context context, int lifeCycle) {
        switch (lifeCycle){
            case ON_PAUSE:
                onPause();
                break;

            case ON_RESUME:
                onResume();
                break;

            case ON_DESTROY:
                release();
                break;
        }
    }

    /*public*/ void seekToImpl(long position) {
        if(player != null){
            player.seekTo(position);
            Logger.d("ExoPlayerManager", "seekToImpl", "start seek: time = " + System.currentTimeMillis());
        }
    }

    boolean isReleased(){
        return player == null && uri == null;
    }

    public interface Callback extends CustomVideoRenderer.Callback, CustomAudioRender.Callback{
        Uri getFileUri(Context context, String file);
        default void onPrepared(Uri uri, boolean playWhenReady){}
        default void onAudioPlayTimeChanged(long positionUs){}
    }
}
