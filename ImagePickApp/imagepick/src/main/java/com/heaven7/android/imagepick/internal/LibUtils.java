package com.heaven7.android.imagepick.internal;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RestrictTo;

import com.heaven7.android.imagepick.pub.module.INextParameter;


/**
 * com.heaven7.android.imagepick.internal
 * @author heaven7
 * @since 1.0.5
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
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

    public static int getDisplayWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getInt(Intent intent, INextParameter np, String key, int defaultVal) {
        int val = intent.getIntExtra(key, 0);
        if(val == 0){
            if(np.getNext() != null){
                val = np.getNext().getInt(key, 0);
            }
            if(val == 0){
                val = defaultVal;
            }
        }
        return val;
    }
}
