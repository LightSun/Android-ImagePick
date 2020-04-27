package com.heaven7.android.pick.app.impl;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.heaven7.android.imagepick.pub.module.IImageItem;
import com.heaven7.android.imagepick.pub.ImageLoadDelegate;
import com.heaven7.android.imagepick.pub.module.ImageOptions;
import com.heaven7.android.pick.app.FileProviderHelper;
import com.heaven7.android.video.ScaleManager;
import com.heaven7.android.video.load.FrameInfo;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class ImageLoadImpl implements ImageLoadDelegate {

    @Override
    public void loadImage(LifecycleOwner owner, ImageView iv, IImageItem item, ImageOptions options) {
        if(item.isGif()){
            if(item.getUrl() != null){
                //net
                throw new UnsupportedOperationException("you should download the gif. and load as GifDrawable.");
            }else {
                GifDrawable gifDrawable = null;
                try {
                    gifDrawable = new GifDrawable(item.getFilePath());
                    iv.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        Context context = iv.getContext();
        FrameInfo info = getFrameInfo(iv.getContext(), item, options);
        if (options != null) {
            //TODO may round or border is invalid
            if(options.getBorder() > 0 && options.getRound() > 0){
                BorderRoundTransformation borderTrans = new BorderRoundTransformation(context, options.getRound(),
                        0, options.getBorder(), options.getBorderColor());
                RequestBuilder rb = (RequestBuilder) getRequestBuilder(context, info)
                        .transform(new Transformation[]{
                                new CenterCrop(),
                                borderTrans})
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                rb.into(iv);
            }else {
                RequestBuilder rb = (RequestBuilder) getRequestBuilder(context, info)
                        .transform(new Transformation[]{new CenterCrop()})
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                rb.into(iv);
            }
        } else {
            RequestBuilder rb = (RequestBuilder) getRequestBuilder(context, info)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            rb.into(iv);
        }
    }
    private RequestBuilder getRequestBuilder(Context context,FrameInfo info){
        if(info.isFromVideo() && info.isLocal()){
            //local video
            return  Glide.with(context).load(info);
        }
        return Glide.with(context).load(info.getUri());
    }

    private FrameInfo getFrameInfo(Context context,IImageItem item, ImageOptions options) {
        FrameInfo info = FrameInfo.obtain();
        info.setScaleType(ScaleManager.ScaleType_CENTER_CROP);
        if(item.getUrl() != null){
            info.setUri(Uri.parse(item.getUrl()));
            info.setIsLocal(false);
            //todo latter support
        }else {
            info.setUri(FileProviderHelper.getUriForFile(context, item.getFilePath()));
            info.setIsLocal(true);
        }
        info.setFromVideo(item.isVideo());
        if(item.isVideo()){
            info.setTimeMsec(0);
        }
        if(options != null){
            info.setWidth(options.getTargetWidth());
            info.setHeight(options.getTargetHeight());
        }
        info.setMaxWidth(800);
        info.setMaxHeight(800);
        return info;
    }

    @Override
    public void pauseRequests(Activity activity) {
         Glide.with(activity).pauseRequests();
    }

    @Override
    public void resumeRequests(Activity activity) {
        if(activity.isFinishing() || activity.isDestroyed()){
            return;
        }
        Glide.with(activity).resumeRequests();
    }
}
