package com.heaven7.android.imagepick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.heaven7.adapter.page.GenericPagerAdapter;
import com.heaven7.adapter.page.GenericRvPagerAdapter;
import com.heaven7.adapter.page.IPageAdapter;
import com.heaven7.adapter.page.ItemViewContext;
import com.heaven7.adapter.page.PageDataProvider;
import com.heaven7.adapter.page.PageViewProvider;
import com.heaven7.adapter.page.ViewPagerDelegate;
import com.heaven7.adapter.page.WrappedPageChangeListener;
import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.internal.LibUtils;
import com.heaven7.android.imagepick.page.MediaPageProviderManager;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.android.imagepick.pub.delegate.SeeBigImageDelegate;
import com.heaven7.android.imagepick.pub.module.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.module.IImageItem;
import com.heaven7.android.imagepick.utils.ComposePageViewProvider;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.Toaster;
import com.heaven7.memory.util.Cacher;

import java.util.List;

/**
 * @author heaven7
 */
public class SeeBigImageActivity extends BaseActivity {

   // ViewPager mVp;
    ViewGroup mVg_root;

    private static final String TAG = "SeeBigImageActivity";
    private int mLayoutId;
    private BigImageSelectParameter mParam;
    private SeeBigImageDelegate mDelegate;
    private ViewPagerDelegate<?> mPagerDelegate;

    private List<IImageItem> mItems;
    private IImageItem mLastSingleItem;

    @Override
    protected int getLayoutId() {
        return mLayoutId;
    }

    @Override
    protected void onPreSetContentView() {
        mParam = getIntent().getParcelableExtra(PickConstants.KEY_PARAMS);

        mDelegate = LibUtils.newInstance(getIntent().getStringExtra(PickConstants.KEY_DELEGATE));
        mDelegate.setProvider(new Provider0());

        //mLayoutId = LibUtils.getInt(getIntent(), mParam, PickConstants.KEY_LAYOUT_ID, R.layout.lib_pick_ac_big_image);
        mLayoutId = getIntent().getIntExtra(PickConstants.KEY_LAYOUT_ID, R.layout.lib_pick_ac_big_image);
        //test ViewPager2 ok.
       //R.layout.lib_pick_ac_big_image2;
    }

    @Override
    protected void initialize(Context context, Bundle savedInstanceState) {
        View vp = mDelegate.getViewPager(getWindow().getDecorView());
        mPagerDelegate = ViewPagerDelegate.get(vp);

        //mVp = findViewById(R.id.lib_pick_vp);
        mVg_root = findViewById(R.id.lib_pick_vg_root);
        mDelegate.initialize(context, mVg_root, getIntent());

        setListeners();
        //setup data
        mLastSingleItem = getIntent().getParcelableExtra(PickConstants.KEY_SINGLE_ITEM);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
       // Logger.d(TAG, "init_"+hashCode(), "mItems.size = " + mItems.size());
        //set media adapter
        MediaPageProviderManager mppm = new MediaPageProviderManager(this, mDelegate);
        mppm.setSupportGesture(mParam.isSupportGestureImage());
        mppm.getItems().addAll(mItems);

        IPageAdapter pa ;
        ComposePageViewProvider provider = new ComposePageViewProvider(mppm.getImageViewProvider(), new VideoViewProvider(this));
        if(vp instanceof ViewPager){
            pa = new PageAdapterV1(mppm.getDataProvider(), provider, false);
        }else if(vp instanceof ViewPager2){
            pa = new PageAdapterV2(mppm.getDataProvider(), provider, false);
        }else {
            throw new UnsupportedOperationException("wrong view pager");
        }
        mPagerDelegate.setAdapter(this, pa);

        // set video manager delegate
        VideoManageDelegate vmd = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(vmd != null){
            vmd.onAttach(this);
        }
        //set ui state
        setUiState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
       // Logger.d(TAG, "onRestoreInstanceState_" + hashCode(), "mItems.size = " + mItems.size());
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy");
        //on detach
        VideoManageDelegate vmd = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(vmd != null){
            vmd.onDetach(this);
        }
        //remove listener
        ViewPager.OnPageChangeListener l = ImagePickDelegateImpl.getDefault().getOnPageChangeListener();
        if(l != null){
            mPagerDelegate.removeOnPageChangeListener(l);
        }
        mDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public void finish() {
        ImagePickDelegateImpl.getDefault().getImageItems().clear();
       // Logger.d(TAG, "onDestroy_"+hashCode(),"clear image items.");
        super.finish();
    }

    @Keep
    public void onClickBack(View view) {
        finish();
    }
    private void setListeners() {
        mPagerDelegate.addOnPageChangListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                //System.out.println("onPageSelected i = " + i);
                setSelectOrder(i + 1, false);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE) {
                    //System.out.println("onPageScrollStateChanged: SCROLL_STATE_IDLE. i = " + i);
                    setSelectOrder(mPagerDelegate.getCurrentItem() + 1, false);
                }
            }
        });
        WrappedPageChangeListener l = ImagePickDelegateImpl.getDefault().getOnPageChangeListener();
        if(l != null){
            mPagerDelegate.addOnPageChangListener(l);
        }
    }

    private void setSelectState(boolean select) {
        //check reach max
        if (select && hasFlag(PickConstants.FLAG_MULTI_SELECT)) {
            if (mParam.getSelectCount() >= mParam.getMaxSelectCount()) {
                Toaster.show(getApplication(), getString(R.string.lib_pick_select_reach_max));
                return;
            }
        }
        //set ui
        setImageBySelectState(select);
        IImageItem item = mItems.get(mParam.getCurrentOrder() - 1);
        //state changed
        if (item.isSelected() != select) {
            mParam.addSelectedCount(select ? 1 : -1);
            ImagePickDelegateImpl.getDefault().dispatchSelectStateChanged(item, select);
        }
        //handle for single select
        if (!hasFlag(PickConstants.FLAG_MULTI_SELECT)) {
            //single select mode. target is select .last must be unselect
            if (select && mLastSingleItem != null) {
                mLastSingleItem.setSelected(false);
            }
            mLastSingleItem = item;
        }
        item.setSelected(select);
        //set button text
        setSelectedText();
    }

    private void setImageBySelectState(boolean select) {
        mDelegate.setSelectState(select);
    }

    private void setSelectOrder(int order, boolean force) {
        if(!force && mParam.getCurrentOrder() == order){
            return;
        }
        mParam.setCurrentOrder(order);
        mDelegate.setSelectOrder();
       // mTv_title.setText(mParam.getCurrentOrder() + "/" + mParam.getTotalCount());

        boolean isSelect = mItems.get(mParam.getCurrentOrder() - 1).isSelected();
        setImageBySelectState(isSelect);
    }

    private void setUiState() {
        setSelectOrder(mParam.getCurrentOrder(), true);

        mDelegate.setUiState();
        setSelectedText();
        //handle position
        mPagerDelegate.setCurrentItem(mParam.getCurrentOrder() - 1);
        //the first time. video not play .we need called it
        if(mParam.getCurrentOrder() == 1){
            ViewPager.OnPageChangeListener listener = ImagePickDelegateImpl.getDefault().getOnPageChangeListener();
            if(listener != null){
                listener.onPageSelected(0);
            }
        }
    }

    private void setSelectedText() {
        mDelegate.setSelectedText();
    }

    private boolean hasFlag(int flags) {
        return (mParam.getFlags() & flags) == flags;
    }
    private static class VideoViewProvider extends PageViewProvider<IImageItem>{

        public VideoViewProvider(Context context) {
            super(context);
        }
        @Override
        public View createItemView(ViewGroup parent, int i, int i1, IImageItem item) {
            if(item.isVideo()){
                return ImagePickDelegateImpl.getDefault().getVideoManageDelegate()
                        .createVideoView(getContext(), parent, item);
            }
            return null;
        }
        @Override
        public void onBindItemView(View view, int position, int realPosition, IImageItem item) {
            if(item.isVideo()){
                ImagePickDelegateImpl.getDefault().getVideoManageDelegate()
                        .onBindItem(view, position, realPosition, item);
            }
        }
        @Override
        public void onDestroyItemView(View view, int position, int realPosition, IImageItem item) {
            if(item.isVideo()){
                ImagePickDelegateImpl.getDefault().getVideoManageDelegate()
                        .onDestroyItem(view, position, realPosition, item);
            }
        }
    }

    private class Provider0 implements SeeBigImageDelegate.Provider{

        @Override
        public AppCompatActivity getActivity() {
            return SeeBigImageActivity.this;
        }
        @Override
        public BigImageSelectParameter getParameter() {
            return mParam;
        }
        @Override
        public IImageItem getImageItem(int index) {
            return mItems.get(index);
        }
        @Override
        public void onClickSelect(View v) {
            Object tag = v.getTag();
            if (tag != null) {
                setSelectState(false);
            } else {
                setSelectState(true);
            }
        }
    }
    private static class PageAdapterV1 extends GenericPagerAdapter<IImageItem>{

        final Cacher<View, ItemViewContext> mVideoCache = new Cacher<View, ItemViewContext>(5) {
            @Override
            public View create(ItemViewContext context) {
                return getViewProvider().createItemView(context.parent, context.position,
                        context.realPosition, (IImageItem) context.data);
            }
        };
        public PageAdapterV1(PageDataProvider<? extends IImageItem> dataProvider, PageViewProvider<? extends IImageItem> viewProvider,
                             boolean loop) {
            super(dataProvider, viewProvider, loop);
        }
        @Override
        protected void recycleItemView(View view, ItemViewContext context) {
            if(isVideo(context)){
                com.heaven7.android.imagepick.internal.MediaLog.recycleItem(context);
                mVideoCache.recycle(view);
            }else {
                super.recycleItemView(view, context);
            }
        }
        @Override
        protected View obtainItemView(ItemViewContext context) {
            if(isVideo(context)){
                com.heaven7.android.imagepick.internal.MediaLog.obtainItem(context);
                return mVideoCache.obtain(context);
            }
            return super.obtainItemView(context);
        }
        private boolean isVideo(ItemViewContext context){
            IImageItem item = (IImageItem) context.data;
            return item.isVideo();
        }
    }
    private static class PageAdapterV2 extends GenericRvPagerAdapter<IImageItem> {

        final Cacher<View, ItemViewContext> mVideoCache = new Cacher<View, ItemViewContext>(5) {
            @Override
            public View create(ItemViewContext context) {
                return getViewProvider().createItemView(context.parent, context.position,
                        context.realPosition, (IImageItem) context.data);
            }
        };
        public PageAdapterV2(PageDataProvider<? extends IImageItem> dataProvider, PageViewProvider<? extends IImageItem> viewProvider,
                            boolean loop) {
            super(dataProvider, viewProvider, loop);
        }
        @Override
        protected void recycleItemView(View view, ItemViewContext context) {
            if(isVideo(context)){
                com.heaven7.android.imagepick.internal.MediaLog.recycleItem(context);
                mVideoCache.recycle(view);
            }else {
                super.recycleItemView(view, context);
            }
        }
        @Override
        protected View obtainItemView(ItemViewContext context) {
            if(isVideo(context)){
                com.heaven7.android.imagepick.internal.MediaLog.obtainItem(context);
                return mVideoCache.obtain(context);
            }
            return super.obtainItemView(context);
        }
        private boolean isVideo(ItemViewContext context){
            IImageItem item = (IImageItem) context.data;
            return item.isVideo();
        }
    }
}
