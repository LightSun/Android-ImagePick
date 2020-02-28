package com.heaven7.android.imagepick;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.viewpager.widget.LibPick$_ViewPagerUtils;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heaven7.android.imagepick.page.AbstractMediaPageAdapter;
import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.core.util.MainWorker;
import com.heaven7.core.util.Toaster;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private MediaAdapter mMediaAdapter;
    private final AtomicBoolean mPendingPlayed = new AtomicBoolean(false);

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_big_image;
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
        //setup data
        mLastSingleItem = getIntent().getParcelableExtra(PickConstants.KEY_SINGLE_ITEM);
        mParam = getIntent().getParcelableExtra(PickConstants.KEY_PARAMS);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
        //set media adapter
        mMediaAdapter = new MediaAdapter(mItems);
        mMediaAdapter.setSupportGestureImage(mParam.isSupportGestureImage());
        mVp.setAdapter(mMediaAdapter);

        //set ui state
        setUiState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mItems = ImagePickDelegateImpl.getDefault().getImageItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaAdapter.onPause(this, mVp.getCurrentItem(), LibPick$_ViewPagerUtils.getCurrentView(mVp));
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        mMediaAdapter.onResume(this, mVp.getCurrentItem(), LibPick$_ViewPagerUtils.getCurrentView(mVp));
        startPlay();
    }
    @Override
    protected void onDestroy() {
        mMediaAdapter.onDestroy(this, mVp.getCurrentItem(), LibPick$_ViewPagerUtils.getCurrentView(mVp));
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
                //if select none. just select current
                if(mParam.getSelectCount() == 0){
                    IImageItem item = mItems.get(mParam.getCurrentOrder() - 1);
                    ImagePickDelegateImpl.getDefault().dispatchSelectStateChanged(item, true);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
        mVg_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = mIv_select.getTag();
                if (tag != null) {
                    setSelectState(false);
                } else {
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
                System.out.println("onPageSelected");
                setSelectOrder(i + 1);
                startPlay();
            }
            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE) {
                    System.out.println("onPageScrollStateChanged: SCROLL_STATE_IDLE");
                    setSelectOrder(mVp.getCurrentItem() + 1);
                    startPlay();
                }
            }
        });
    }
    private void startPlay(){
        if(mPendingPlayed.compareAndSet(false, true)){
            startPlay0();
        }
    }
    private void startPlay0(){
        final View currentView = LibPick$_ViewPagerUtils.getCurrentView(mVp);
        if(currentView == null){
            MainWorker.postDelay(20, new Runnable() {
                @Override
                public void run() {
                    startPlay0();
                }
            });
        }else {
            if(mPendingPlayed.compareAndSet(true, false)){
                mMediaAdapter.startPlay(getApplicationContext(), mVp.getCurrentItem(), currentView);
            }
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
        if (!select) {
            mIv_select.setTag(null);
            mIv_select.setImageResource(R.drawable.lib_pick_ic_unselect);
        } else {
            mIv_select.setTag(true);
            mIv_select.setImageResource(R.drawable.lib_pick_ic_selected);
        }
    }

    private void setSelectOrder(int order) {
        mParam.setCurrentOrder(order);
        mTv_indexes.setText(mParam.getCurrentOrder() + "/" + mParam.getTotalCount());

        boolean isSelect = mItems.get(mParam.getCurrentOrder() - 1).isSelected();
        setImageBySelectState(isSelect);
    }

    private void setUiState() {
        setSelectOrder(mParam.getCurrentOrder());

        mVg_top.setVisibility(hasFlag(PickConstants.FLAG_SHOW_TOP) ? View.VISIBLE : View.GONE);
        mVg_bottom.setVisibility(hasFlag(PickConstants.FLAG_SHOW_BOTTOM) ? View.VISIBLE : View.GONE);
        if (hasFlag(PickConstants.FLAG_SHOW_BOTTOM_END_BUTTON)) {
            //current only have one button
            mVg_select.setVisibility(View.VISIBLE);
        } else {
            mVg_select.setVisibility(View.GONE);
        }
        setSelectedText();
        //handle position
        mVp.getAdapter().notifyDataSetChanged();
        mVp.setCurrentItem(mParam.getCurrentOrder() - 1);
    }

    private void setSelectedText() {
        if (hasFlag(PickConstants.FLAG_SHOW_TOP_END_BUTTON)) {
            mTv_upload.setVisibility(View.VISIBLE);
            String text = mParam.getTopRightText() == null ? getString(R.string.lib_pick_upload) : mParam.getTopRightText();
            if (hasFlag(PickConstants.FLAG_MULTI_SELECT) && mParam.getSelectCount() > 0) {
                text += String.format(Locale.getDefault(), "(%d/%d)", mParam.getSelectCount(), mParam.getMaxSelectCount());
                mTv_upload.setText(text);
            } else {
                mTv_upload.setText(text);
            }
        } else {
            mTv_upload.setVisibility(View.GONE);
        }
    }

    private boolean hasFlag(int flags) {
        return (mParam.getFlags() & flags) == flags;
    }
    private class MediaAdapter extends AbstractMediaPageAdapter{

        public MediaAdapter(List<? extends IImageItem> mDatas) {
            super(mDatas);
        }
        @Override
        protected void onBindImageItem(ImageView iv, int index, IImageItem data) {
            RequestManager rm = Glide.with(iv.getContext());
            if (data.getFilePath() != null) {
                rm
                        .load(new File(data.getFilePath()))
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(iv);
            } else {
                rm
                        .load(data.getUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv);
            }
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasFlag(PickConstants.FLAG_SHOW_TOP) || hasFlag(PickConstants.FLAG_SHOW_BOTTOM)) {
                        mParam.deleteFlags(PickConstants.FLAG_SHOW_TOP | PickConstants.FLAG_SHOW_BOTTOM);
                    } else {
                        mParam.addFlags(PickConstants.FLAG_SHOW_TOP | PickConstants.FLAG_SHOW_BOTTOM);
                    }
                    setUiState();
                }
            });
        }
    }
}
