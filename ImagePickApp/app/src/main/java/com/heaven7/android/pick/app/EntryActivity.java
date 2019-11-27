package com.heaven7.android.pick.app;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.heaven7.android.imagepick.CameraActivity;
import com.heaven7.android.imagepick.ImageSelectActivity;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;

import java.util.ArrayList;

public class EntryActivity extends AppCompatActivity {

    private final PermissionHelper mHelper = new PermissionHelper(this);
    public static final int REQ_CAMERA  = 1;
    public static final int REQ_GALLERY = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_entry);
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
                            startActivityForResult(new Intent(EntryActivity.this, CameraActivity.class), REQ_CAMERA);
                        }
                    }
                });
    }

    public void onClickStartGallery(View view) {
        mHelper.startRequestPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new int[]{1, 2}, new PermissionHelper.ICallback() {
                    @Override
                    public void onRequestPermissionResult(String s, int i, boolean b) {
                        if (b) {
                            startActivityForResult(new Intent(EntryActivity.this, ImageSelectActivity.class), REQ_GALLERY);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQ_CAMERA: {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelectActivity.KEY_RESULT);
                    Logger.d("EntryActivity", "onActivityResult", "REQ_CAMERA >> " + images);
                    break;
                }
                    
                case REQ_GALLERY:
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelectActivity.KEY_RESULT);
                    Logger.d("EntryActivity", "onActivityResult", "REQ_GALLERY >> " + images);
                    break;
            }
        }
    }
}
