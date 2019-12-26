package com.heaven7.android.imagepick.page;

import com.heaven7.core.util.Logger;

/*public*/ class MediaLog {

    public static void obtainItem(int position) {
        Logger.d("MediaLog", "obtainItem", "pos = " + position);
    }
    public static void recycleItem(int position) {
        Logger.d("MediaLog", "recycleItem", "position = " + position);
    }

    public static void destroyItem(int pos) {
        Logger.d("MediaLog", "destroyItem", "pos = " + pos);
    }
    public static void createItem(int pos) {
        Logger.d("MediaLog", "createItem", "pos = " + pos);
    }

    public static void onBindItem(int pos) {
        Logger.d("MediaLog", "onBindItem", "pos = " + pos);
    }

    public static void instantiateItem(int pos) {
        Logger.d("MediaLog", "instantiateItem", "pos = " + pos);
    }
}

