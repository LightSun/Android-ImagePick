package com.heaven7.android.pick.app.impl;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.heaven7.android.imagepick.pub.delegate.impl.SimpleSeeBigImageDelegate;


/**
 *  for test just use a vertical ViewPager2
 */
public class SeeBigImageDelegate2 extends SimpleSeeBigImageDelegate {

    @Override
    public View getViewPager(View root) {
        View vp = super.getViewPager(root);
        if(vp instanceof ViewPager2){
            ((ViewPager2) vp).setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        }
        return vp;
    }
}
