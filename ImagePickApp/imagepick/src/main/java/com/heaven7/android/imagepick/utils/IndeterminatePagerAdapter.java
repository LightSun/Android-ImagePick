package com.heaven7.android.imagepick.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public abstract class IndeterminatePagerAdapter<T> extends PagerAdapter {

    private final Provider<T> mProvider;

    public IndeterminatePagerAdapter(Provider<? extends T> mProvider) {
        this.mProvider = (Provider<T>) mProvider;
    }

    public Provider<T> getProvider() {
        return mProvider;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int index = mProvider.getPositionActually(position);
        T data = mProvider.getItem(index);
        View view = onCreateItemView(container, position, index, data);
        container.addView(view);
        return view;
    }

    protected abstract View onCreateItemView(@NonNull ViewGroup container, int position, int realPos,T data);

    public interface Provider<T>{
        int getPositionActually(int position);
        T getItem(int position);
    }

}
