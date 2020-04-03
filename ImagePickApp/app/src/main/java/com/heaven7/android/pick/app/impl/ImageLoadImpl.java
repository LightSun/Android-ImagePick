package com.heaven7.android.pick.app.impl;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.ImageLoadDelegate;
import com.heaven7.android.imagepick.pub.ImageOptions;

import java.io.File;

public class ImageLoadImpl implements ImageLoadDelegate {

    @Override
    public void loadImage(ImageView iv, IImageItem item, ImageOptions options) {
        Context context = iv.getContext();
        if (options != null) {
            //TODO may round or border is invalid
            if(options.getBorder() > 0 && options.getRound() > 0){
                BorderRoundTransformation borderTrans = new BorderRoundTransformation(context, options.getRound(),
                        0, options.getBorder(), options.getBorderColor());
                RequestBuilder rb = (RequestBuilder) Glide.with(context)
                        .load(new File(item.getFilePath()))
                        .transform(new Transformation[]{new CenterCrop()
                                , borderTrans})
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                rb.into(iv);
            }else {
                RequestBuilder rb = (RequestBuilder) Glide.with(context)
                        .load(new File(item.getFilePath()))
                        .transform(new Transformation[]{new CenterCrop()})
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                rb.into(iv);
            }
        } else {
            RequestManager rm = Glide.with(iv.getContext());
            if (item.getFilePath() != null) {
                rm
                        .load(new File(item.getFilePath()))
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(iv);
            } else {
                rm
                        .load(item.getUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv);
            }
        }
    }

    @Override
    public void pauseRequests(Activity activity) {
         Glide.with(activity).pauseRequests();
    }

    @Override
    public void resumeRequests(Activity activity) {
        Glide.with(activity).resumeRequests();
    }
}
