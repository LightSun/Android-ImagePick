package com.heaven7.android.imagepick.pub.delegate;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

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
