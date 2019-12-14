package com.heaven7.android.imagepick;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.SelectHelper;
import com.heaven7.adapter.util.ViewHelper2;
import com.heaven7.android.imagepick.page.GestureBigImageAdapter;
import com.heaven7.core.util.ViewHelper;
import com.heaven7.core.util.viewhelper.action.Getters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BrowseActivity extends BaseActivity {

    private RecyclerView mRv;
    private QuickRecycleViewAdapter<Item> mAdapter;
    private ViewPager mVp;
    private Adapter0 mPageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_browse;
    }
    @Override
    protected void init(Context context, Bundle savedInstanceState) {
        mVp = findViewById(R.id.vp);
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        setAdapter(context);
        Utils.closeDefaultAnimator(mRv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Item> items = createItems();
        mAdapter.getAdapterManager().replaceAllItems(items);
        if(items.size() > 0){
            mAdapter.getSelectHelper().select(0);
        }
        mPageAdapter.replaceItems(items);
    }

    private void setAdapter(Context context) {
        final List<Item> items = createItems();
        mVp.setAdapter(mPageAdapter = new Adapter0(items));

        final int margin = SystemConfig.dip2px(context, 10);
        final int round = SystemConfig.dip2px(context, 8);
        final int borderColor = Color.parseColor("#677DB8");
        final int border =  SystemConfig.dip2px(context, 1);
        mRv.setAdapter(mAdapter = new QuickRecycleViewAdapter<Item>(R.layout.lib_pick_item_image, items) {
            @Override
            protected void onBindData(final Context context, final int position, final Item item, int itemLayoutId, ViewHelper2 helper) {
                View rootView = helper.getRootView();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
                mlp.setMarginStart(position != 0 ? margin : 0);
                rootView.setLayoutParams(mlp);

                helper.performViewGetter(R.id.iv, new Getters.ImageViewGetter() {
                    @Override
                    public void onGotView(ImageView imageView, ViewHelper viewHelper) {
                        int bc = item.isSelected() ? borderColor : Color.TRANSPARENT;
                        BorderRoundTransformation borderTrans = new BorderRoundTransformation(context, round, 0, border, bc);
                        Glide.with(context)
                                .load(new File(item.file))
                                .bitmapTransform(new Transformation[]{new CenterCrop(context)
                                        , borderTrans})
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                    }
                }).setRootOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectHelper<Item> selectHelper = mAdapter.getSelectHelper();
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
        SelectHelper<Item> helper = mAdapter.getSelectHelper();
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
            List<Item> items = mAdapter.getAdapterManager().getItems();
            if(items.size() >= 2){
                //remove item
                Item deleteItem = items.get(posToDelete);
                Item item = items.get(showPos);
                mAdapter.getAdapterManager().removeItem(deleteItem);
                ImagePickDelegateImpl.getDefault().removeImagePath(deleteItem.file);
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

    private static List<Item> createItems() {
        List<Item> list = new ArrayList<>();
        List<String> images = ImagePickDelegateImpl.getDefault().getImages();
        for (String file : images){
            list.add(new Item(file));
        }
        return list;
    }

    private class Adapter0 extends GestureBigImageAdapter<Item>{

        public Adapter0(List<Item> mDatas) {
            super(false, mDatas, true);
        }
        @Override
        protected void onBindItem(ImageView iv, int index, Item data) {
            RequestManager rm = Glide.with(iv.getContext());
            if (data.getFilePath() != null) {
                rm
                        .load(new File(data.getFilePath()))
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(iv);
            }
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private static class Item extends BaseSelector {
        String file;
        public Item(String file) {
            this.file = file;
        }
        public String getFilePath() {
            return file;
        }
    }
}
