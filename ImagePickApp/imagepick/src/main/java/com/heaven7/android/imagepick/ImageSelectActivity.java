package com.heaven7.android.imagepick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heaven7.adapter.ISelectable;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.RecyclerViewUtils;
import com.heaven7.adapter.util.ViewHelper2;
import com.heaven7.core.util.Toaster;
import com.heaven7.core.util.ViewHelper;
import com.heaven7.core.util.viewhelper.action.Getters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * the image select activity.
 */
public class ImageSelectActivity extends BaseActivity implements MediaResourceHelper.Callback {

    public static final String KEY_RESULT = "result";
    private TextView mTv_upload;
    private RecyclerView mRv;

    private MediaResourceHelper mMediaHelper;
    private int mItemWidth;
    private int mItemHeight;
    //may be config from intent
    private int mSpanCount = 4;
    private int mSpace = 0;
    private int mAspectX = 1;
    private int mAspectY = 1;
    private int mMaxSelect = 8;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_image_select;
    }

    @Override
    protected void init(Context context, Bundle savedInstanceState) {
        mMediaHelper = new MediaResourceHelper(this);
        int width = getWidth();
        mItemWidth = (width - mSpace * (mSpanCount - 1)) / mSpanCount;
        //x / y = mItemWidth / mItemHeight
        mItemHeight = mAspectY * mItemWidth / mAspectX;

        mTv_upload = findViewById(R.id.tv_upload);
        mRv = findViewById(R.id.rv);
        mTv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Adapter0 adapter = (Adapter0) mRv.getAdapter();
                List<MediaResourceHelper.MediaResourceItem> items = adapter.getSelectHelper().getSelectedItems();
                if(items == null || items.size() == 0){
                    return;
                }
                ArrayList<String> files = new ArrayList<>();
                for (MediaResourceHelper.MediaResourceItem item: items){
                    files.add(item.getFilePath());
                }
                setResult(RESULT_OK, new Intent().putExtra(KEY_RESULT, files));
                finish();
            }
        });
        setAdapter();
        setSelectText();

        mMediaHelper.getMediaResource(MediaResourceHelper.FLAG_IMAGE, this);
    }

    @Override
    protected void onDestroy() {
        mMediaHelper.cancel();
        super.onDestroy();
    }

    @Keep
    public void onClickClose(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void setAdapter() {
        Adapter0 adapter = new Adapter0(null);
        GridLayoutManager layoutManager = RecyclerViewUtils.createGridLayoutManager(adapter, this, mSpanCount);
        mRv.setLayoutManager(layoutManager);
        mRv.setAdapter(adapter);
    }

    private int getWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void setSelectText() {
        //1 no need
        if(mMaxSelect > 1){
            Adapter0 adapter = (Adapter0) mRv.getAdapter();
            List<MediaResourceHelper.MediaResourceItem> items = adapter.getSelectHelper().getSelectedItems();
            int size = items != null ? items.size() : 0;
            String text = getString(R.string.upload) + String.format("(%d/%d)", size , mMaxSelect);
            mTv_upload.setText(text);
        }
    }

    @Override
    public void onCallback(List<MediaResourceHelper.MediaResourceItem> photoes, List<MediaResourceHelper.MediaResourceItem> videoes) {
        Adapter0 adapter = (Adapter0) mRv.getAdapter();
        adapter.getAdapterManager().replaceAllItems(photoes);
    }

    private class Adapter0 extends QuickRecycleViewAdapter<MediaResourceHelper.MediaResourceItem> {

        public Adapter0(List<MediaResourceHelper.MediaResourceItem> mDatas) {
            super(R.layout.item_select_image, mDatas, mMaxSelect == 1 ? ISelectable.SELECT_MODE_SINGLE : ISelectable.SELECT_MODE_MULTI);
        }

        @Override
        protected void onBindData(final Context context, final int position, final MediaResourceHelper.MediaResourceItem item,
                                  int itemLayoutId, ViewHelper2 helper) {
            View rootView = helper.getRootView();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
            lp.width = mItemWidth;
            lp.height = mItemHeight;
            lp.setMarginStart(position != 0 ? mSpace : 0);
            rootView.setLayoutParams(lp);

            helper.setImageResource(R.id.iv_select_state, item.isSelected() ? R.drawable.ic_selected : R.drawable.ic_unselect)
                    .performViewGetter(R.id.iv, new Getters.ImageViewGetter() {
                        @Override
                        public void onGotView(ImageView imageView, ViewHelper viewHelper) {
                            Glide.with(context)
                                    .load(new File(item.getFilePath()))
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                        }
                    }).setOnClickListener(R.id.iv_select_state, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<MediaResourceHelper.MediaResourceItem> items = getSelectHelper().getSelectedItems();
                    if(mMaxSelect > 1 && items != null && items.size() >= mMaxSelect){
                        Toaster.show(v.getContext(), getString(R.string.select_reach_max));
                        return;
                    }
                    getSelectHelper().toggleSelect(position);
                    setSelectText();
                }
            });
            //  Glide.with(context).load(new File(item.getFilePath())).
        }
    }
}
