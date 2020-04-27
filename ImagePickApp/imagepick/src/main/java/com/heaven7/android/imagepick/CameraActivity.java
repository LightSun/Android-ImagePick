package com.heaven7.android.imagepick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.pub.PickConstants;

import java.util.ArrayList;

public class CameraActivity extends BaseActivity implements CameraFragment.ActionCallback {

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_camera;
    }

    @Override
    protected void initialize(Context context, Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        
        CameraFragment fragment = new CameraFragment();
        fragment.setArguments(extras);
        fragment.setPictureCallback(new PictureCallbackImpl());
        fragment.setActionCallback(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.vg_container, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        ImagePickDelegateImpl.getDefault().clearImages();
        super.onDestroy();
    }

    @Override
    public void onClickFinish() {
        //submit
        setResult(RESULT_OK, new Intent().putExtra(PickConstants.KEY_RESULT,
                new ArrayList<String>(ImagePickDelegateImpl.getDefault().getImages())));
        finish();
    }
    @Override
    public void onClickImage() {
        startActivity(new Intent(this, BrowseActivity.class));
    }

    private class PictureCallbackImpl extends CameraFragment.PictureCallback{
        @Override
        protected void onTakePictureResult(String file) {
            ImagePickDelegateImpl.getDefault().addImagePath(file);
        }
    }
}
