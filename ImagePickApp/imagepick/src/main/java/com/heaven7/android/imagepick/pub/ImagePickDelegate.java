package com.heaven7.android.imagepick.pub;

import android.app.Activity;

import java.util.List;

/**
 * image pick delegate
 * @author heaven7
 */
public interface ImagePickDelegate {

    /**
     * set dialog delegate
     * @param dd the dialog delegate
     * @since 1.0.2
     */
    void setDialogDelegate(DialogDelegate dd);

    /**
     * get dialog delegate
     * @return the dialog delegate
     */
    DialogDelegate getDialogDelegate();

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
     * start browse images
     * @param context the context
     * @param param the image select parameter
     */
    void startBrowseImages(Activity context, ImageSelectParameter param);
    /**
     * start browse big images
     * @param context the context
     * @param param the parameter of ui
     * @param allItems the items to browse
     * @param single the single selected item.can be null for multi-select it must be null.
     */
    void startBrowseBigImages(Activity context, BigImageSelectParameter param, List<? extends IImageItem> allItems, IImageItem single);


    /**
     * the dialog delegate used to helpful handle image.
     * @since 1.0.2
     */
    interface DialogDelegate{

        /**
         * show image processing
         * @param activity the activity
         */
        void showImageProcessing(Activity activity);
        /**
         * dismiss image processing dialog
         * @param next the task used to do next. this is helpful for animate dialog
         */
        void dismissImageProcessing(Runnable next);
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
