package com.heaven7.android.imagepick.page;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * bit image adapter ,support gesture
 * @param <T> the data type
 */
public abstract class GestureBigImageAdapter<T> extends BigImageAdapter<T>{

    private final Constructor mCons;

    public GestureBigImageAdapter(boolean mCarouselAllTime, List<T> mDatas, boolean supportGesture) {
        super(mCarouselAllTime, mDatas);
        mCons = supportGesture ? getGestureImageViewConstructor() : null;
    }

    @Override
    protected ImageView onCreateItemView(ItemViewContext context) {
        if(mCons != null){
            try {
                ImageView iv = (ImageView)mCons.newInstance(context.context);
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return iv;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onCreateItemView(context);
    }

    private static Constructor<?> getGestureImageViewConstructor(){
        Class mClazz = null;
        try {
            mClazz = Class.forName("com.github.chrisbanes.photoview.PhotoView");
        } catch (Exception e) {
            //ignore
            System.err.println("for support gesture image. you should include PhotoView lib ,such as \n" +
                    "{ implementation 'com.github.chrisbanes:PhotoView:2.1.4'}");
        }
        if(mClazz != null){
            try {
                return mClazz.getConstructor(Context.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                //never happen
            }
        }
        return null;
    }
}
