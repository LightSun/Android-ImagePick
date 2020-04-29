package com.heaven7.android.imagepick.internal;

import androidx.annotation.RestrictTo;

import com.heaven7.adapter.page.ItemViewContext;
import com.heaven7.core.util.Logger;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class MediaLog {

    private static final boolean DEBUG = true;

    public static void obtainItem(ItemViewContext context) {
        if(DEBUG){
            String msg = String.format("for video ---> pos = %d, realPos = %d, data = %s", context.position, context.realPosition, context.data.toString());
            Logger.d("MediaLog", "obtainItem", msg);
        }
    }
    public static void recycleItem(ItemViewContext context) {
        if(DEBUG){
            String msg = String.format("for video ---> pos = %d, realPos = %d, data = %s", context.position, context.realPosition, context.data.toString());
            Logger.d("MediaLog", "recycleItem", msg);
        }
    }
}

