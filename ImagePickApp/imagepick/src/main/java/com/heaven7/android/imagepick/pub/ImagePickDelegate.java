package com.heaven7.android.imagepick.pub;

import android.app.Activity;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.heaven7.android.imagepick.pub.delegate.SeeBigImageDelegate;
import com.heaven7.android.imagepick.pub.delegate.SeeImageDelegate;

import java.util.List;

/**
 * image pick delegate
 * @author heaven7
 */
public interface ImagePickDelegate {

    /**
     * set OnPageChangeListener which is used for {@linkplain com.heaven7.android.imagepick.SeeBigImageActivity}
     * @param l OnPageChangeListener
     * @since 1.0.5
     */
    void setOnPageChangeListener(ViewPager.OnPageChangeListener l);
    /**
     * get OnPageChangeListener
     * @since 1.0.5
     */
    ViewPager.OnPageChangeListener getOnPageChangeListener();
    /**
     * set video manage delegate
     * @param delegate the image delegate
     * @since 1.0.5
     */
    void setImageLoadDelegate(ImageLoadDelegate delegate);
    /**
     * get image delegate
     * @since 1.0.5
     */
    ImageLoadDelegate getImageLoadDelegate();
    /**
     * set video manage delegate
     * @param delegate the video manage delegate
     * @since 1.0.5
     */
    void setVideoManageDelegate(VideoManageDelegate delegate);
    /**
     * get video manage delegate
     * @return video manage delegate
     * @since 1.0.5
     */
    VideoManageDelegate getVideoManageDelegate();
    /**
     * void set exception handler
     * @param handler the exception handler
     */
    void setExceptionHandler(ExceptionHandler handler);

    /**
     * void set exception handler
     */
    ExceptionHandler getExceptionHandler();
    /**
     * set on image process listener
     * @param l the dialog delegate
     * @since 1.0.2
     */
    void setOnImageProcessListener(OnImageProcessListener l);

    /**
     * get on image process listener
     * @return image process listener
     */
    OnImageProcessListener getOnImageProcessListener();

    /**
     * add select state change listener
     * @param l the listener
     */
    void addOnSelectStateChangedListener(OnSelectStateChangedListener l);
    /**
     * remove select state change listener
     * @param l the listener
     */
    void removeOnSelectStateChangedListener(OnSelectStateChangedListener l);

    /**
     * start camera with default parameter. for more see {@linkplain #startCamera(Activity, CameraParameter)}.
     * @param context the context
     */
    void startCamera(Activity context);

    /**
     * start camera
     * @param context the context
     * @param parameter the camera param
     * @since 1.0.1
     */
    void startCamera(Activity context, CameraParameter parameter);

    /**
     * start browse images. used by {@linkplain com.heaven7.android.imagepick.ImageSelectActivity}.
     * @param context the context
     * @param param the image select parameter
     */
    void startBrowseImages(Activity context, ImageSelectParameter param);

    /**
     *  start browse/see images. used by {@linkplain com.heaven7.android.imagepick.SeeImageActivity}.
     * @param context the activity
     * @param parameter the parameter
     */
    void startBrowseImages2(Activity context, SeeImageParameter parameter);

    /**
     *  start browse/see images. used by {@linkplain com.heaven7.android.imagepick.SeeImageActivity}.
     * @param context the activity
     * @param clazz the delegate class
     * @param parameter the parameter
     * @param extra the extras
     */
    void startBrowseImages2(Activity context, Class<? extends SeeImageDelegate> clazz, SeeImageParameter parameter, Bundle extra);

    /**
     * start browse big images/videos with a default delegate of {@linkplain SeeBigImageDelegate}.
     * @param context the context
     * @param param the parameter of ui
     * @param allItems the items to browse
     * @param single the single selected item.can be null for multi-select it must be null.
     */
    void startBrowseBigImages(Activity context, BigImageSelectParameter param, List<? extends IImageItem> allItems, IImageItem single);

    /**
     * start browse big images/videos by target delegate and select params.
     * @param context the context
     * @param param the parameter of ui
     * @param clazz the class of {@linkplain SeeBigImageDelegate}
     * @param extra the extra ot intent
     * @param allItems the items to browse
     * @param single the single selected item.can be null for multi-select it must be null.
     * @since 1.0.5
     */
    void startBrowseBigImages(Activity context, BigImageSelectParameter param, Class<? extends SeeBigImageDelegate> clazz, Bundle extra,
                              List<? extends IImageItem> allItems, IImageItem single);
    /**
     * the dialog delegate used to helpful handle image.
     * @since 1.0.3
     */
    interface OnImageProcessListener{

        /**
         * called on processing start.
         * @param activity the activity
         * @param totalCount the total task count , 0 means not care about it. like camera.
         */
        void onProcessStart(Activity activity, int totalCount);
        /**
         * called on processing image
         * @param activity the activity
         * @param finishCount finished task count
         * @param totalCount the total task count
         */
        void onProcessUpdate(Activity activity, int finishCount, int totalCount);
        /**
         * called on processing end
         * @param next the task used to do next. this is helpful for animate dialog
         */
        void onProcessEnd(Runnable next);
        /**
         * called on image process image exception
         * @param activity the activity
         * @param order the order of task index. start from 1.
         * @param size the task count
         * @param e the exception of handle image process. may be null. like image mime not support to scale.
         * @return true if you handled this exception and want to continue processing
         */
        boolean onProcessException(Activity activity, int order, int size, MediaResourceItem item, Exception e);
    }
    /**
     * on select state change listener
     */
    interface OnSelectStateChangedListener{
        /**
         * called on select state changed
         * @param item the image item
         * @param select true if select
         */
        void onSelectStateChanged(IImageItem item, boolean select);
    }
}
