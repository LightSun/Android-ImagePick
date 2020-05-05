package com.heaven7.android.imagepick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.cameraview.CameraView;
import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.internal.LibUtils;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.delegate.CameraUIDelegate;
import com.heaven7.android.imagepick.pub.delegate.impl.DefaultCameraUIDelegate;
import com.heaven7.android.imagepick.pub.module.CameraParameter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CameraActivity extends BaseActivity{

    private CameraUIDelegate mDelegate;

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_camera;
    }

    @Override
    protected void onPreSetContentView() {
        String dn = getIntent().getStringExtra(PickConstants.KEY_DELEGATE);
        if(dn == null){
            mDelegate = new DefaultCameraUIDelegate();
        }else {
            mDelegate = LibUtils.newInstance(dn);
        }
    }

    @Override
    protected void initialize(Context context, Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        
        CameraFragment fragment = new CameraFragment();
        mDelegate.setProvider(new Provider0(this, fragment));
        fragment.setCameraDelegate(mDelegate);
        fragment.setArguments(extras);
        fragment.setPictureCallback(new PictureCallbackImpl());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.vg_container, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        ImagePickDelegateImpl.getDefault().clearCameraImages();
        super.onDestroy();
    }
    /*public void onClickImage() {
        startActivity(new Intent(this, BrowseActivity.class));
    }*/

    private static class PictureCallbackImpl extends CameraFragment.PictureCallback{
        @Override
        protected void onTakePictureResult(String file) {
            ImagePickDelegateImpl.getDefault().addCameraImage(file);
        }
    }

    private static class Provider0 implements CameraUIDelegate.Provider{
        final WeakReference<Activity> weakAc;
        final WeakReference<CameraFragment> weakFrag;

        public Provider0(Activity ac, CameraFragment fragment) {
            this.weakAc = new WeakReference<>(ac);
            this.weakFrag = new WeakReference<>(fragment);
        }
        private CameraFragment getCameraFragment(){
            return weakFrag.get();
        }
        @Override
        public Activity getActivity() {
            return weakAc.get();
        }
        @Override
        public CameraParameter getParameter() {
            CameraFragment mFragment = getCameraFragment();
            return mFragment != null ?mFragment.getParameter():null;
        }
        @Override
        public CameraView getCameraView() {
            CameraFragment mFragment = getCameraFragment();
            return mFragment != null ?mFragment.getCameraView():null;
        }
        @Override
        public boolean isImageProcessing() {
            CameraFragment mFragment = getCameraFragment();
            return mFragment != null && mFragment.isImageProcessing();
        }
        @Override
        public void finishCamera() {
            Activity activity = getActivity();
            if(activity != null){
                //submit
                activity.setResult(RESULT_OK, new Intent().putExtra(PickConstants.KEY_RESULT,
                        new ArrayList<String>(ImagePickDelegateImpl.getDefault().getCameraImages())));
                activity.finish();
            }
        }
    }
}
