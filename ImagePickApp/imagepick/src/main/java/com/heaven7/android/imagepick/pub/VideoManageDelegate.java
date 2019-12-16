package com.heaven7.android.imagepick.pub;

import android.content.Context;
import android.view.View;

/**
 * the video manage delegate
 * @author heaven7
 * @since 1.0.5
 */
public interface VideoManageDelegate {

    /**
     * indicate the view is video view or not
     * @param view the view
     * @param data the media data
     * @return true if is video view.
     */
    boolean isVideoView(View view, IImageItem data);

    /**
     * called on create video video
     * @param context the context
     * @param data the media item
     * @return the view of video
     */
    View createVideoView(Context context, IImageItem data);

    /**
     * called on set media data to video view.
     * @param v the video view which is create by {@linkplain #createVideoView(Context, IImageItem)}
     * @param context the context
     * @param data the media data
     */
    void setMediaData(Context context, View v, IImageItem data);

    /**
     * called on pause video
     * @param context the context
     * @param v the video view
     */
    void pauseVideo(Context context, View v);

    /**
     * called on resume video
     * @param context the context
     * @param v the video view
     */
    void resumeVideo(Context context, View v);

    /**
     * called on destroy video
     * @param context the context
     *  @param v the video view
     */
    void destroyVideo(Context context, View v);

    /**
     * called on start play video.
     * @param v the video view which is create by {@linkplain #createVideoView(Context, IImageItem)}
     * @param context the context
     * @param data the media data
     */
    void startPlay(Context context, View v, IImageItem data);
}
