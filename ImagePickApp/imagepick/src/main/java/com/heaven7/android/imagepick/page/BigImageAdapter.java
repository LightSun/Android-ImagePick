package com.heaven7.android.imagepick.page;

import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public abstract class BigImageAdapter<T> extends AbstractPagerAdapter<T, ImageView> {

    public BigImageAdapter(boolean mCarouselAllTime, List<T> mDatas) {
        super(mCarouselAllTime, mDatas);
    }
    @Override
    protected ImageView onCreateItemView(ItemViewContext context) {
        ImageView iv = new ImageView(context.context);
       // iv.setScaleType(ImageView.ScaleType.CENTER);
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return iv;
    }
}