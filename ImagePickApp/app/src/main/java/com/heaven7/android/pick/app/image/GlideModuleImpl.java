package com.heaven7.android.pick.app.image;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.GlideModule;
import com.heaven7.android.video.load.FrameInfo;

import java.io.InputStream;

@Keep
public class GlideModuleImpl implements GlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {

    }
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //4.x: append
        registry.append(FrameInfo.class, InputStream.class, new GlideVideoFrameLoader.Factory(context.getApplicationContext()));
        //3.x: register
    }
}
