package com.heaven7.android.imagepick.internal;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RestrictTo;


/**
 * com.heaven7.android.imagepick.internal
 * @author heaven7
 * @since 1.0.5
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class LibUtils {

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className){
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            if(e instanceof RuntimeException){
                throw (RuntimeException)e;
            }else {
                throw new RuntimeException(e);
            }
        }
    }

    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
