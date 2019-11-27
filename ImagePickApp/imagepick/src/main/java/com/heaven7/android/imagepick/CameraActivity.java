package com.heaven7.android.imagepick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class CameraActivity extends BaseActivity implements CameraFragment.ActionCallback {

    @Override
    protected int getLayoutId() {
        return R.layout.ac_camera;
    }

    @Override
    protected void init(Context context, Bundle savedInstanceState) {
        CameraFragment fragment = new CameraFragment();
        fragment.setPictureCallback(new PictureCallbackImpl());
        fragment.setActionCallback(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.vg_container, fragment)
                .commit();
    }

    @Override
    public void onClickFinish() {
        //submit
        setResult(RESULT_OK, new Intent().putExtra(ImageSelectActivity.KEY_RESULT,
                new ArrayList<String>(ImagePickManager.getDefault().getImages())));
        finish();
    }
    @Override
    public void onClickImage() {
        startActivity(new Intent(this, BrowseActivity.class));
    }

    private class PictureCallbackImpl extends CameraFragment.PictureCallback{
        @Override
        protected void onTakePictureResult(String file) {
            ImagePickManager.getDefault().addImagePath(file);
        }
    }
}
