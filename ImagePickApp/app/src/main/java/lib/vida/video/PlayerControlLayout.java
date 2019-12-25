package lib.vida.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.TimeBar;
import com.heaven7.android.pick.app.R;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by heaven7 on 2018/11/28 0028.
 */
public class PlayerControlLayout extends FrameLayout {

    public static final int STATE_PAUSE = MediaViewCons.TYPE_PAUSE;
    public static final int STATE_VIDEO = MediaViewCons.TYPE_VIDEO;
    public static final int STATE_COVER = MediaViewCons.TYPE_COVER;
    public static final int STATE_COVER_PAUSE = MediaViewCons.TYPE_COVER_PAUSE;

    private final ImageView mPauseView;
    private final ImageView mCoverView;

    private final ComponentListener mListener = new ComponentListener();
    private final Set<Callback> mCallbacks = new CopyOnWriteArraySet<>();
    private int state = -1;
    private Player player;

    public PlayerControlLayout(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    public PlayerControlLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public PlayerControlLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PlayerControlLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        ClickListenerImpl mClickListener = new ClickListenerImpl();
        LayoutInflater.from(context).inflate(R.layout.view_player_control, this);
        mCoverView = findViewById(R.id.iv_cover);
        mCoverView.setOnClickListener(mClickListener);
        mCoverView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mCoverView.setVisibility(GONE);

        mPauseView = findViewById(R.id.iv_pause);
        mPauseView.setOnClickListener(mClickListener);
        mPauseView.setVisibility(GONE);
        this.setOnClickListener(mClickListener);
    }

    public void setPauseMargin(int top, int bottom){
        //margin with layout_center : order center then margin
        MarginLayoutParams mlp = (MarginLayoutParams) mPauseView.getLayoutParams();
        mlp.topMargin = top / 2 ;
        mlp.bottomMargin = bottom / 2; // for margin
    }

    public void setPlayer(Player player){
        if(this.player == player){
            return;
        }
        if(this.player != null){
            this.player.removeListener(mListener);
        }
        this.player = player;
        if(player != null) {
            player.addListener(mListener);
        }
    }

    public ImageView getCoverView() {
        return mCoverView;
    }

    public void addCallback(Callback callback){
        mCallbacks.add(callback);
    }

    public int getState() {
        return state;
    }

    public void setState(@MediaViewCons.TypeDef int state){
        if(this.state != state){
            final int old = this.state;
            this.state = state;
            switch (state){
                case STATE_PAUSE:
                     mPauseView.setVisibility(VISIBLE);
                     mCoverView.setVisibility(GONE);
                     if(player != null){
                         player.setPlayWhenReady(false);
                     }
                    break;

                case STATE_VIDEO:
                    mPauseView.setVisibility(GONE);
                    mCoverView.setVisibility(GONE);
                    if(player != null){
                        player.setPlayWhenReady(true);
                    }
                    break;

                case STATE_COVER:
                    mPauseView.setVisibility(GONE);
                    mCoverView.setVisibility(VISIBLE);
                    if(player != null){
                        player.setPlayWhenReady(false);
                    }
                    break;

                case STATE_COVER_PAUSE:
                    mPauseView.setVisibility(VISIBLE);
                    mCoverView.setVisibility(VISIBLE);
                    if(player != null){
                        player.setPlayWhenReady(false);
                    }
                    break;
            }
            for (Callback callback : mCallbacks){
                callback.onStateChanged(this, old, state);
            }
        }
    }

    private boolean dispatchClickPause(){
        for (Callback callback : mCallbacks){
            if(callback.dispatchClickPause(this)){
                return true;
            }
        }
        return false;
    }
    private boolean dispatchClickControl(){
        for (Callback callback : mCallbacks){
            if(callback.dispatchClickControl(this)){
                return true;
            }
        }
        return false;
    }
    private boolean dispatchClickCover(){
        for (Callback callback : mCallbacks){
            if(callback.dispatchClickCover(this)){
                return true;
            }
        }
        return false;
    }

    public interface Callback{
        default void onStateChanged(PlayerControlLayout layout, int oldState, int newState){}
        default boolean dispatchClickPause(PlayerControlLayout layout){return false;}
        default boolean dispatchClickControl(PlayerControlLayout layout){return false;}
        default boolean dispatchClickCover(PlayerControlLayout layout){return false;}
    }

    public static class DisableClickCallback implements Callback{

        public boolean dispatchClickPause(PlayerControlLayout layout){
            return true;
        }
        public boolean dispatchClickControl(PlayerControlLayout layout){
            return true;
        }
        public boolean dispatchClickCover(PlayerControlLayout layout){
            return true;
        }
    }

    private class ClickListenerImpl implements OnClickListener{
        @Override
        public void onClick(View v) {
            if(v == mPauseView){
                if(!dispatchClickPause()){
                    setState(STATE_VIDEO);
                }
            }else if(v == mCoverView){
                dispatchClickCover();
            }else if( v == PlayerControlLayout.this){
                if(dispatchClickControl()){
                    return;
                }
                switch (state){
                    case -1:
                    case STATE_VIDEO:
                        setState(STATE_PAUSE);
                        break;

                    case STATE_PAUSE:
                        setState(STATE_VIDEO);
                        break;

                    case STATE_COVER:
                        if(player != null){
                            setState(STATE_VIDEO);
                        }
                        break;
                }
            }
        }
    }

    private final class ComponentListener implements Player.EventListener, TimeBar.OnScrubListener{

        @Override
        public void onScrubStart(TimeBar timeBar, long position) {
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
             //setState(playWhenReady ? STATE_VIDEO : STATE_PAUSE);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
        }

        @Override
        public void onTimelineChanged(
                Timeline timeline, Object manifest, @Player.TimelineChangeReason int reason) {
        }
    }
}
