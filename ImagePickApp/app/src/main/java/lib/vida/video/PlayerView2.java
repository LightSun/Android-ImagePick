package lib.vida.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.heaven7.android.savestate2.BundleStateWrapper;
import com.heaven7.android.savestate2.BundleSupportType;
import com.heaven7.android.savestate2.SaveStateField;

import java.util.List;

/**
 * Created by heaven7 on 2018/11/28 0028.
 */
public class PlayerView2 extends FrameLayout {

    private static final int SURFACE_TYPE_NONE = 0;
    /**
     * note: surface view may have a bug of over edge. texture view is good
     */
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;

    private final BundleStateWrapper mSaveStateWrapper;
    private final AspectRatioFrameLayout contentFrame;
    private final View surfaceView;
    private final PlayerControlLayout controller2;
    private final PlayerView2.ComponentListener componentListener;

    private Player player;
    @SaveStateField(value = "PlayerView2:textureViewRotation", flag = BundleSupportType.INT)
    private int textureViewRotation;

    public PlayerView2(Context context) {
        this(context, null);
    }
    public PlayerView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PlayerView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            mSaveStateWrapper = BundleStateWrapper.of(this);
            contentFrame = null;
            surfaceView = null;
            componentListener = null;
            controller2 = null;
            ImageView logo = new ImageView(context);
            if (Util.SDK_INT >= 23) {
                configureEditModeLogoV23(getResources(), logo);
            } else {
                configureEditModeLogo(getResources(), logo);
            }
            addView(logo);
            return;
        }

        int playerLayoutId = R.layout.view_player_view2;
        int surfaceType = SURFACE_TYPE_TEXTURE_VIEW;
        int resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        int pauseMarginTop = 0 ;
        int pauseMarginBottom = 0 ;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayerView2, 0, 0);
            try {
                playerLayoutId = a.getResourceId(R.styleable.PlayerView2_player_layout_id, playerLayoutId);
                surfaceType = a.getInt(R.styleable.PlayerView2_surface_type, surfaceType);
                resizeMode = a.getInt(R.styleable.PlayerView2_resize_mode, resizeMode);
                pauseMarginTop = a.getDimensionPixelSize(R.styleable.PlayerView2_pcv_pause_margin_top, pauseMarginTop);
                pauseMarginBottom = a.getDimensionPixelSize(R.styleable.PlayerView2_pcv_pause_margin_bottom, pauseMarginBottom);
            } finally {
                a.recycle();
            }
        }

        LayoutInflater.from(context).inflate(playerLayoutId, this);
        componentListener = new PlayerView2.ComponentListener();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        // Content frame.
        contentFrame = findViewById(com.google.android.exoplayer2.ui.R.id.exo_content_frame);
        if (contentFrame != null) {
            setResizeModeRaw(contentFrame, resizeMode);
        }
        // Create a surface view and insert it into the content frame, if there is one.
        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            surfaceView =
                    surfaceType == SURFACE_TYPE_TEXTURE_VIEW
                            ? new TextureView(context)
                            : new SurfaceView(context);
            surfaceView.setLayoutParams(params);
            contentFrame.addView(surfaceView, 0);
        } else {
            surfaceView = null;
        }
        // Shutter view.
        // Overlay frame layout.
        // Artwork view.
        // Subtitle view.
        // Playback control view.
        controller2 = findViewById(R.id.pcl);
        controller2.setPauseMargin(pauseMarginTop, pauseMarginBottom);
        mSaveStateWrapper = BundleStateWrapper.of(this, controller2);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return mSaveStateWrapper.onSaveInstanceState(super.onSaveInstanceState());
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(mSaveStateWrapper.onRestoreInstanceState(state));
    }

    /**
     * Switches the view targeted by a given {@link Player}.
     *
     * @param player The player whose target view is being switched.
     * @param oldPlayerView The old view to detach from the player.
     * @param newPlayerView The new view to attach to the player.
     */
    public static void switchTargetView(
            @NonNull Player player,
            @Nullable PlayerView2 oldPlayerView,
            @Nullable PlayerView2 newPlayerView) {
        if (oldPlayerView == newPlayerView) {
            return;
        }
        // We attach the new view before detaching the old one because this ordering allows the player
        // to swap directly from one surface to another, without transitioning through a state where no
        // surface is attached. This is significantly more efficient and achieves a more seamless
        // transition when using platform provided video decoders.
        if (newPlayerView != null) {
            newPlayerView.setPlayer(player);
        }
        if (oldPlayerView != null) {
            oldPlayerView.setPlayer(null);
        }
    }

    public void addControlCallback(PlayerControlLayout.Callback callback){
        controller2.addCallback(callback);
    }

    public void setState(@MediaViewCons.TypeDef int state){
        controller2.setState(state);
    }

    public int getState() {
        return controller2.getState();
    }

    public void disableClick(){
        controller2.addCallback(new PlayerControlLayout.DisableClickCallback());
    }

    public ImageView getCoverView(){
        return controller2.getCoverView();
    }

    /** Returns the player currently set on this view, or null if no player is set. */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the {@link Player} to use.
     *
     * <p>To transition a {@link Player} from targeting one view to another, it's recommended to use
     * {@link #switchTargetView(Player, PlayerView2, PlayerView2)} rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view <em>before</em>
     * calling {@code setPlayer(null)} to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The {@link Player} to use.
     */
    public void setPlayer(Player player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
            Player.VideoComponent oldVideoComponent = this.player.getVideoComponent();
            if (oldVideoComponent != null) {
                oldVideoComponent.removeVideoListener(componentListener);
                if (surfaceView instanceof TextureView) {
                    oldVideoComponent.clearVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof SurfaceView) {
                    oldVideoComponent.clearVideoSurfaceView((SurfaceView) surfaceView);
                }
            }
            Player.TextComponent oldTextComponent = this.player.getTextComponent();
            if (oldTextComponent != null) {
                oldTextComponent.removeTextOutput(componentListener);
            }
        }
        this.player = player;
        controller2.setPlayer(player);//add
        if (player != null) {
            Player.VideoComponent newVideoComponent = player.getVideoComponent();
            if (newVideoComponent != null) {
                if (surfaceView instanceof TextureView) {
                    newVideoComponent.setVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof SurfaceView) {
                    newVideoComponent.setVideoSurfaceView((SurfaceView) surfaceView);
                }
                newVideoComponent.addVideoListener(componentListener);
            }
            Player.TextComponent newTextComponent = player.getTextComponent();
            if (newTextComponent != null) {
                newTextComponent.addTextOutput(componentListener);
            }
            player.addListener(componentListener);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (surfaceView instanceof SurfaceView) {
            // Work around https://github.com/google/ExoPlayer/issues/3160.
            surfaceView.setVisibility(visibility);
        }
    }

    /**
     * Sets the resize mode.
     *
     * @param resizeMode The resize mode.
     */
    public void setResizeMode(@AspectRatioFrameLayout.ResizeMode int resizeMode) {
        Assertions.checkState(contentFrame != null);
        contentFrame.setResizeMode(resizeMode);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player != null && player.isPlayingAd()) {
            // Focus any overlay UI now, in case it's provided by a WebView whose contents may update
            // dynamically. This is needed to make the "Skip ad" button focused on Android TV when using
            // IMA [Internal: b/62371030].
            //overlayFrameLayout.requestFocus();
            return super.dispatchKeyEvent(event);
        }
        boolean isDpadWhenControlHidden = isDpadKey(event.getKeyCode());
        return isDpadWhenControlHidden || super.dispatchKeyEvent(event);
    }

    /**
     * Gets the view onto which video is rendered. This is a:
     *
     * <ul>
     *   <li>{@link SurfaceView} by default, or if the {@code surface_type} attribute is set to {@code
     *       surface_view}.
     *   <li>{@link TextureView} if {@code surface_type} is {@code texture_view}.
     *   <li>{@code null} if {@code surface_type} is {@code none}.
     * </ul>
     *
     * @return The {@link SurfaceView}, {@link TextureView} or {@code null}.
     */
    public View getVideoSurfaceView() {
        return surfaceView;
    }

  /*  @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (player == null) {
            return false;
        }
        return true;
    }*/
    private boolean shouldShowControllerIndefinitely() {
        if (player == null) {
            return true;
        }
        int playbackState = player.getPlaybackState();
        return (playbackState == Player.STATE_IDLE
                || playbackState == Player.STATE_ENDED
                || !player.getPlayWhenReady());
    }

    private boolean isPlayingAd() {
        return player != null && player.isPlayingAd() && player.getPlayWhenReady();
    }

    @TargetApi(23)
    private static void configureEditModeLogoV23(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(com.google.android.exoplayer2.ui.R.drawable.exo_edit_mode_logo, null));
        logo.setBackgroundColor(resources.getColor(com.google.android.exoplayer2.ui.R.color.exo_edit_mode_background_color, null));
    }

    @SuppressWarnings("deprecation")
    private static void configureEditModeLogo(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(com.google.android.exoplayer2.ui.R.drawable.exo_edit_mode_logo));
        logo.setBackgroundColor(resources.getColor(com.google.android.exoplayer2.ui.R.color.exo_edit_mode_background_color));
    }

    @SuppressWarnings("ResourceType")
    private static void setResizeModeRaw(AspectRatioFrameLayout aspectRatioFrame, int resizeMode) {
        aspectRatioFrame.setResizeMode(resizeMode);
    }

    /** Applies a texture rotation to a {@link TextureView}. */
    private static void applyTextureViewRotation(TextureView textureView, int textureViewRotation) {
        float textureViewWidth = textureView.getWidth();
        float textureViewHeight = textureView.getHeight();
        if (textureViewWidth == 0 || textureViewHeight == 0 || textureViewRotation == 0) {
            textureView.setTransform(null);
        } else {
            Matrix transformMatrix = new Matrix();
            float pivotX = textureViewWidth / 2;
            float pivotY = textureViewHeight / 2;
            transformMatrix.postRotate(textureViewRotation, pivotX, pivotY);

            // After rotation, scale the rotated texture to fit the TextureView size.
            RectF originalTextureRect = new RectF(0, 0, textureViewWidth, textureViewHeight);
            RectF rotatedTextureRect = new RectF();
            transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
            transformMatrix.postScale(
                    textureViewWidth / rotatedTextureRect.width(),
                    textureViewHeight / rotatedTextureRect.height(),
                    pivotX,
                    pivotY);
            textureView.setTransform(transformMatrix);
        }
    }

    @SuppressLint("InlinedApi")
    private boolean isDpadKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
    }

    private final class ComponentListener extends Player.DefaultEventListener
            implements TextOutput, VideoListener, OnLayoutChangeListener {

        // TextOutput implementation

        @Override
        public void onCues(List<Cue> cues) {
            /*if (subtitleView != null) {
                subtitleView.onCues(cues);
            }*/
        }

        // VideoListener implementation

        @Override
        public void onVideoSizeChanged(
                int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            if (contentFrame == null) {
                return;
            }
            float videoAspectRatio =
                    (height == 0 || width == 0) ? 1 : (width * pixelWidthHeightRatio) / height;

            if (surfaceView instanceof TextureView) {
                // Try to apply rotation transformation when our surface is a TextureView.
                if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                    // We will apply a rotation 90/270 degree to the output texture of the TextureView.
                    // In this case, the output video's width and height will be swapped.
                    videoAspectRatio = 1 / videoAspectRatio;
                }
                if (textureViewRotation != 0) {
                    surfaceView.removeOnLayoutChangeListener(this);
                }
                textureViewRotation = unappliedRotationDegrees;
                if (textureViewRotation != 0) {
                    // The texture view's dimensions might be changed after layout step.
                    // So add an OnLayoutChangeListener to apply rotation after layout step.
                    surfaceView.addOnLayoutChangeListener(this);
                }
                applyTextureViewRotation((TextureView) surfaceView, textureViewRotation);
            }

            contentFrame.setAspectRatio(videoAspectRatio);
        }

        @Override
        public void onRenderedFirstFrame() {
          /*  if (shutterView != null) {
                shutterView.setVisibility(INVISIBLE);
            }*/
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
          //  updateForCurrentTrackSelections();
        }

        // Player.EventListener implementation

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
        }

        // OnLayoutChangeListener implementation

        @Override
        public void onLayoutChange(
                View view,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
            applyTextureViewRotation((TextureView) view, textureViewRotation);
        }
    }

}
