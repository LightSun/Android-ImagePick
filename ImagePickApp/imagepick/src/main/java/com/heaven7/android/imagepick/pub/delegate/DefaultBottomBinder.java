package com.heaven7.android.imagepick.pub.delegate;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heaven7.android.imagepick.R;

/*public*/ class DefaultBottomBinder extends SeeBigImageDelegate.ViewBinder {

    private ViewGroup mVg_select;
    private ImageView mIv_select;
    private TextView mTv_select;

    public DefaultBottomBinder(SeeBigImageDelegate delegate, ViewGroup parent) {
        super(delegate, parent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_vb_default_bottom;
    }

    @Override
    public void onBind() {
        mVg_select = getView().findViewById(R.id.vg_select);
        mIv_select = getView().findViewById(R.id.iv_select);
        mTv_select = getView().findViewById(R.id.tv_select);

        mVg_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProvider().onClickSelect(v);
            }
        });
    }
    @Override
    public void setSelectState(boolean select) {
        if (!select) {
            mVg_select.setTag(null);
            mIv_select.setImageResource(R.drawable.lib_pick_ic_unselect);
        } else {
            mVg_select.setTag(true);
            mIv_select.setImageResource(R.drawable.lib_pick_ic_selected);
        }
    }

    @Override
    public void setBottomEndVisible(boolean visible) {
        //current only have one button
        mVg_select.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
