package com.heaven7.android.imagepick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.cameraview.CameraView;
import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.delegate.CameraUIDelegate;
import com.heaven7.android.imagepick.pub.module.CameraParameter;
import com.heaven7.android.imagepick.pub.module.ImageParameter;
import com.heaven7.core.util.ImageParser;
import com.heaven7.core.util.MainWorker;
import com.heaven7.core.util.Toaster;
import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.base.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * camera fragment
 * @author heaven7
 */
public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";
    private CameraView mCameraView;

    private PictureCallback mPictureCallback;
    private final ThreadHelper mThreadHelper = new ThreadHelper();

    private final AtomicBoolean mProcessing = new AtomicBoolean(false);
    private File mSaveDir;
    private ImageParser mImgParser;
    private CameraParameter mCameraParam;

    private CameraUIDelegate mCameraDelegate;

    public void setCameraDelegate(CameraUIDelegate cameraDelegate) {
        this.mCameraDelegate = cameraDelegate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(mCameraDelegate.getLayoutId(), container, false);
        //prepare callback #initialize
        Bundle arguments = getArguments();
        mCameraParam = arguments !=null ? (CameraParameter) arguments.getParcelable(PickConstants.KEY_PARAMS) : null;
        Intent intent = new Intent();
        if(arguments != null){
            intent.putExtras(arguments);
        }
        mCameraDelegate.initialize(this, view, intent);
        //camera view
        mCameraView = view.findViewById(R.id.lib_pick_camera);
        if(mCameraView == null){
            throw new IllegalStateException("must provide camera view.");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSaveDir();
        handleArguments();

        mCameraView.setFlash(CameraView.FLASH_AUTO);
        mCameraView.addCallback(mPictureCallback);
        mCameraView.addCallback(new InternalCallback());
        mCameraDelegate.setCameraEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> images = ImagePickDelegateImpl.getDefault().getCameraImages();
        //Logger.d("CameraFragment", "onResume", "" + images);
        if(images.isEmpty()){
            mCameraDelegate.onEmptyImage();
        }
        //check if max reach and remove some.
        if(mCameraParam != null && mCameraParam.getMaxCount() > 0 &&
                images.size() < mCameraParam.getMaxCount() ){
            mCameraDelegate.setReachMax(false);
        }
        //for some phone have bug . when jump to another activity, then return. like samsung.
        //so just use a delay message to start camera.
        MainWorker.postDelay(200, new Runnable() {
            @Override
            public void run() {
                if(isDetached()){
                    return;
                }
                try {
                    mCameraView.start();
                }catch (Exception e){
                    FragmentActivity activity = getActivity();
                    if(activity != null && !ImagePickDelegateImpl.getDefault().handleException(activity, PickConstants.CODE_CAMERA, e)){
                        Toaster.show(activity, activity.getString(R.string.lib_pick_camera_failed));
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    public void onDetach() {
        quit();
        super.onDetach();
    }
    public void setPictureCallback(PictureCallback pictureCallback) {
        this.mPictureCallback = pictureCallback;
    }
    public CameraParameter getParameter() {
        return mCameraParam;
    }
    private void handleArguments() {
        Bundle arguments = getArguments();
        if(arguments != null){
            if(mCameraParam == null){
                mImgParser = new ImageParser(4000, 4000,
                        Bitmap.Config.RGB_565, true);
                mCameraView.setAutoFocus(true);
            }else {
                ImageParameter ip = mCameraParam.getImageParameter();
                mImgParser = Utils.createImageParser(ip, true);
                mCameraView.setAutoFocus(mCameraParam.isAutoFocus());
            }
        }else {
            mImgParser = new ImageParser(4000, 4000,
                    Bitmap.Config.RGB_565, true);
            mCameraView.setAutoFocus(true);
        }
    }
    private void setSaveDir() {
        String dir = null;
        if(getArguments() != null){
            dir = getArguments().getString(PickConstants.KEY_SAVE_DIR);
        }
        if(dir == null){
            dir = Environment.getExternalStorageDirectory() + "/lib_pick";
        }
        if(!TextUtils.isEmpty(dir)){
            File file = new File(dir);
            if(!file.exists()){
                file.mkdirs();
            }
            mSaveDir = file;
        }
    }
    private Handler getBackgroundHandler() {
        return mThreadHelper.getBackgroundHandler();
    }

    private void quit(){
        mThreadHelper.quit(false);
    }

    public CameraView getCameraView() {
        return mCameraView;
    }

    public boolean isImageProcessing() {
        return mProcessing.get();
    }

    public static abstract class PictureCallback extends CameraView.Callback{

        /**
         * called when you want save picture file com.heaven7.android.imagepick.internal. default return true.
         * @return true .
         */
        protected boolean shouldSavePicture(){
            return true;
        }
        /**
         * called on take picture result
         * @param file the file. null means failed.
         */
        protected abstract void onTakePictureResult(String file);
    }

    private class InternalCallback extends CameraView.Callback{
        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            if(!mProcessing.compareAndSet(false, true)){
                return;
            }
            if(!mPictureCallback.shouldSavePicture()){
                mCameraDelegate.setCameraEnabled(false);
                mProcessing.compareAndSet(true, false);
                return;
            }
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    Activity context = getActivity();
                    if(context == null){
                        mProcessing.compareAndSet(true, false);
                        return;
                    }
                    //show dialog
                    ImagePickDelegateImpl.getDefault().onImageProcessStart(context, 0);
                    final File file = new File(mSaveDir, System.currentTimeMillis() + ".jpg");
                    OutputStream os = null;
                    try {
                        Bitmap bitmap = mImgParser.parseToBitmap(data);
                        os = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
                        os.flush();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                        mPictureCallback.onTakePictureResult(null);
                        return;
                    } finally {
                        IOUtils.closeQuietly(os);
                        mProcessing.compareAndSet(true, false);
                    }
                    //do next
                    ImagePickDelegateImpl.getDefault().onImageProcessEnd(context, new Runnable() {
                        @Override
                        public void run() {
                            mPictureCallback.onTakePictureResult(file.getAbsolutePath());
                            mCameraDelegate.applyImageFile(file.getAbsolutePath());
                        }
                    });
                }
            });
        }
    }
}
