package com.heaven7.android.imagepick.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.heaven7.memory.util.Cacher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heaven7
 */
public abstract class AbstractPagerAdapter<T, ItemView extends View> extends PagerAdapter {

    private final boolean mCarouselAllTime;
    private final List<T> mDatas;
    private final Cacher<ItemView, ItemViewContext> mCacher;

    public AbstractPagerAdapter(boolean mCarouselAllTime, List<? extends T> mDatas) {
        this.mCarouselAllTime = mCarouselAllTime;
        this.mDatas = mDatas != null ? new ArrayList<T>(mDatas) : new ArrayList<T>();
        mCacher = new Cacher<ItemView, ItemViewContext>() {
            @Override
            public ItemView create(ItemViewContext context) {
                return onCreateItemView(context);
            }
        };
    }
    public List<T> getItems(){
        return mDatas;
    }
    public void addItems(List<T> datas){
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
    public void replaceItems(List<T> datas){
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
    public T getItemAt(int index){
        return mDatas.get(index);
    }
    public void clearItems(){
        mDatas.clear();
        notifyDataSetChanged();
    }
    public void removeItem(T item){
        mDatas.remove(item);
        notifyDataSetChanged();
    }
    public void clearCacheViews(){
        mCacher.clear();
    }

    @Override
    public int getCount() {
        if(mCarouselAllTime && mDatas.size() > 0){
            return Integer.MAX_VALUE;
        }
        return mDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int index = getPositionActually(position);
        T data = mDatas.get(index);

        ItemView itemView = obtainItemView(new ItemViewContext(container, index, data));
        container.addView(itemView);
        onBindItem(itemView, position, index, data);
        return itemView;
    }

    @Override @SuppressWarnings("unchecked")
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ItemView view = (ItemView)object;
        container.removeView(view);
        if(shouldRecycle(getPositionActually(position), view)){
            mCacher.recycle(view);
        }
    }

    public int getPositionActually(int position){
        if(mCarouselAllTime && mDatas.size()  > 0){
            return position % mDatas.size();
        }
        return position;
    }

    /**
     * obtain item view
     * @param p the parameter to obtain
     * @return the item view.
     */
    protected ItemView obtainItemView(ItemViewContext p){
        return mCacher.obtain(p);
    }
    /**
     * called if you want to recycle view
     * @param position the real position
     * @param view the item view
     * @return true if should recycle. default is true
     */
    protected boolean shouldRecycle(int position, View view){
        return true;
    }

    /**
     * called on bind item data
     * @param iv the image view
     * @param position the position
     * @param realPos the actually position
     * @param data the data
     */
    protected abstract void onBindItem(ItemView iv, int position, int realPos, T data);

    /**
     * called on create item view for view pager
     * @param context the context
     * @return the item view.
     */
    protected abstract ItemView onCreateItemView(ItemViewContext context);


    public static class ItemViewContext{
        public final ViewGroup parent;
        public final int position;
        public final Object data;
        public ItemViewContext(ViewGroup parent, int position, Object data) {
            this.parent = parent;
            this.position = position;
            this.data = data;
        }
        public Context getContext(){
            return parent.getContext();
        }
    }
}
