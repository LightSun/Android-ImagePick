package com.heaven7.android.imagepick;

import com.heaven7.adapter.ISelectable;

import java.util.ArrayList;
import java.util.List;

/*public*/ class Selector<T extends ISelectable> {

    private final List<T> mList = new ArrayList<>();
    private final Callback<T> mCallback;
    /**
     * true if is single select mode. false for multi mode.
     * */
    private boolean mSingleMode;

    public Selector(Callback<T> mCallback) {
        this.mCallback = mCallback;
    }

    public boolean isSingleMode() {
        return mSingleMode;
    }
    public void setSingleMode(boolean mSingleMode) {
        this.mSingleMode = mSingleMode;
    }
    public List<T> getSelects(){
        return mList;
    }
    public void select(T t){
        //single mode
        if(isSingleMode() && !mList.isEmpty()){
            //unselect pre
            T oldItem = mList.get(0);
            mList.clear();
            oldItem.setSelected(false);
            mCallback.onUnselect(mList, oldItem);
        }
        mList.add(t);
        t.setSelected(true);
        mCallback.onSelect(mList, t);
    }
    public void unselect(T t){
        mList.remove(t);
        t.setSelected(false);
        mCallback.onUnselect(mList, t);
    }
    public boolean isSelect(T t){
        return mList.contains(t);
    }
    public void toggleSelect(T t){
        if(isSelect(t)){
            unselect(t);
        }else {
            select(t);
        }
    }
    public interface Callback<T extends ISelectable>{
        void onSelect(List<T> items, T item);
        void onUnselect(List<T> items, T item);
    }
}
