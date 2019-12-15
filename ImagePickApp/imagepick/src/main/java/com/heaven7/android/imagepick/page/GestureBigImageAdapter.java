package com.heaven7.android.imagepick.page;

import android.widget.ImageView;

import java.util.List;

import internal.GestureImageUtils;

/**
 * bit image adapter ,support gesture
 * @param <T> the data type
 */
public abstract class GestureBigImageAdapter<T> extends BigImageAdapter<T>{

    private boolean supportGesture;

    public GestureBigImageAdapter(boolean mCarouselAllTime, List<? extends T> mDatas, boolean supportGesture) {
        super(mCarouselAllTime, mDatas);
        this.supportGesture = supportGesture;
    }

    @Override
    protected ImageView onCreateItemView(ItemViewContext context) {
        if(supportGesture){
            ImageView view = GestureImageUtils.createGestureImageView(context.context);
            if(view != null){
                return view;
            }
        }
        return super.onCreateItemView(context);
    }
}
