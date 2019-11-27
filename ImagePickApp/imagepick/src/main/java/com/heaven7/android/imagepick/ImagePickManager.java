package com.heaven7.android.imagepick;

import com.heaven7.android.imagepick.pub.ImageItem;

import java.util.ArrayList;
import java.util.List;

public final class ImagePickManager {

    private static ImagePickManager sInstance;
    private ImagePickManager(){}

    private List<String> mImages = new ArrayList<>(5);
    private OnSelectStateChangedListener mListener;
    private List<ImageItem> mItems;

    public static ImagePickManager getDefault(){
        if(sInstance == null){
            sInstance = new ImagePickManager();
        }
        return sInstance;
    }
    /*public*/ List<String> getImages(){
        return mImages;
    }
    /*public*/ void addImagePath(String file){
        if(!mImages.contains(file)){
            mImages.add(file);
        }
        //System.out.println("addImagePath left: " + mImages);
    }
    /*public*/ void removeImagePath(String file){
        mImages.remove(file);
        //System.out.println("removeImagePath left: " + mImages);
    }

    public List<ImageItem> getImageItems() {
        return mItems;
    }
    public void setImageItems(List<ImageItem> mItems) {
        this.mItems = mItems;
    }

    public OnSelectStateChangedListener getOnSelectStateChangedListener() {
        return mListener;
    }
    public void setOnSelectStateChangedListener(OnSelectStateChangedListener mListener) {
        this.mListener = mListener;
    }

    public interface OnSelectStateChangedListener{
        void onSelectStateChanged(ImageItem item, boolean select);
    }
}
