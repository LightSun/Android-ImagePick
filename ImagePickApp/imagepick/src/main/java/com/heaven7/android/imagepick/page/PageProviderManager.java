package com.heaven7.android.imagepick.page;

import android.content.Context;

import com.heaven7.adapter.page.PageDataProvider;

import java.util.ArrayList;
import java.util.List;

public class PageProviderManager<T>{

    private final List<T> mDatas = new ArrayList<>();
    private final DataProvider0 mDataProvider;

    public PageProviderManager(Context context) {
        mDataProvider = new DataProvider0(context);
    }

    public PageDataProvider<T> getDataProvider() {
        return mDataProvider;
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
}
