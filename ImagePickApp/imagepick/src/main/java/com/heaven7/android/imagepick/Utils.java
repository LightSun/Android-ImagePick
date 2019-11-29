package com.heaven7.android.imagepick;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import com.heaven7.android.imagepick.pub.IImageItem;
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

    public static List<? extends IImageItem> createImageItems(List<MediaResourceHelper.MediaResourceItem> items) {
        List<IImageItem> list = new ArrayList<>(items.size() * 4 / 3 + 1);
        for (int i = 0 , size = items.size() ; i < size ; i ++){
            MediaResourceHelper.MediaResourceItem item = items.get(i);
            ImageItem ii = new ImageItem();
            ii.setFilePath(item.getFilePath());
            ii.setSelected(item.isSelected());
            list.add(ii);
        }
        return list;
    }

    public static ImageItem createImageItem(MediaResourceHelper.MediaResourceItem item) {
        ImageItem ii = new ImageItem();
        ii.setFilePath(item.getFilePath());
        ii.setSelected(item.isSelected());
        return ii;
    }

    public static ArrayList<String> getFilePaths(List<MediaResourceHelper.MediaResourceItem> items) {
        ArrayList<String> paths = new ArrayList<>();
        for (MediaResourceHelper.MediaResourceItem item : items){
            paths.add(item.getFilePath());
        }
        return paths;
    }
}
