package com.heaven7.android.imagepick;

import java.util.ArrayList;
import java.util.List;

/*public*/ final class ImagePickManager {

    private static ImagePickManager sInstance;
    private List<String> mImages = new ArrayList<>(5);
    private ImagePickManager(){}

    public static ImagePickManager getDefault(){
        if(sInstance == null){
            sInstance = new ImagePickManager();
        }
        return sInstance;
    }
    public List<String> getImages(){
        return mImages;
    }
    public void addImagePath(String file){
        if(!mImages.contains(file)){
            mImages.add(file);
        }
        //System.out.println("addImagePath left: " + mImages);
    }
    public void removeImagePath(String file){
        mImages.remove(file);
        //System.out.println("removeImagePath left: " + mImages);
    }
}
