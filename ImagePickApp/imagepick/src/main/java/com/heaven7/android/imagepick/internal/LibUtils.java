package com.heaven7.android.imagepick.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RestrictTo;

import com.heaven7.android.imagepick.pub.module.INextParameter;


/**
 * com.heaven7.android.imagepick.internal
 *
 * @author heaven7
 * @since 1.0.5
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class LibUtils {

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
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
        return new TwoBundle(np.getNext(), intent.getExtras()).getInt(key, defaultVal);
    }
    public static String getString(Intent intent, INextParameter np, String key, String defaultVal) {
        return new TwoBundle(np.getNext(), intent.getExtras()).getString(key, defaultVal);
    }

    private static class TwoBundle {
        final Bundle b1;
        final Bundle b2;

        public TwoBundle(Bundle b1, Bundle b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
        public int getInt(String key, int defaultVal) {
            int val = 0;
            if(b1 != null){
                val = b1.getInt(key, 0);
            }
            if(val == 0 && b2 != null){
                val = b2.getInt(key, 0);
            }
            if(val == 0){
                return defaultVal;
            }
            return val;
        }
        public String getString(String key, String defaultVal) {
            String val = null;
            if(b1 != null){
                val = b1.getString(key);
            }
            if(val == null && b2 != null){
                val = b2.getString(key);
            }
            if(val == null){
                return defaultVal;
            }
            return val;
        }
    }
}
