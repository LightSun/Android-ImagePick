package com.heaven7.android.imagepick.pub;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * the video manage delegate
 * @author heaven7
 * @since 1.0.5
 */
public interface VideoManageDelegate {

    /**
     * called on create video video
     *
     * @param context the context
     * @param parent the parent
     * @param data the media item
     * @return the view of video
     */
    View createVideoView(Context context, ViewGroup parent, IImageItem data);

    /**
     * called on set media data to video view by onBindItem.
     * @param context the context
     * @param v the video view which is create by {@linkplain #createVideoView(Context, ViewGroup, IImageItem)}
     * @param position the position
     * @param data the media data
     */
    void setMediaData(Context context, View v, int position, IImageItem data);

    /**
     * called on pause video
     * @param context the context
     * @param pos the position
     * @param v the video view
     */
    void pauseVideo(Context context, int pos, View v);

    /**
     * called on resume video
     * @param context the context
     * @param position the position
     * @param v the video view
     */
    void resumeVideo(Context context, int position, View v);

    /**
     * called on destroy video
     * @param context the context
     *  @param position the position
     * @param v the video view
     */
    void destroyVideo(Context context, int position, View v);

    /**
     * called on activity destroy
     * @param context the context
     * @param position the position
     * @param view the view
     */
    void releaseVideo(Context context, int position, View view);

    /**
     * called on set primary item, this is often used to start play video.
     * @param view the video view which is create by {@linkplain #createVideoView(Context, ViewGroup, IImageItem)}
     * @param actualPosition the real position of view pager
     * @param data the media data
     * @since 1.0.5
     */
    void setPrimaryItem(View view, int actualPosition, IImageItem data);

    /**
     * same as {@linkplain androidx.viewpager.widget.ViewPager#setCurrentItem(int)}.
     * @param position the position
     * @since 1.0.5
     */
    void setCurrentPosition(int position);

}
