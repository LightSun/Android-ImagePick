package com.heaven7.android.imagepick.utils;

import android.view.View;
import android.view.ViewGroup;

import com.heaven7.adapter.page.PageViewProvider;
import com.heaven7.android.imagepick.pub.module.IImageItem;

import java.util.Arrays;
import java.util.List;

/**
 * compose page view provider
 * @author heaven7
 * @since 2.0.0
 */
public final class ComposePageViewProvider extends PageViewProvider<IImageItem> {

    private final List<PageViewProvider<IImageItem>> mList;

    public ComposePageViewProvider(List<PageViewProvider<IImageItem>> ps) {
        super(ps.get(0).getContext());
        mList = ps;
    }
    public ComposePageViewProvider(PageViewProvider<IImageItem>... ps) {
       this(Arrays.asList(ps));
    }

    @Override
    public View createItemView(ViewGroup viewGroup, int i, int i1, IImageItem iImageItem) {
        for (PageViewProvider<IImageItem> p : mList){
            View itemView = p.createItemView(viewGroup, i, i1, iImageItem);
            if(itemView != null){
                return itemView;
            }
        }
        return null;
    }

    @Override
    public void onBindItemView(View view, int i, int i1, IImageItem iImageItem) {
        for (PageViewProvider<IImageItem> p : mList){
            p.onBindItemView(view, i, i1, iImageItem);
        }
    }
}