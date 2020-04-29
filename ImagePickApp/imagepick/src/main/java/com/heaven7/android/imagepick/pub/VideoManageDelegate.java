package com.heaven7.android.imagepick.pub;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.heaven7.android.imagepick.pub.module.IImageItem;

/**
 * the video manage delegate
 * @author heaven7
 * @since 1.0.5
 */
public interface VideoManageDelegate {

    /**
     * called on create the video view. you can bind click event here.
     *
     * @param context the context
     * @param parent the parent
     * @param data the media item
     * @return the view of video
     */
    View createVideoView(Context context, ViewGroup parent, IImageItem data);

    /**
     * called on set media data to video view by onBindItem.
     * @param v the video view which is create by {@linkplain #createVideoView(Context, ViewGroup, IImageItem)}
     * @param pos the position in view pager
     * @param realPos the real position
     * @param data the media data
     */
    void onBindItem(View v, int pos, int realPos, IImageItem data);
    /**
     * called on destroy video
     * @param v the video view. which is create by {@linkplain #createVideoView(Context, ViewGroup, IImageItem)}
     * @param pos the position
     * @param realPos the real position
     * @param data the media item
     */
    void onDestroyItem(View v, int pos, int realPos, IImageItem data);

    /**
     * called on attach---onCreate
     * @param activity the activity
     */
    void onAttach(Activity activity);

    /**
     * called on detach---onDestroy
     * @param activity the activity
     */
    void onDetach(Activity activity);
}
