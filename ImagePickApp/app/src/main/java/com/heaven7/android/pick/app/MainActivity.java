/*
package com.heaven7.android.pick.app;


import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.heaven7.android.imagepick0.CameraFragment;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements CameraFragment.Callback, CameraFragment.IBitmapDelegate {

    @BindView(R.id.iv_image)
    ImageView mIv_image;

    @BindView(R.id.vg_container)
    ViewGroup mVg_container;

    private CameraFragment mFragment;
    private PermissionHelper mHelper = new PermissionHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mIv_image.setVisibility(View.GONE);

        mHelper.startRequestPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new int[]{1, 2}, new PermissionHelper.ICallback() {
            @Override
            public void onRequestPermissionResult(String s, int i, boolean b) {
                if(b){
                   // setCameraFragment();
                    testCropLib();
                }
            }
        });
    }

    private void testCropLib() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.bt_req_image)
    public void onClickRequestBitmap(View view){
        mFragment.requestBitmap(this);
    }

    @OnClick(R.id.iv_image)
    public void onClickImage(View view){
        mIv_image.setVisibility(View.GONE);
    }

    @Override
    public void onInitFailed(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onInitSuccess() {
        Logger.d("MainActivity", "onInitSuccess", "");
    }

    @Override
    public void onBitmap(Bitmap bitmap) {
        Logger.d("MainActivity", "onBitmap", "bitmap = " + bitmap);
        if(bitmap != null){
            mIv_image.setImageBitmap(bitmap);
            mIv_image.setVisibility(View.VISIBLE);
        }
    }
    private void setCameraFragment() {
        mFragment = new CameraFragment();
        mFragment.setCallback(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.vg_container, mFragment)
                .commit();
    }
}
*/
