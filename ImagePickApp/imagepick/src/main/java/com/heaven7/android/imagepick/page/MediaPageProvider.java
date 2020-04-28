package com.heaven7.android.imagepick.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.heaven7.adapter.page.PageDataProvider;
import com.heaven7.adapter.page.PageViewProvider;

import java.util.ArrayList;
import java.util.List;

public final class MediaPageProvider<T>{

    private final List<T> mDatas = new ArrayList<>();

    private final DataProvider0 mDataProvider;
    private final ViewProvider0 mViewProvider;

    public MediaPageProvider(Context context) {
        mDataProvider = new DataProvider0(context);
        mViewProvider = new ViewProvider0(context);
    }

    public List<T> getItems(){
        return mDatas;
    }
    public void addItems(List<T> datas){
        mDatas.addAll(datas);
        mDataProvider.notifyDataSetChanged();
    }
    public void replaceItems(List<T> datas){
        mDatas.clear();
        mDatas.addAll(datas);
        mDataProvider.notifyDataSetChanged();
    }
    public T getItemAt(int index){
        return mDatas.get(index);
    }
    public void clearItems(){
        mDatas.clear();
        mDataProvider.notifyDataSetChanged();
    }
    public void removeItem(T item){
        mDatas.remove(item);
        mDataProvider.notifyDataSetChanged();
    }

    private class DataProvider0 extends PageDataProvider<T>{

        public DataProvider0(Context context) {
            super(context);
        }
        @Override
        public int getItemCount() {
            return mDatas.size();
        }
        @Override
        public T getItem(int i) {
            return mDatas.get(i);
        }
    }
    //TODO
    private class ViewProvider0 extends PageViewProvider<T>{

        public ViewProvider0(Context context) {
            super(context);
        }
        @Override
        public View createItemView(ViewGroup viewGroup, int i, int i1, T t) {
            return null;
        }
        @Override
        public void onBindItemView(View view, int i, int i1, T t) {

        }
    }
}
