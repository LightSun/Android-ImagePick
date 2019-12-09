package com.heaven7.android.pick.app;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.heaven7.android.imagepick.pub.ImageParameter;
import com.heaven7.android.imagepick.pub.ImagePickManager;
import com.heaven7.android.imagepick.pub.ImageSelectParameter;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;

import java.util.ArrayList;

import static com.heaven7.android.imagepick.pub.PickConstants.REQ_CAMERA;
import static com.heaven7.android.imagepick.pub.PickConstants.REQ_GALLERY;

public class EntryActivity extends AppCompatActivity {

    private final PermissionHelper mHelper = new PermissionHelper(this);
    private RetrofitRxComponent mComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_entry);
        mComponent = new RetrofitRxComponent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onClickStartCamera(View view) {
        mHelper.startRequestPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new int[]{1, 2}, new PermissionHelper.ICallback() {
                    @Override
                    public void onRequestPermissionResult(String s, int i, boolean b) {
                        if (b) {
                            ImagePickManager.get().getImagePickDelegate().startCamera(EntryActivity.this);
                        }
                    }
                    @Override
                    public boolean handlePermissionHadRefused(String s, int i, Runnable runnable) {
                        return false;
                    }
                });
    }

    public void onClickStartGallery(View view) {
        mHelper.startRequestPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new int[]{1, 2}, new PermissionHelper.ICallback() {
                    @Override
                    public void onRequestPermissionResult(String s, int i, boolean b) {
                        if (b) {
                            String cacheDir = Environment.getExternalStorageDirectory() + "/lib_pick";
                            ImagePickManager.get().getImagePickDelegate().startBrowseImages(EntryActivity.this,
                                    new ImageSelectParameter.Builder()
                                            .setImageParameter(ImageParameter.DEFAULT)
                                            .setCacheDir(cacheDir)
                                           // .setFlags(PickConstants.FLAG_IMAGE_AND_VIDEO)
                                            .setMaxSelect(4)
                                    .build());
                        }
                    }
                    @Override
                    public boolean handlePermissionHadRefused(String s, int i, Runnable runnable) {
                        return false;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String url = "http://log.stable-test.bdfint.cn/app/api/v1/image";
            switch (requestCode){
                //{"code":460,"message":"抱歉，文件大小超过限制","data":null}
                case REQ_CAMERA: {
                    ArrayList<String> images = data.getStringArrayListExtra(PickConstants.KEY_RESULT);
                    Logger.d("EntryActivity", "onActivityResult", "REQ_CAMERA >> " + images);
                   /* mComponent.ofUploadImages(url, images).jsonConsumer(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Logger.d("EntryActivity", "accept", "" + s);
                        }
                    }).error(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    }).finishTask(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }).subscribe();*/
                    break;
                }
                    
                case REQ_GALLERY:
                    ArrayList<String> images = data.getStringArrayListExtra(PickConstants.KEY_RESULT);
                    Logger.d("EntryActivity", "onActivityResult", "REQ_GALLERY >> " + images);
                    break;
            }
        }
    }
}
