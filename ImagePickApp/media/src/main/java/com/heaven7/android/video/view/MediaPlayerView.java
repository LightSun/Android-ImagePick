package com.heaven7.android.video.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.heaven7.android.video.MediaViewCons;
import com.heaven7.android.video.MediaViewDelegate;
import com.heaven7.android.video.R;
import com.heaven7.android.video.impl.DefaultMediaViewDelegate;
import com.heaven7.java.base.anno.Nullable;

import java.lang.reflect.Constructor;

import static com.heaven7.android.video.MediaViewCons.TYPE_VIDEO;

/**
 * the media player view .contains: videoView, coverView, pauseView.
 * Created by heaven7 on 2018/10/16 0016.
 */
public class MediaPlayerView extends FrameLayout {

    private MediaViewDelegate<MediaPlayerView, TextureView> mDelegate;

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("unchecked")
    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       // setKeepScreenOn(true);

        String dname;
        boolean needProxy = false;
        Drawable pauseIcon;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MediaPlayerView);
        try {
            dname = a.getString(R.styleable.MediaPlayerView_media_mpv_delegate);
            //set default
            if(dname == null){
                dname = DefaultMediaViewDelegate.class.getName();
            }
            needProxy = a.getBoolean(R.styleable.MediaPlayerView_media_mpv_need_proxy, needProxy);
            pauseIcon = a.getDrawable(R.styleable.MediaPlayerView_media_mpv_pause_icon);
            if(pauseIcon != null){
                pauseIcon.setBounds(0, 0 ,pauseIcon.getIntrinsicWidth(), pauseIcon.getIntrinsicHeight());
            }
        }finally {
            a.recycle();
        }
        try {
            Constructor<?> cons = Class.forName(dname).getConstructor(ViewGroup.class);
            mDelegate = (MediaViewDelegate<MediaPlayerView, TextureView>) cons.newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mDelegate.initViews(needProxy);
        mDelegate.getPauseView().setImageDrawable(pauseIcon);
        //default is video
        showContent(TYPE_VIDEO);
    }

    public MediaViewDelegate<MediaPlayerView, TextureView> getDelegate(){
        return mDelegate;
    }
    public void setCallback(Callback mCallback) {
        mDelegate.setCallback(mCallback);
    }
    public ProxyView getProxyView(){
        return mDelegate.getProxyView();
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
