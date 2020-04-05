package com.heaven7.android.imagepick.pub.delegate;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.android.imagepick.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.R;
import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.PickConstants;

import java.util.Locale;

/*public*/ class DefaultTopBinder extends SeeBigImageDelegate.ViewBinder {

    private ImageView mIv_back;
    private TextView mTv_indexes;
    private TextView mTv_upload;

    public DefaultTopBinder(SeeBigImageDelegate delegate, ViewGroup parent) {
        super(delegate, parent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_vb_default_top;
    }

    @Override
    public void onBind() {
        mIv_back = getView().findViewById(R.id.iv_back);
        mTv_indexes = getView().findViewById(R.id.tv_indexes);
        mTv_upload = getView().findViewById(R.id.tv_upload);

        mIv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProvider().getActivity().finish();
            }
        });
        mTv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if select none. just select current
                SeeBigImageDelegate.Provider provider = getProvider();
                BigImageSelectParameter mParam = provider.getSelectParams();
                if(mParam.getSelectCount() == 0){
                    IImageItem item = provider.getImageItem(mParam.getCurrentOrder() - 1);
                    //IImageItem item = mItems.get(mParam.getCurrentOrder() - 1);
                    ImagePickDelegateImpl.getDefault().dispatchSelectStateChanged(item, true);
                }
                AppCompatActivity activity = provider.getActivity();
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });
    }

    @Override
    public void setSelectedText() {
        BigImageSelectParameter mParam = getProvider().getSelectParams();
        if (hasFlag(PickConstants.FLAG_SHOW_TOP_END_BUTTON)) {
            mTv_upload.setVisibility(View.VISIBLE);
            String text = mParam.getTopRightText() == null ? getResource().getString(R.string.lib_pick_upload) : mParam.getTopRightText();
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

    @Override
    public void setSelectOrder() {
        BigImageSelectParameter mParam = getProvider().getSelectParams();
        mTv_indexes.setText(mParam.getCurrentOrder() + "/" + mParam.getTotalCount());
    }

    @Override
    public void setSelectState(boolean select) {

    }

    private boolean hasFlag(int flag){
        BigImageSelectParameter params = getProvider().getSelectParams();
        return (params.getFlags() & flag) == flag;
    }
}
