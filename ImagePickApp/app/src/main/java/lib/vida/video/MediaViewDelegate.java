package lib.vida.video;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.heaven7.android.pick.app.R;

import static lib.vida.video.MediaViewCons.TYPE_COVER;
import static lib.vida.video.MediaViewCons.TYPE_COVER_PAUSE;
import static lib.vida.video.MediaViewCons.TYPE_PAUSE;
import static lib.vida.video.MediaViewCons.TYPE_VIDEO;

/**
 * the media view delegate.
 * @param <P> the parent view type.
 * @param <V> the video view type
 * Created by heaven7 on 2018/11/9 0009.
 */
/*public*/ abstract class MediaViewDelegate<P extends ViewGroup,V extends View> {

    private final P mParent;
    private V mVideoView;
    private AppCompatImageView mCoverView;
    private AppCompatImageView mPauseView;
    private ProxyView mProxyView;

    private int type;
    private Callback<P> mCallback;
    private TouchPositionProvider mProvider;
    private PositionCallback<P> mPositionCallback;

    /*public*/ MediaViewDelegate(P parent) {
        this.mParent = parent;
    }

    public void initViews(boolean addProxyView){
        Context context = mParent.getContext();
        mVideoView = createVideoView(context);
        mVideoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mParent.addView(mVideoView);

        mCoverView = new AppCompatImageView(context);
        mCoverView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mCoverView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mParent.addView(mCoverView);

        if(addProxyView){
            mProxyView = new ProxyView(context);
            mProxyView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mParent.addView(mProxyView);
        }

        mPauseView = new AppCompatImageView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mPauseView.setLayoutParams(lp);
        mParent.addView(mPauseView);
        //set default res
        mPauseView.setImageResource(R.drawable.ic_video_pause);

        //set click listener
        mParent.setOnClickListener(v -> performClickType(type, true));
    }
    public void setCallback(Callback<P> callback){
        this.mCallback = callback;
    }
    public Callback<P> getCallback() {
        return mCallback;
    }

    public ProxyView getProxyView() {
        return mProxyView;
    }
    public void setTouchPositionProvider(TouchPositionProvider provider){
        this.mProvider = provider;
    }
    public void setPositionCallback(PositionCallback<P> callback){
        this.mPositionCallback = callback;
    }
    public PositionCallback<P> getPositionCallback() {
        return mPositionCallback;
    }

    public ImageView getPauseView(){
        return mPauseView;
    }
    public V getVideoView(){
        return mVideoView;
    }
    public ImageView getCoverView(){
        return mCoverView;
    }
    public void performClickType(){
        performClickType(type, false);
    }

    private void performClickType(@MediaViewCons.TypeDef int type, boolean checkPosition){
        //handle position
        if(checkPosition && mProvider != null && mPositionCallback != null){
            float lastX = mProvider.getLastX();
            float lastY = mProvider.getLastY();
            int[] pxy = getScreenXY(mParent);
            float dx = (lastX - pxy[0]) / mParent.getWidth();
            float dy = (lastY - pxy[1]) / mParent.getHeight();
            if(type == TYPE_PAUSE){
                //if clicked paused.
                int[] xy = getScreenXY(mPauseView);
                //pause view position relative to parent
                float dx_pause = (xy[0] - pxy[0]) * 1f / mParent.getWidth();
                float dy_pause = (xy[1] - pxy[1]) * 1f / mParent.getHeight();
                float dx_pause_max = dx_pause + mPauseView.getWidth() * 1f /  mParent.getWidth();
                float dy_pause_max = dy_pause + mPauseView.getHeight() * 1f /  mParent.getHeight();
                if(dx >= dx_pause && dx <= dx_pause_max && dy >= dy_pause && dy <= dy_pause_max){
                     //pause was clicked
                    performClickType(TYPE_PAUSE, false);
                    return;
                }
            }
            if(mPositionCallback.handleClick(mParent, type, dx, dy)){
                return;
            }
        }
        switch (type){
            case TYPE_VIDEO:
                showContent(TYPE_PAUSE);
                mCallback.pauseVideo(mParent);
                break;

            case TYPE_COVER:
                mCallback.onClickCover(mParent);
                break;

            case TYPE_COVER_PAUSE:
            case TYPE_PAUSE:
                showContent(TYPE_VIDEO);
                mCallback.resumeVideo(mParent);
                break;
        }
    }

    public boolean isPaused(){
        return getContentType() == TYPE_PAUSE;
    }

    public void showContent(@MediaViewCons.TypeDef int type){
        switch (type){
            case TYPE_VIDEO:
                mPauseView.setVisibility(View.GONE);
                mCoverView.setVisibility(View.INVISIBLE);
                if(mProxyView != null){
                    mProxyView.setVisibility(View.GONE);
                }
                break;

            case TYPE_COVER:
                mCoverView.setVisibility(View.VISIBLE);
                mPauseView.setVisibility(View.GONE);
                if(mProxyView != null){
                    mProxyView.setVisibility(View.GONE);
                }
                break;

            case TYPE_PAUSE:
                mPauseView.setVisibility(View.VISIBLE);
                mCoverView.setVisibility(View.INVISIBLE);
                if(mProxyView != null){
                    mProxyView.setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_COVER_PAUSE:
                mCoverView.setVisibility(View.VISIBLE);
                mPauseView.setVisibility(View.VISIBLE);
                if(mProxyView != null){
                    mProxyView.setVisibility(View.GONE);
                }
                break;

            default:
                throw new UnsupportedOperationException("type = " + type);
        }
        this.type = type;
    }

    public @MediaViewCons.TypeDef int getContentType(){
        return type;
    }

    protected abstract V createVideoView(Context context);

    private static int[] getScreenXY(View view) {
        int[] cors = new int[2];
        view.getLocationOnScreen(cors);
        return cors;
    }

    public Parcelable onSaveInstanceState(Parcelable state) {
        return state;
    }

    public Parcelable onRestoreInstanceState(Parcelable state) {
        return state;
    }

    public interface Callback<T extends ViewGroup>{

        /** called on click video view, and attempt to pause video.  */
        void pauseVideo(T mpv);
        /** called on click pause view, and attempt to resume video.  */
        void resumeVideo(T mpv);
        /** called on click cover  */
        default void onClickCover(T mpv){}
    }

    /**
     * the touch position provider which provide the last touched positions.
     */
    public interface TouchPositionProvider {
        /**
         * get the last x position on screen. origin position is the Left-Top.
         * @return the last x . [ 0, 1]
         */
        float getLastX();
        /**
         * get the last y position on screen. origin position is the Left-Top.
         * @return the last y . [ 0, 1]
         */
        float getLastY();
    }

    /**
     * the position callback. which give a chance to do with click . such as handle range click .
     * @param <P> the parent view
     */
    public interface PositionCallback<P extends ViewGroup>{
        /**
         * called on click view.
         * @param parent the media controller view
         * @param curMediaType the current media type. see {@linkplain MediaViewCons#TYPE_VIDEO} and etc.
         * @param dx the last dx of touch position [0,1]
         * @param dy the last dy position of touch position [0,1]
         * @return true if this click is handled
         */
        boolean handleClick(P parent, int curMediaType, float dx, float dy);

        /**
         * called on draw split screen icon
         * @param playerView the media sdk view
         * @param canvas the canvas to draw
         * @param contentType the content type of media
         * @param icon the drawable.
         */
        void onDrawSplitScreenIcon(MediaSdkPlayerView playerView, Canvas canvas, int contentType, Drawable icon);
    }
}
