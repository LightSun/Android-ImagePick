package lib.vida.video;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by heaven7 on 2019/5/16.
 */
/*public*/ class CustomAudioRender extends MediaCodecAudioRenderer {

    private final CopyOnWriteArraySet<Callback> mCallbacks = new CopyOnWriteArraySet<>();

    public CustomAudioRender(Context context, MediaCodecSelector mediaCodecSelector) {
        super(context, mediaCodecSelector);
    }

    public CustomAudioRender(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                             boolean playClearSamplesWithoutKeys) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys);
    }

    public CustomAudioRender(Context context, MediaCodecSelector mediaCodecSelector, @Nullable Handler eventHandler,
                             @Nullable AudioRendererEventListener eventListener) {
        super(context, mediaCodecSelector, eventHandler, eventListener);
    }

    public CustomAudioRender(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                             boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener);
    }

    public CustomAudioRender(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                             boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, @Nullable AudioCapabilities audioCapabilities, AudioProcessor... audioProcessors) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, audioCapabilities, audioProcessors);
    }

    public CustomAudioRender(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                             boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener,
                             AudioSink audioSink) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, audioSink);
    }
    public void addCallback(Callback listener) {
        this.mCallbacks.add(listener);
    }

    @Override
    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        super.render(positionUs, elapsedRealtimeUs);
        //for offset
        //positionUs -= RENDERER_TIMESTAMP_OFFSET_US;
        //note: if play multi video. the time is piled
        for (Callback callback : mCallbacks) {
            callback.onAudioPlayTimeChanged(positionUs);
        }
    }

    public interface Callback {
        void onAudioPlayTimeChanged(long positionUs);
    }
}
