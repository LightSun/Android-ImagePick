package com.heaven7.android.imagepick.page;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

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

    public AbstractPagerAdapter(boolean mCarouselAllTime, List<T> mDatas) {
        this.mCarouselAllTime = mCarouselAllTime;
        this.mDatas = mDatas != null ? new ArrayList<T>(mDatas) : new ArrayList<T>();
        mCacher = new Cacher<ItemView, ItemViewContext>() {
            @Override
            public ItemView create(ItemViewContext context) {
                return onCreateItemView(context);
            }
        };
    }
    @Deprecated
    public List<T> getDatas(){
        return mDatas;
    }
    @Deprecated
    public void addDatas(List<T> datas){
        mDatas.addAll(datas);
        notifyDataSetChanged();
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
       /* ImageView iv = new ImageView(container.getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/
        int index = getPositionActually(position);
        ItemView itemView = mCacher.obtain(new ItemViewContext(container.getContext(), index));
        container.addView(itemView);
        onBindItem(itemView, index, mDatas.get(index));
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    protected int getPositionActually(int position){
        if(mCarouselAllTime && mDatas.size()  > 0){
            return position % mDatas.size();
        }
        return position;
    }

    /**
     * called on bind item data
     * @param iv the image view
     * @param index the actually index
     * @param data the data
     */
    protected abstract void onBindItem(ItemView iv, int index, T data);

    /**
     * called on create item view for view pager
     * @param context the context
     * @return the item view.
     */
    protected abstract ItemView onCreateItemView(ItemViewContext context);


    public static class ItemViewContext{
        public final Context context;
        public final int position;
        public ItemViewContext(Context context, int position) {
            this.context = context;
            this.position = position;
        }
    }
}
