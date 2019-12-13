package com.heaven7.android.imagepick;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.RecyclerView;

import com.heaven7.android.imagepick.pub.ImageParameter;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.core.util.ImageParser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/*public*/ final class Utils {

    /*public static void openDefaultAnimator(RecyclerView mRv) {
        RecyclerView.ItemAnimator animator = mRv.getItemAnimator();
        animator.setAddDuration(120);
        animator.setChangeDuration(250);
        animator.setMoveDuration(250);
        animator.setRemoveDuration(120);
        ((SimpleItemAnimator) animator).setSupportsChangeAnimations(true);
    }*/
    static Constructor<?> getGestureImageViewConstructor(){
        Class mClazz = null;
        try {
            mClazz = Class.forName("com.github.chrisbanes.photoview.PhotoView");
        } catch (Exception e) {
            //ignore
            System.err.println("for support gesture image. you should include PhotoView lib ,such as \n" +
                    "{ implementation 'com.github.chrisbanes:PhotoView:2.1.4'}");
        }
        if(mClazz != null){
            try {
                return mClazz.getConstructor(Context.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                //never happen
            }
        }
        return null;
    }
    public static void closeDefaultAnimator(RecyclerView mRv) {
        mRv.setItemAnimator(new com.heaven7.android.imagepick.DefaultItemAnimator());
    }

    public static ArrayList<String> getFilePaths(List<MediaResourceItem> items) {
        ArrayList<String> paths = new ArrayList<>();
        for (MediaResourceItem item : items){
            paths.add(item.getFilePath());
        }
        return paths;
    }

    public static ImageParser createImageParser(ImageParameter ip, boolean checkExif) {
        final Bitmap.Config config;
        switch (ip.getFormat()){
            default:
            case PickConstants.FORMAT_RGB_565:
                config = Bitmap.Config.RGB_565;
                break;
            case PickConstants.FORMAT_ARGB_8888:
                config = Bitmap.Config.ARGB_8888;
                break;
            case PickConstants.FORMAT_RGBA_F16:
                //API26
                if(Build.VERSION.SDK_INT >= 26){
                    config = Bitmap.Config.RGBA_F16;
                }else {
                    System.err.println("CameraParameter >> the format of camera parameter can't support RGBA_F16. force to use ARGB_8888 now.");
                    config = Bitmap.Config.ARGB_8888;
                }
                break;
        }
        return new ImageParser(ip.getMaxWidth(), ip.getMaxHeight(), config, checkExif);
    }

    public static Bitmap.CompressFormat getCompressFormat(String extension) {
        switch (extension){
            case "jpg":
            case "jpeg":
            case "JPEG":
            case "JPG":
                return Bitmap.CompressFormat.JPEG;

            case "png":
            case "PNG":
                return Bitmap.CompressFormat.PNG;

            case "webp":
            case "WEBP":
                return Bitmap.CompressFormat.WEBP;
        }
        return null;
    }
}
