package com.heaven7.android.pick.app;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
                            ImagePickManager.get().getImagePickDelegate().startCamera(EntryActivity.this);
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
                            ImagePickManager.get().getImagePickDelegate().startBrowseImages(EntryActivity.this,
                                    new ImageSelectParameter.Builder()
                                            .setFlags(PickConstants.FLAG_IMAGE_AND_VIDEO)
                                            .setMaxSelect(1)
                                    .build());
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
                    ArrayList<String> images = data.getStringArrayListExtra(PickConstants.KEY_RESULT);
                    Logger.d("EntryActivity", "onActivityResult", "REQ_CAMERA >> " + images);
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
