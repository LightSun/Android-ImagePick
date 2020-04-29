package com.heaven7.android.imagepick.page;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.heaven7.adapter.page.ItemViewContext;

import java.util.List;

public abstract class BigImageAdapter<T> extends AbstractPagerAdapter<T, ImageView> {

    public BigImageAdapter(boolean mCarouselAllTime, List<? extends T> mDatas) {
        super(mCarouselAllTime, mDatas);
    }
    @Override
    protected ImageView onCreateItemView(ItemViewContext context) {
        ImageView iv = new ImageView(context.getContext());
       // iv.setScaleType(ImageView.ScaleType.CENTER);
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return iv;
    }
}