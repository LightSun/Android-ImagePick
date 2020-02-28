package lib.vida.video;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.heaven7.java.base.anno.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Created by heaven7 on 2018/10/12 0012.
 */
/*public*/ class DefaultRenderersFactory2 extends DefaultRenderersFactory {

    private static final String TAG = "RenderersFactory2";
    private CustomVideoRenderer videoRender;
    private CustomAudioRender mAudioRender;

    public DefaultRenderersFactory2(Context context) {
        super(context);
    }

    public DefaultRenderersFactory2(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        super(context, drmSessionManager);
    }

    public DefaultRenderersFactory2(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode) {
        super(context, drmSessionManager, extensionRendererMode);
    }

    public DefaultRenderersFactory2(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                    int extensionRendererMode, long allowedVideoJoiningTimeMs) {
        super(context, drmSessionManager, extensionRendererMode, allowedVideoJoiningTimeMs);
    }

    public CustomVideoRenderer getVideoRender(){
        return this.videoRender;
    }
    public CustomAudioRender getAudioRender() {
        return mAudioRender;
    }
    @Override
    protected void buildAudioRenderers(Context context,
                                       @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                       AudioProcessor[] audioProcessors, Handler eventHandler,
                                       AudioRendererEventListener eventListener, @ExtensionRendererMode int extensionRendererMode,
                                       ArrayList<Renderer> out) {
        CustomAudioRender audioRender = new CustomAudioRender(
                context,
                MediaCodecSelector.DEFAULT,
                drmSessionManager,
                /* playClearSamplesWithoutKeys= */ false,
                eventHandler,
                eventListener,
                AudioCapabilities.getCapabilities(context),
                audioProcessors);
        mAudioRender = audioRender;
        out.add(audioRender);

        if (extensionRendererMode == EXTENSION_RENDERER_MODE_OFF) {
            return;
        }
        int extensionRendererIndex = out.size();
        if (extensionRendererMode == EXTENSION_RENDERER_MODE_PREFER) {
            extensionRendererIndex--;
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            Handler.class,
                            com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
                            com.google.android.exoplayer2.audio.AudioProcessor[].class);
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            Renderer renderer =
                    (Renderer) constructor.newInstance(eventHandler, eventListener, audioProcessors);
            out.add(extensionRendererIndex++, renderer);
            com.google.android.exoplayer2.util.Log.i(TAG, "Loaded LibopusAudioRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating Opus extension", e);
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            Handler.class,
                            com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
                            com.google.android.exoplayer2.audio.AudioProcessor[].class);
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            Renderer renderer =
                    (Renderer) constructor.newInstance(eventHandler, eventListener, audioProcessors);
            out.add(extensionRendererIndex++, renderer);
            com.google.android.exoplayer2.util.Log.i(TAG, "Loaded LibflacAudioRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating FLAC extension", e);
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            Class<?> clazz =
                    Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            Handler.class,
                            com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
                            com.google.android.exoplayer2.audio.AudioProcessor[].class);
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            Renderer renderer =
                    (Renderer) constructor.newInstance(eventHandler, eventListener, audioProcessors);
            out.add(extensionRendererIndex++, renderer);
            com.google.android.exoplayer2.util.Log.i(TAG, "Loaded FfmpegAudioRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating FFmpeg extension", e);
        }
    }

    @Override
    protected void buildVideoRenderers(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                       long allowedVideoJoiningTimeMs, Handler eventHandler, VideoRendererEventListener eventListener,
                                       int extensionRendererMode, ArrayList<Renderer> out) {
        CustomVideoRenderer videoRenderer = new CustomVideoRenderer(context, MediaCodecSelector.DEFAULT,
                allowedVideoJoiningTimeMs, drmSessionManager, false, eventHandler, eventListener,
                MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY);
        this.videoRender = videoRenderer;
        out.add(videoRenderer);

        if (extensionRendererMode == EXTENSION_RENDERER_MODE_OFF) {
            return;
        }
        int extensionRendererIndex = out.size();
        if (extensionRendererMode == EXTENSION_RENDERER_MODE_PREFER) {
            extensionRendererIndex--;
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            boolean.class,
                            long.class,
                            Handler.class,
                            VideoRendererEventListener.class,
                            int.class);
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            Renderer renderer =
                    (Renderer)
                            constructor.newInstance(
                                    true,
                                    allowedVideoJoiningTimeMs,
                                    eventHandler,
                                    eventListener,
                                    MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY);
            out.add(extensionRendererIndex++, renderer);
            Log.i(TAG, "Loaded LibvpxVideoRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating VP9 extension", e);
        }
    }
}
