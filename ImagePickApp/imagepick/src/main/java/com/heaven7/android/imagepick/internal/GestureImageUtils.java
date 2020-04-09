package com.heaven7.android.imagepick.internal;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RestrictTo;

import java.lang.reflect.Constructor;

/**
 * the gesture image utils.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class GestureImageUtils {

    private static final Constructor<?> sCons;

    static {
        sCons = getGestureImageViewConstructor();
    }

    public static ImageView createGestureImageView(Context context){
        if(sCons != null){
            try {
                ImageView iv = (ImageView)sCons.newInstance(context);
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return iv;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
