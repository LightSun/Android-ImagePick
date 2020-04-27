package com.heaven7.android.imagepick.pub.delegate.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.delegate.SeeBigImageDelegate;

public class SimpleSeeBigImageDelegate extends SeeBigImageDelegate {

    private String mTitle;

    @Override
    protected ViewBinder onCreateBottomBinder(Context context, ViewGroup parent, Intent intent) {
        return null;
    }
    @Override
    protected ViewBinder onCreateTopBinder(Context context, ViewGroup parent, Intent intent) {
        mTitle = intent.getStringExtra(PickConstants.KEY_EXTRA);
        return new TopBinder0(this, parent);
    }

    @Override
    public void initialize(Context context, ViewGroup parent, Intent intent) {
        super.initialize(context, parent, intent);
        TopBinder0 binder = (TopBinder0) getTopBinder();
        binder.setTitle(mTitle);
    }

    private static class TopBinder0 extends DefaultTopBinder{

        public TopBinder0(SeeBigImageDelegate delegate, ViewGroup parent) {
            super(delegate, parent);
        }
        @Override
        public void onBind() {
            super.onBind();
            mTv_upload.setVisibility(View.GONE);
        }
        public void setTitle(String mTitle) {
            mTv_title.setText(mTitle);
        }
    }
}
