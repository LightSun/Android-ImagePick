package com.heaven7.android.imagepick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.LibPick$_ViewPagerUtils;
import androidx.viewpager.widget.ViewPager;

import com.heaven7.android.imagepick.internal.LibUtils;
import com.heaven7.android.imagepick.page.AbstractMediaPageAdapter;
import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.android.imagepick.pub.delegate.SeeBigImageDelegate;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.Toaster;

import java.util.List;

/**
 * @author heaven7
 */
public class SeeBigImageActivity extends BaseActivity {

    ViewPager mVp;
    ViewGroup mVg_root;

    private static final String TAG = "SeeBigImageActivity";
    private BigImageSelectParameter mParam;
    private SeeBigImageDelegate mDelegate;

    private List<IImageItem> mItems;
    private IImageItem mLastSingleItem;

    private MediaAdapter mMediaAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_big_image;
    }
    @Override
    protected void init(Context context, Bundle savedInstanceState) {
        mDelegate = LibUtils.newInstance(getIntent().getStringExtra(PickConstants.KEY_DELEGATE));
        mDelegate.setProvider(new Provider0());

        mVp = findViewById(R.id.vp);
        mVg_root = findViewById(R.id.vg_root);
        mDelegate.initialize(context, mVg_root, getIntent());

        setListeners();
        //setup data
        mLastSingleItem = getIntent().getParcelableExtra(PickConstants.KEY_SINGLE_ITEM);
        mParam = getIntent().getParcelableExtra(PickConstants.KEY_PARAMS);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
       // Logger.d(TAG, "init_"+hashCode(), "mItems.size = " + mItems.size());
        //set media adapter
        mMediaAdapter = new MediaAdapter(mItems);
        mMediaAdapter.setSupportGestureImage(mParam.isSupportGestureImage());
        mVp.setAdapter(mMediaAdapter);

        //
        VideoManageDelegate vmd = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(vmd != null){
            vmd.onAttach(this);
        }
        //set ui state
        setUiState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       // Logger.d(TAG, "onNewIntent", "" + hashCode());
        setIntent(intent);
        init(this, null);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
       // Logger.d(TAG, "onRestoreInstanceState_" + hashCode(), "mItems.size = " + mItems.size());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG,"onPause");
        mMediaAdapter.onPause(mVp.getCurrentItem(), LibPick$_ViewPagerUtils.getCurrentView(mVp));
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMediaAdapter.onResume( mVp.getCurrentItem(), LibPick$_ViewPagerUtils.getCurrentView(mVp));
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy");
        //release media
        mMediaAdapter.onDestroy(mVp.getCurrentItem(), LibPick$_ViewPagerUtils.getCurrentView(mVp));
        //on detach
        VideoManageDelegate vmd = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(vmd != null){
            vmd.onDetach(this);
        }
        //remove listener
        ViewPager.OnPageChangeListener l = ImagePickDelegateImpl.getDefault().getOnPageChangeListener();
        if(l != null){
            mVp.removeOnPageChangeListener(l);
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
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                System.out.println("onPageSelected i = " + i);
                setSelectOrder(i + 1, false);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE) {
                    System.out.println("onPageScrollStateChanged: SCROLL_STATE_IDLE. i = " + i);
                    setSelectOrder(mVp.getCurrentItem() + 1, false);
                }
            }
        });
        ViewPager.OnPageChangeListener l = ImagePickDelegateImpl.getDefault().getOnPageChangeListener();
        if(l != null){
            mVp.addOnPageChangeListener(l);
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
        mVp.setCurrentItem(mParam.getCurrentOrder() - 1);
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
    private class MediaAdapter extends AbstractMediaPageAdapter{

        public MediaAdapter(List<? extends IImageItem> mDatas) {
            super(mDatas);
        }
        @Override
        protected void onBindImageItem(ImageView iv, final int index, final IImageItem data) {
            if(!mDelegate.bindImageItem(iv, index, data)){
                ImagePickDelegateImpl.getDefault().getImageLoadDelegate().loadImage(iv, data, null);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDelegate.onClickPageImageView(v, index, data);
                    }
                });
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
        @Override
        public View getCurrentView() {
            return LibPick$_ViewPagerUtils.getCurrentView(mVp);
        }
        public int getCurrentPosition(){
            return mMediaAdapter.getPositionActually(mVp.getCurrentItem());
        }
    }
}
