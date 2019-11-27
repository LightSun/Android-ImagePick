package com.heaven7.android.imagepick;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

/*public*/ final class Utils {

    public static void openDefaultAnimator(RecyclerView mRv) {
        RecyclerView.ItemAnimator animator = mRv.getItemAnimator();
        animator.setAddDuration(120);
        animator.setChangeDuration(250);
        animator.setMoveDuration(250);
        animator.setRemoveDuration(120);
        ((SimpleItemAnimator) animator).setSupportsChangeAnimations(true);
    }
    public static void closeDefaultAnimator(RecyclerView mRv) {
        mRv.setItemAnimator(new com.heaven7.android.imagepick.DefaultItemAnimator());
       /* RecyclerView.ItemAnimator animator = mRv.getItemAnimator();
        animator.setAddDuration(0);
        animator.setChangeDuration(0);
        animator.setMoveDuration(0);
        animator.setRemoveDuration(0);
        ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);*/
    }
}
