package com.heaven7.android.imagepick.pub;

import android.app.Activity;

/**
 * the exception handler
 * @author heaven7
 * @since 1.0.3
 */
public interface ExceptionHandler {

    /**
     * called on handle exception
     * @param activity the activity
     * @param code the code
     * @param e the exception
     * @return true if handled.
     */
    boolean handleException(Activity activity, int code, Exception e);
}
