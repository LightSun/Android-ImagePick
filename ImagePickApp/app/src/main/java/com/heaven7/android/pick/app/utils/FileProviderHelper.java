package com.heaven7.android.pick.app.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Created by heaven7 on 2017/12/22.
 */

public final class FileProviderHelper {

    public static Uri getUriForFile(Context context, String file) {
        return getUriForFile(context, new File(file));
    }

    public static Uri getUriForFile(Context context, File file) {
        if (file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, getAuthority(context), file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private static String getAuthority(Context activity){
        return activity.getPackageName() + ".fileprovider";
    }
}
