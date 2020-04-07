package com.heaven7.android.imagepick.page;

import com.heaven7.core.util.Logger;

/*public*/ class MediaLog {

    private static final boolean DEBUG = false;

    public static void obtainItem(int position) {
        if(DEBUG){
            Logger.d("MediaLog", "obtainItem", "pos = " + position);
        }
    }
    public static void recycleItem(int position) {
        if(DEBUG){
            Logger.d("MediaLog", "recycleItem", "position = " + position);
        }
    }

    public static void destroyItem(int pos) {
        if(DEBUG){
            Logger.d("MediaLog", "destroyItem", "pos = " + pos);
        }
    }
    public static void createItem(int pos) {
        if(DEBUG){
            Logger.d("MediaLog", "createItem", "pos = " + pos);
        }
    }

    public static void onBindItem(int pos) {
        if(DEBUG){
            Logger.d("MediaLog", "onBindItem", "pos = " + pos);
        }
    }

    public static void instantiateItem(int pos) {
        if(DEBUG){
            Logger.d("MediaLog", "instantiateItem", "pos = " + pos);
        }
    }
}

