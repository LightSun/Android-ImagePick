package com.heaven7.android.pick.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.heaven7.android.imagepick.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.pub.ImageParameter;
import com.heaven7.android.imagepick.pub.ImagePickDelegate;
import com.heaven7.android.imagepick.pub.ImagePickManager;
import com.heaven7.android.imagepick.pub.ImageSelectParameter;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

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

        ImagePickDelegateImpl.getDefault().setOnImageProcessListener(new ImagePickDelegate.OnImageProcessListener() {
            @Override
            public void onProcessStart(Activity activity, int totalCount) {

            }
            @Override
            public void onProcessUpdate(Activity activity, int finishCount, int totalCount) {

            }
            @Override
            public void onProcessEnd(Runnable next) {

            }
            @Override
            public boolean onProcessException(Activity activity, int order, int size, MediaResourceItem item, Exception e) {
                return false;
            }
        });
        test1();
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
                            //10M = 10485760 b .  Math.sqrt(10485760/4)
                            String cacheDir = Environment.getExternalStorageDirectory() + "/lib_pick";
                            ImagePickManager.get().getImagePickDelegate().startBrowseImages(EntryActivity.this,
                                    new ImageSelectParameter.Builder()
                                            .setImageParameter(new ImageParameter.Builder()
                                                    .setMaxWidth(1619)
                                                    .setMaxHeight(1619)
                                                    .build())
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
            switch (requestCode){
                //{"code":460,"message":"抱歉，文件大小超过限制","data":null}
                case REQ_CAMERA: {
                    ArrayList<String> images = data.getStringArrayListExtra(PickConstants.KEY_RESULT);
                    Logger.d("EntryActivity", "onActivityResult", "REQ_CAMERA >> " + images);
                   // uploadToServer(images);
                    break;
                }

                case REQ_GALLERY:
                    ArrayList<String> images = data.getStringArrayListExtra(PickConstants.KEY_RESULT);
                   // uploadToServer(images);
                    Logger.d("EntryActivity", "onActivityResult", "REQ_GALLERY >> " + images);
                    break;
            }
        }
    }

    private void test1() {
        int dpi = getResources().getDisplayMetrics().densityDpi;
        System.out.println("dpi = " + dpi);
        switch (dpi) {
            case DisplayMetrics.DENSITY_LOW:
                // ...
                System.out.println("DENSITY_LOW");
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                // ...
                System.out.println("DENSITY_MEDIUM");
                break;
            case DisplayMetrics.DENSITY_HIGH:
                // ...
                System.out.println("DENSITY_HIGH");
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                // ...
                System.out.println("DENSITY_XHIGH");
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                // ...
                System.out.println("DENSITY_XXHIGH");
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                // ...
                System.out.println("DENSITY_XHIGH");
                break;
        }
    }

    private void uploadToServer(ArrayList<String> images) {
        String url = "http://log.stable-test.bdfint.cn/app/api/v1/image";
        mComponent.ofUploadImages(url, images).jsonConsumer(new Consumer<String>() {
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
        }).subscribe();
    }
}
