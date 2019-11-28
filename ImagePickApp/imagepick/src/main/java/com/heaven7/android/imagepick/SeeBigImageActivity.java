package com.heaven7.android.imagepick;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heaven7.android.imagepick.page.BigImageAdapter;
import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.core.util.Toaster;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * @author heaven7
 */
public class SeeBigImageActivity extends BaseActivity {

    TextView mTv_upload;
    TextView mTv_indexes;

    ViewGroup mVg_select; //select group
    ImageView mIv_select;
    ViewPager mVp;

    ViewGroup mVg_top;
    ViewGroup mVg_bottom;

    private BigImageSelectParameter mParam;
    private List<IImageItem> mItems;
    private IImageItem mLastSingleItem;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_big_image;
    }

    @Override
    protected void init(Context context, Bundle savedInstanceState) {
        mTv_upload = findViewById(R.id.tv_upload);
        mTv_indexes = findViewById(R.id.tv_indexes);
        mVg_top = findViewById(R.id.vg_top);
        mVp = findViewById(R.id.vp);

        mVg_select = findViewById(R.id.vg_select);
        mVg_bottom = findViewById(R.id.vg_bottom);
        mIv_select = findViewById(R.id.iv_select);
        setListeners();

        mLastSingleItem = getIntent().getParcelableExtra(PickConstants.KEY_SINGLE_ITEM);
        mParam = getIntent().getParcelableExtra(PickConstants.KEY_PARAMS);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
        mVp.setAdapter(new PageAdapter0(mItems));
        setUiState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
    }

    @Override
    protected void onDestroy() {
        ImagePickDelegateImpl.getDefault().getImageItems().clear();
        super.onDestroy();
    }

    @Keep
    public void onClickBack(View view) {
        finish();
    }

    private void setListeners() {
        mTv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        mVg_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = mIv_select.getTag();
                if(tag != null){
                    setSelectState(false);
                }else {
                    setSelectState(true);
                }
            }
        });
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                setSelectOrder(i + 1);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
                if(i == ViewPager.SCROLL_STATE_IDLE){
                    setSelectOrder(mVp.getCurrentItem() + 1);
                }
            }
        });
    }
    private void setSelectState(boolean select){
        //check reach max
        if(select && hasFlag(PickConstants.FLAG_MULTI_SELECT)){
            if(mParam.getSelectCount() >= mParam.getMaxSelectCount()){
                Toaster.show(getApplication(), getString(R.string.lib_pick_select_reach_max));
                return;
            }
        }
        //set ui
        setImageBySelectState(select);
        IImageItem item = mItems.get(mParam.getCurrentOrder() - 1);
        //state changed
        if(item.isSelected() != select){
            mParam.addSelectedCount(select ? 1: -1);
            ImagePickDelegateImpl.getDefault().dispatchSelectStateChanged(item, select);
        }
        //handle for single select
        if(!hasFlag(PickConstants.FLAG_MULTI_SELECT)){
            //single select mode. target is select .last must be unselect
            if(select && mLastSingleItem != null){
                mLastSingleItem.setSelected(false);
            }
            mLastSingleItem = item;
        }
        item.setSelected(select);
        //set button text
        setSelectedText();
    }

    private void setImageBySelectState(boolean select) {
        if(!select){
            mIv_select.setTag(null);
            mIv_select.setImageResource(R.drawable.lib_pick_ic_unselect);
        }else {
            mIv_select.setTag(true);
            mIv_select.setImageResource(R.drawable.lib_pick_ic_selected);
        }
    }

    private void setSelectOrder(int order){
        mParam.setCurrentOrder(order);
        mTv_indexes.setText(mParam.getCurrentOrder() + "/" + mParam.getTotalCount());

        boolean isSelect = mItems.get(mParam.getCurrentOrder() - 1).isSelected();
        setImageBySelectState(isSelect);
    }
    private void setUiState() {
        setSelectOrder(mParam.getCurrentOrder());

        setTopHeight();
        setBottomHeight();
        setSelectedText();
        //handle position
        mVp.getAdapter().notifyDataSetChanged();
        mVp.setCurrentItem(mParam.getCurrentOrder() - 1);
    }

    private void setTopHeight() {
        mVg_top.setVisibility(hasFlag(PickConstants.FLAG_SHOW_TOP) ? View.VISIBLE : View.GONE);
    }
    private void setBottomHeight() {
        mVg_bottom.setVisibility(hasFlag(PickConstants.FLAG_SHOW_BOTTOM) ? View.VISIBLE : View.GONE);
        if(hasFlag(PickConstants.FLAG_SHOW_BOTTOM_END_BUTTON)){
            //current only have one button
            mVg_select.setVisibility(View.VISIBLE);
        }else {
            mVg_select.setVisibility(View.GONE);
        }
    }

    private void setSelectedText() {
        if(hasFlag(PickConstants.FLAG_SHOW_TOP_END_BUTTON)){
            mTv_upload.setVisibility(View.VISIBLE);
            String text = mParam.getTopRightText() == null ? getString(R.string.lib_pick_upload) : mParam.getTopRightText();
            if(hasFlag(PickConstants.FLAG_MULTI_SELECT)){
                text += String.format(Locale.getDefault(), "(%d/%d)", mParam.getSelectCount(), mParam.getMaxSelectCount());
                mTv_upload.setText(text);
            }else {
                mTv_upload.setText(text);
            }
        }else {
            mTv_upload.setVisibility(View.GONE);
        }
    }

    private boolean hasFlag(int flags){
        return (mParam.getFlags() & flags) == flags;
    }

    private class PageAdapter0 extends BigImageAdapter<IImageItem>{

        public PageAdapter0(List<IImageItem> mDatas) {
            super(false, mDatas);
        }
        @Override
        protected void onBindItem(ImageView iv, int index, IImageItem data) {
            RequestManager rm = Glide.with(iv.getContext());
            if(data.getFilePath() != null){
                rm
                        .load(new File(data.getFilePath()))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(iv);
            }else {
                rm
                        .load(data.getUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(iv);
            }
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hasFlag(PickConstants.FLAG_SHOW_TOP) || hasFlag(PickConstants.FLAG_SHOW_BOTTOM)){
                        mParam.deleteFlags(PickConstants.FLAG_SHOW_TOP | PickConstants.FLAG_SHOW_BOTTOM);
                    }else {
                        mParam.addFlags(PickConstants.FLAG_SHOW_TOP | PickConstants.FLAG_SHOW_BOTTOM);
                    }
                    setUiState();
                }
            });
        }
    }
}
