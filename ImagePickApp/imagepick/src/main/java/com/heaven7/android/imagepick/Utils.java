package com.heaven7.android.imagepick;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import com.heaven7.android.imagepick.pub.ImageItem;

import java.util.ArrayList;
import java.util.List;

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

    public static List<ImageItem> createImageItems(List<MediaResourceHelper.MediaResourceItem> items, int[] selectPosition) {
        List<Integer> poss = new ArrayList<>();
        if(selectPosition != null){
            for (int pos : selectPosition){
                poss.add(pos);
            }
        }
        List<ImageItem> list = new ArrayList<>(items.size() * 4 / 3 + 1);
        for (int i = 0 , size = items.size() ; i < size ; i ++){
            MediaResourceHelper.MediaResourceItem item = items.get(i);
            ImageItem ii = new ImageItem();
            ii.setFilePath(item.getFilePath());
            ii.setSelected(poss.contains(i));
            list.add(ii);
        }
        return list;
    }
}
