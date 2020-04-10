package com.heaven7.android.imagepick.pub;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

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
     * @param position the position
     * @param data the media data
     */
    void onBindItem(View v, int position, IImageItem data);
    /**
     * called on destroy video
     * @param v the video view. which is create by {@linkplain #createVideoView(Context, ViewGroup, IImageItem)}
     * @param position the position
     * @param data the media item
     */
    void onDestroyItem(View v, int position, IImageItem data);

    /**
     * called on set primary item
     * @param view the video view which is create by {@linkplain #createVideoView(Context, ViewGroup, IImageItem)}
     * @param position the real position
     * @param data the media data
     */
    void setPrimaryItem(View view, int position, IImageItem data);

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
