package lib.vida.video;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.concurrent.CopyOnWriteArraySet;

/*public*/ class CustomVideoRenderer extends MediaCodecVideoRenderer {

    //private static final int RENDERER_TIMESTAMP_OFFSET_US = 60000000;
    private final CopyOnWriteArraySet<Callback> mCallbacks = new CopyOnWriteArraySet<>();

    public CustomVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
        super(context, mediaCodecSelector);
    }

    public CustomVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs) {
        super(context, mediaCodecSelector, allowedJoiningTimeMs);
    }

    public CustomVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFrameCountToNotify) {
        super(context, mediaCodecSelector, allowedJoiningTimeMs, eventHandler, eventListener, maxDroppedFrameCountToNotify);
    }

    public CustomVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        super(context, mediaCodecSelector, allowedJoiningTimeMs, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, maxDroppedFramesToNotify);
    }

    @Override
    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        super.render(positionUs, elapsedRealtimeUs);
        //for offset
        //positionUs -= RENDERER_TIMESTAMP_OFFSET_US;
        //note: if play multi video. the time is piled
        for (Callback callback : mCallbacks) {
            callback.onPlayTimeChanged(positionUs);
        }
    }

    public void addCallback(Callback listener) {
        this.mCallbacks.add(listener);
    }

    public interface Callback {
        void onPlayTimeChanged(long positionUs);
    }
}