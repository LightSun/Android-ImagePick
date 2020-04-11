package com.heaven7.android.pick.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.android.imagepick.pub.CameraParameter;
import com.heaven7.android.imagepick.pub.ImageParameter;
import com.heaven7.android.imagepick.pub.ImagePickDelegate;
import com.heaven7.android.imagepick.pub.ImagePickManager;
import com.heaven7.android.imagepick.pub.ImageSelectParameter;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.SeeImageParameter;
import com.heaven7.android.imagepick.pub.delegate.DefaultSeeImageDelegate;
import com.heaven7.android.pick.app.impl.ImageLoadImpl;
import com.heaven7.core.util.BundleHelper;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;
import com.heaven7.java.base.anno.Nullable;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import pl.droidsonroids.gif.GifDrawable;

import static com.heaven7.android.imagepick.pub.PickConstants.REQ_CAMERA;
import static com.heaven7.android.imagepick.pub.PickConstants.REQ_GALLERY;

public class EntryActivity extends AppCompatActivity {

    private final PermissionHelper mHelper = new PermissionHelper(this);
    private RetrofitRxComponent mComponent;

    private static final String GIF = "/storage/emulated/0/Pictures/gangxin/27c233cf2d3f0516f75c1c4e88af5a0e.gif";

    @BindView(R.id.iv)
    ImageView mIv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_entry);
        ButterKnife.bind(this);
        mComponent = new RetrofitRxComponent();

        ImagePickManager.get().getImagePickDelegate().setImageLoadDelegate(new ImageLoadImpl());
        ImagePickManager.get().getImagePickDelegate().setOnImageProcessListener(new ImagePickDelegate.OnImageProcessListener() {
            @Override
            public void onProcessStart(Activity activity, int totalCount) {

            }
            @Override
            public void onProcessUpdate(Activity activity, int finishCount, int totalCount) {

            }
            @Override
            public boolean onProcessEnd(Runnable next) {
                return false;
            }
            @Override
            public boolean onProcessException(Activity activity, int order, int size, MediaResourceItem item, Exception e) {
                return false;
            }
        });
        VideoManager vm = new VideoManager(getApplicationContext());
        ImagePickManager.get().getImagePickDelegate().setVideoManageDelegate(vm);
        ImagePickManager.get().getImagePickDelegate().setOnPageChangeListener(vm);
        test1();
    }
    private void testGif(){
        long start = SystemClock.elapsedRealtime();
        try {
            GifDrawable gifDrawable = new GifDrawable(new File(GIF));
            System.out.println("cost  = " + (SystemClock.elapsedRealtime() - start));
            mIv.setImageDrawable(gifDrawable);
        }catch (Exception e){
            e.printStackTrace();
        }
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
                            ImagePickManager.get().getImagePickDelegate().startCamera(EntryActivity.this,
                                    new CameraParameter.Builder()
                                            .setMaxCount(4)
                                    .build());
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
                                            .setFlags(PickConstants.FLAG_IMAGE_AND_VIDEO)
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
    public void onClickSeeImage(View view){
        mHelper.startRequestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new int[]{1}, new PermissionHelper.ICallback() {
                    @Override
                    public void onRequestPermissionResult(String s, int i, boolean b) {
                        if (b) {
                            //testGif();
                            startWithGif();
                            //startWithoutGif();
                        }
                    }
                    @Override
                    public boolean handlePermissionHadRefused(String s, int i, Runnable runnable) {
                        return false;
                    }
                });

    }
    private void startWithGif(){
        SeeImageParameter parameter = new SeeImageParameter.Builder()
                .setPauseIconRes(R.drawable.ic_video_pause)
                .build();
        Bundle extra = new BundleHelper()
                .putBoolean(PickConstants.KEY_WITH_GIF, true)
                .getBundle();
        ImagePickManager.get().getImagePickDelegate().startBrowseImages2(EntryActivity.this,
                DefaultSeeImageDelegate.class, parameter, extra);
    }
    private void startWithoutGif(){
        SeeImageParameter parameter = new SeeImageParameter.Builder()
                .setPauseIconRes(R.drawable.ic_video_pause)
                .build();
        ImagePickManager.get().getImagePickDelegate().startBrowseImages2(EntryActivity.this, parameter);
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
