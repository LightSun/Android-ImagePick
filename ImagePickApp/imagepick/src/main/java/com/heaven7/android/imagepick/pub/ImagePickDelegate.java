package com.heaven7.android.imagepick.pub;

import android.app.Activity;

import java.util.List;

public interface ImagePickDelegate {

    void addOnSelectStateChangedListener(OnSelectStateChangedListener l);

    void removeOnSelectStateChangedListener(OnSelectStateChangedListener l);

    /**
     * start browse images
     * @param context the context
     */
    void startCamera(Activity context);
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
