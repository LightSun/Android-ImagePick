package com.heaven7.android.imagepick.pub.delegate.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.google.android.cameraview.CameraView;
import com.heaven7.android.imagepick.R;
import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.pub.delegate.CameraUIDelegate;
import com.heaven7.android.imagepick.pub.module.CameraParameter;
import com.heaven7.android.imagepick.pub.module.ImageItem;
import com.heaven7.android.imagepick.pub.module.ImageOptions;
import com.heaven7.core.util.DimenUtil;
import com.heaven7.core.util.Toaster;

public class DefaultCameraUIDelegate extends CameraUIDelegate {

    private LifecycleOwner mOwner;

    private ImageView mIv_image;
    private ImageView mIv_camera;
    private ImageView mIv_flash;

    private TextView mTv_finish;


    @Override
    public int getLayoutId() {
        return R.layout.lib_pick_frag_camera_main;
    }

    @Override
    public void initialize(LifecycleOwner owner, View view, Intent intent) {
        this.mOwner = owner;
        mIv_image = view.findViewById(R.id.iv_image);
        mIv_camera = view.findViewById(R.id.iv_camera);
        mTv_finish = view.findViewById(R.id.tv_finish);
        mIv_flash = view.findViewById(R.id.iv_flash);

        mIv_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getProvider().isImageProcessing()){
                    return;
                }
                CameraView mCameraView = getProvider().getCameraView();
                if(mCameraView.getFlash() == CameraView.FLASH_AUTO){
                    mIv_flash.setImageResource(R.drawable.lib_pick_ic_flash_off);
                    mCameraView.setFlash(CameraView.FLASH_OFF);
                }else {
                    mIv_flash.setImageResource(R.drawable.lib_pick_ic_flash_on);
                    mCameraView.setFlash(CameraView.FLASH_ON);
                }
            }
        });
        mIv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getProvider().isImageProcessing()){
                    return;
                }
                if(v.getTag() != null){
                    //re-camera
                    setCameraEnabled(true);
                }else {
                    CameraParameter mCameraParam = getParameter();
                    //normal
                    if(mCameraParam != null && mCameraParam.getMaxCount() > 0 &&
                            ImagePickDelegateImpl.getDefault().getCameraImages().size() >= mCameraParam.getMaxCount()){
                        setCameraEnabled(false);
                        setReachMax(true);
                        Toaster.show(v.getContext(), getResources().getString(R.string.lib_pick_camera_reach_max, mCameraParam.getMaxCount()));
                        return;
                    }
                    getProvider().getCameraView().takePicture();
                }
            }
        });
        mIv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getProvider().isImageProcessing()){
                    return;
                }
                onClickImage();
            }
        });
        mTv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getProvider().isImageProcessing()){
                    return;
                }
                getProvider().finishCamera();
            }
        });
    }
    @Override
    public void setCameraEnabled(boolean enabled){
        if(enabled){
            mIv_camera.setImageResource(R.drawable.lib_pick_ic_camera);
            mIv_camera.setTag(null);
            mIv_image.setVisibility(View.GONE);
            mTv_finish.setVisibility(View.GONE);
        }else {
            mIv_camera.setImageResource(R.drawable.lib_pick_ic_re_camera);
            mIv_camera.setTag(true);
            mIv_image.setVisibility(View.VISIBLE);
            mTv_finish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void applyImageFile(String path) {
        setImageFile(path);
    }
    @Override
    public void setReachMax(boolean reachMax) {
        if(reachMax){
            mIv_camera.setEnabled(false);
        }else {
            mIv_camera.setEnabled(true);
        }
    }

    @Override
    public void onEmptyImage() {
        mIv_image.setVisibility(View.GONE);
    }

    private void setImageFile(final String file){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Context context = getActivity();
                if(context == null){
                    return;
                }
                ViewGroup.LayoutParams lp = mIv_image.getLayoutParams();
                int round = DimenUtil.dip2px(context, 8);
                setCameraEnabled(false);

                ImageOptions options = new ImageOptions.Builder()
                        .setRound(round)
                        .setBorder(1)
                        .setBorderColor(Color.TRANSPARENT)
                        .setCacheFlags(ImageOptions.CACHE_FLAG_DATA | ImageOptions.CACHE_FLAG_RESOURCE)
                        .setTargetWidth(lp.width)
                        .setTargetHeight(lp.height)
                        .build();
                ImagePickDelegateImpl.getDefault().getImageLoadDelegate().loadImage(mOwner, mIv_image, ImageItem.ofImage(file), options);
            }
        });
    }
    /**
     * called on click image. which is from camera
     */
    protected void onClickImage(){

    }
}
