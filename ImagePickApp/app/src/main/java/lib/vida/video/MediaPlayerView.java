package lib.vida.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.heaven7.java.base.anno.Nullable;

import static lib.vida.video.MediaViewCons.TYPE_VIDEO;

/**
 * the media player view .contains: videoView, coverView, pauseView.
 * Created by heaven7 on 2018/10/16 0016.
 */
public class MediaPlayerView extends FrameLayout {

    private MediaViewDelegate<MediaPlayerView, TextureView> mDelegate;

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setKeepScreenOn(true);

        mDelegate = new MediaViewDelegate<MediaPlayerView, TextureView>(this) {
            @Override
            protected TextureView createVideoView(Context context) {
                return new TextureView(context);
            }
        };
        mDelegate.initViews(false);
        //default is video
        showContent(TYPE_VIDEO);
    }
/*
    private void setTransformInternal(int dstWidth, int dstHeight) {
        Matrix m = new Matrix();
        final int width = mVideoWidth;
        final int height = mVideoHeight;
        if (width != dstWidth || height != dstHeight) {
            final float sx = dstWidth  * 1f/  width;
            final float sy = dstHeight * 1f/  height;
            Logger.d("MediaPlayerView", "setTransformInternal", "sx = " + sx + " ,sy = " + sy);
            float scale = Math.max(sx, sy);
            if(scale < 1f){
                scale = 1f;
            }
            m.setScale(scale, scale);
            getVideoView().setTransform(m);
        }
    }*/

    public void setCallback(Callback mCallback) {
        mDelegate.setCallback(mCallback);
    }

    public ImageView getPauseView(){
        return mDelegate.getPauseView();
    }
    public TextureView getVideoView(){
        return mDelegate.getVideoView();
    }
    public ImageView getCoverView(){
        return mDelegate.getCoverView();
    }

    public void performClickType(){
        mDelegate.performClickType();
    }

    public void showContent(@MediaViewCons.TypeDef int type){
        mDelegate.showContent(type);
    }

    public @MediaViewCons.TypeDef int getContentType(){
        return mDelegate.getContentType();
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

    public interface Callback extends MediaViewDelegate.Callback<MediaPlayerView>{
    }

}
