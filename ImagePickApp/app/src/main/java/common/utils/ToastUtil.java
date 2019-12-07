package common.utils;

import android.content.Context;


/**
 * Created by Administrator on 2017/5/12.
 */

public class ToastUtil {

    @Deprecated
    public static void e(Context context, Throwable throwable) {
        if (null != context && null != throwable) {
//            Toasty.normal(context, throwable.getMessage()).show();
        }
    }
}