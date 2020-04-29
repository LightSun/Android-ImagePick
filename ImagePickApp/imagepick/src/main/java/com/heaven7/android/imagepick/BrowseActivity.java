package com.heaven7.android.imagepick;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.SelectHelper;
import com.heaven7.adapter.util.ViewHelper2;
import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.page.GestureBigImageAdapter;
import com.heaven7.android.imagepick.pub.module.ImageItem;
import com.heaven7.android.imagepick.pub.module.ImageOptions;
import com.heaven7.core.util.DimenUtil;
import com.heaven7.core.util.ViewHelper;
import com.heaven7.core.util.viewhelper.action.Getters;

import java.util.ArrayList;
import java.util.List;


public class BrowseActivity extends BaseActivity {

    private RecyclerView mRv;
    private QuickRecycleViewAdapter<ImageItem> mAdapter;
    private ViewPager mVp;
    private Adapter0 mPageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_browse;
    }
    @Override
    protected void initialize(Context context, Bundle savedInstanceState) {
        mVp = findViewById(R.id.vp);
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        setAdapter(context);
        Utils.closeDefaultAnimator(mRv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<ImageItem> items = createItems();
        mAdapter.getAdapterManager().replaceAllItems(items);
        if(items.size() > 0){
            mAdapter.getSelectHelper().select(0);
        }
        mPageAdapter.replaceItems(items);
    }

    private void setAdapter(Context context) {
        final List<ImageItem> items = createItems();
        mVp.setAdapter(mPageAdapter = new Adapter0(items));

        final int margin = DimenUtil.dip2px(context, 10);
        final int round = DimenUtil.dip2px(context, 8);
        final int borderColor = Color.parseColor("#677DB8");
        final int border =  DimenUtil.dip2px(context, 1);
        mRv.setAdapter(mAdapter = new QuickRecycleViewAdapter<ImageItem>(R.layout.lib_pick_item_image, items) {
            @Override
            protected void onBindData(final Context context, final int position, final ImageItem item, int itemLayoutId, ViewHelper2 helper) {
                final View rootView = helper.getRootView();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
                mlp.setMarginStart(position != 0 ? margin : 0);
                rootView.setLayoutParams(mlp);

                helper.performViewGetter(R.id.iv, new Getters.ImageViewGetter() {
                    @Override
                    public void onGotView(ImageView imageView, ViewHelper viewHelper) {
                        int bc = item.isSelected() ? borderColor : Color.TRANSPARENT;

                        ImageOptions options = new ImageOptions.Builder()
                                .setRound(round)
                                .setBorder(border)
                                .setBorderColor(bc)
                                .setTargetWidth(rootView.getWidth())
                                .setTargetHeight(rootView.getHeight())
                                .build();
                        ImagePickDelegateImpl.getDefault().getImageLoadDelegate().loadImage(BrowseActivity.this, imageView, item, options);
                    }
                }).setRootOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectHelper<ImageItem> selectHelper = mAdapter.getSelectHelper();
                        selectHelper.select(getAdapterManager().getItems().indexOf(item));
                    }
                });
            }
        });
    }

    @Keep
    public void onClickBack(View view){
        finish();
    }
    @Keep
    public void onClickDelete(View view){
        SelectHelper<ImageItem> helper = mAdapter.getSelectHelper();
        int[] poss = helper.getSelectPosition();
        if(poss != null && poss.length > 0){
            int posToDelete = poss[0];
            int showPos;
            if(posToDelete > 0){
                showPos = posToDelete - 1;
            }else {
                showPos = posToDelete + 1;
            }
            helper.clearSelectedPosition();
            //load
            List<ImageItem> items = mAdapter.getAdapterManager().getItems();
            if(items.size() >= 2){
                //remove item
                ImageItem deleteItem = items.get(posToDelete);
                ImageItem item = items.get(showPos);
                mAdapter.getAdapterManager().removeItem(deleteItem);
                ImagePickDelegateImpl.getDefault().removeImagePath(deleteItem.getFilePath());
                //select and load item
                mPageAdapter.removeItem(deleteItem);
                helper.select(items.indexOf(item));
            }else {
                mAdapter.getAdapterManager().clearItems();
                mPageAdapter.clearItems();
                ImagePickDelegateImpl.getDefault().clearImages();
            }
        }
    }

    private static List<ImageItem> createItems() {
        List<ImageItem> list = new ArrayList<>();
        List<String> images = ImagePickDelegateImpl.getDefault().getImages();
        for (String file : images){
            list.add(ImageItem.ofImage(file));
        }
        return list;
    }

    private class Adapter0 extends GestureBigImageAdapter<ImageItem>{

        public Adapter0(List<ImageItem> mDatas) {
            super(false, mDatas, true);
        }
        @Override
        protected void onBindItem(ImageView iv, int position, int index, ImageItem data) {
            ImagePickDelegateImpl.getDefault().getImageLoadDelegate().loadImage(BrowseActivity.this, iv, data, null);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
