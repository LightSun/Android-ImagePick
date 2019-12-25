package lib.vida.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.heaven7.android.pick.app.R;
import com.heaven7.core.util.Logger;

import static lib.vida.video.MediaViewCons.TYPE_VIDEO;

/**
 * the media player view .contains: videoView, coverView, pauseView.
 * Created by heaven7 on 2018/10/16 0016.
 */
public class MediaSdkPlayerView extends FrameLayout implements MediaViewDelegate.TouchPositionProvider, ProxyView.Callback {

    private MediaViewDelegate<MediaSdkPlayerView, GLSurfaceView> mDelegate;
    private Drawable mSplitScreenIcon;
    private float mLastX;
    private float mLastY;

    public MediaSdkPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaSdkPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public MediaSdkPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MediaSdkPlayerView);
        try {
            mSplitScreenIcon = a.getDrawable(R.styleable.MediaSdkPlayerView_mspv_split_screen_icon);
            if(mSplitScreenIcon != null){
                mSplitScreenIcon.setBounds(0, 0, mSplitScreenIcon.getIntrinsicWidth(), mSplitScreenIcon.getIntrinsicHeight());
            }
        }finally {
            a.recycle();
        }
        mDelegate = new MediaViewDelegate<MediaSdkPlayerView, GLSurfaceView>(this) {
            @Override
            protected GLSurfaceView createVideoView(Context context) {
                return new GLSurfaceView(context);
            }
        };
        mDelegate.initViews(true);
        mDelegate.getProxyView().setCallback(this);
        mDelegate.setTouchPositionProvider(this);
        //default is video
        mDelegate.showContent(TYPE_VIDEO);
    }
    public void setCallback(Callback mCallback) {
        mDelegate.setCallback(mCallback);
    }

    public void setPositionCallback(PositionCallback callback){
        mDelegate.setPositionCallback(callback);
    }

    public ImageView getPauseView(){
        return mDelegate.getPauseView();
    }
    public GLSurfaceView getVideoView(){
        return mDelegate.getVideoView();
    }
    public ImageView getCoverView(){
        return mDelegate.getCoverView();
    }

    public void showContent(@MediaViewCons.TypeDef int type){
        mDelegate.showContent(type);
    }
    public @MediaViewCons.TypeDef int getContentType(){
        return mDelegate.getContentType();
    }
    public void performClickType(){
        mDelegate.performClickType();
    }

    public boolean isPaused(){
        return mDelegate.isPaused();
    }

    public boolean isPlaying() {
        return getContentType() == TYPE_VIDEO;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return mDelegate.onSaveInstanceState(super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(mDelegate.onRestoreInstanceState(state));
    }

  /*  @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if(mSplitScreenIcon != null && child.getTag(R.id.key_pause) != null){
            //child is pause view
            MediaViewDelegate.PositionCallback<MediaSdkPlayerView> callback = mDelegate.getPositionCallback();
            if(callback != null){
                callback.onDrawSplitScreenIcon(this, canvas, mDelegate.getContentType(), mSplitScreenIcon);
            }
        }
        return super.drawChild(canvas, child, drawingTime);
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastX = ev.getRawX();
        mLastY = ev.getRawY();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public float getLastX() {
        return mLastX;
    }
    @Override
    public float getLastY() {
        return mLastY;
    }

    @Override
    public void onDraw(View view, Canvas canvas) {
        MediaViewDelegate.PositionCallback<MediaSdkPlayerView> callback = mDelegate.getPositionCallback();
        if(mSplitScreenIcon != null && callback != null){
            Logger.d("MediaSdkPlayerView", "onDraw", "onDrawSplitScreenIcon");
            callback.onDrawSplitScreenIcon(this, canvas, mDelegate.getContentType(), mSplitScreenIcon);
        }
    }

    public void setDrawProxyView(boolean draw) {
        Logger.d("MediaSdkView", "setDrawProxyView", "draw = " + draw);
        ProxyView view = mDelegate.getProxyView();
        view.setDraw(draw && mDelegate.getContentType() == MediaViewCons.TYPE_PAUSE);
    }

    public interface Callback extends MediaViewDelegate.Callback<MediaSdkPlayerView>{

    }
    public interface PositionCallback extends MediaViewDelegate.PositionCallback<MediaSdkPlayerView>{

    }

}
