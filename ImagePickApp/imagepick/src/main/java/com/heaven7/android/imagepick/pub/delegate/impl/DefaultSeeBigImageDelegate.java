package com.heaven7.android.imagepick.pub.delegate.impl;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.heaven7.android.imagepick.pub.delegate.SeeBigImageDelegate;

/**
 * the default delegate which is used by {@linkplain com.heaven7.android.imagepick.SeeBigImageActivity}.
 * @since 2.0.0
 */
public class DefaultSeeBigImageDelegate extends SeeBigImageDelegate{

    @Override
    protected ViewBinder onCreateTopBinder(Context context, ViewGroup parent, Intent intent) {
        return new DefaultTopBinder(this, parent);
    }

    @Override
    protected ViewBinder onCreateBottomBinder(Context context, ViewGroup parent, Intent intent) {
        return new DefaultBottomBinder(this, parent);
    }
}
