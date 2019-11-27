package com.heaven7.android.imagepick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.cameraview.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * camera fragment
 */
public class CameraFragment extends Fragment{

    public static final String KEY_SAVE_DIR = "save_dir";
    private static final String TAG = "CameraFragment";
    private CameraView mCameraView;

    private ImageView mIv_image;
    private ImageView mIv_camera;
    private ImageView mIv_flash;

    private TextView mTv_finish;

    private PictureCallback mPictureCallback;
    private ActionCallback mActionCallback;
    private Handler mBackgroundHandler;

    private File mSaveDir;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_camera_main, container, false);
        mCameraView = view.findViewById(R.id.cameraView);
        mIv_image = view.findViewById(R.id.iv_image);
        mIv_camera = view.findViewById(R.id.iv_camera);
        mTv_finish = view.findViewById(R.id.tv_finish);
        mIv_flash = view.findViewById(R.id.iv_flash);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSaveDir();

        mCameraView.setFlash(CameraView.FLASH_AUTO);
        mCameraView.addCallback(mPictureCallback);
        mCameraView.addCallback(new InternalCallback());
        mIv_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCameraView.getFlash() == CameraView.FLASH_AUTO){
                    mIv_flash.setImageResource(R.drawable.ic_flash_off);
                    mCameraView.setFlash(CameraView.FLASH_OFF);
                }else {
                    mIv_flash.setImageResource(R.drawable.ic_flash_on);
                    mCameraView.setFlash(CameraView.FLASH_ON);
                }
            }
        });
        mIv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() != null){
                    //re-camera
                    setCameraEnabled(true);
                }else {
                    //normal
                    mCameraView.takePicture();
                }
            }
        });
        mIv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionCallback != null){
                    mActionCallback.onClickImage();
                }
            }
        });
        mTv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionCallback != null ){
                    mActionCallback.onClickFinish();
                }
            }
        });
        setCameraEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
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

    public void setActionCallback(ActionCallback mActionCallback) {
        this.mActionCallback = mActionCallback;
    }
    public void setPictureCallback(PictureCallback pictureCallback) {
        this.mPictureCallback = pictureCallback;
    }
    private void setSaveDir() {
        if(getArguments() != null){
            String dir = getArguments().getString(KEY_SAVE_DIR);
            if(!TextUtils.isEmpty(dir)){
                File file = new File(dir);
                if(!file.exists()){
                    file.mkdirs();
                }
                mSaveDir = file;
            }
        }
        if(mSaveDir == null){
            mSaveDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
    }
    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }
    private void quit(){
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }
    private void runOnUi(Runnable r){
        FragmentActivity activity = getActivity();
        if(activity != null){
            activity.runOnUiThread(r);
        }
    }
    private void setImageFile(final String file){
         runOnUi(new Runnable() {
             @Override
             public void run() {
                 Context context = getContext();
                 if(context == null){
                     return;
                 }
                 int round = SystemConfig.dip2px(context, 8);
                 setCameraEnabled(false);
                 Glide.with(context)
                         .load(new File(file))
                         .bitmapTransform(new Transformation[]{new CenterCrop(context)
                                 , new BorderRoundTransformation(context, round, 0, 1, Color.TRANSPARENT)})
                         .dontAnimate()
                         .diskCacheStrategy(DiskCacheStrategy.ALL)
                         .into(mIv_image);
             }
         });
    }
    private void setCameraEnabled(boolean enabled){
        if(enabled){
            mIv_camera.setImageResource(R.drawable.ic_camera);
            mIv_camera.setTag(null);
            mIv_image.setVisibility(View.GONE);
            mTv_finish.setVisibility(View.GONE);
        }else {
            mIv_camera.setImageResource(R.drawable.ic_re_camera);
            mIv_camera.setTag(true);
            mIv_image.setVisibility(View.VISIBLE);
            mTv_finish.setVisibility(View.VISIBLE);
        }
    }

    public interface ActionCallback{
        /**
         * called on click finish
         */
        void onClickFinish();

        /**
         * called on click image.
         */
        void onClickImage();
    }

    public static abstract class PictureCallback extends CameraView.Callback{

        /**
         * called when you want save picture file internal. default return true.
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
            if(!mPictureCallback.shouldSavePicture()){
                setCameraEnabled(false);
                return;
            }
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    Context context = getContext();
                    if(context == null){
                        return;
                    }
                    File file = new File(mSaveDir, System.currentTimeMillis() + ".jpg");
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                        mPictureCallback.onTakePictureResult(null);
                        return;
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                    mPictureCallback.onTakePictureResult(file.getAbsolutePath());
                    setImageFile(file.getAbsolutePath());
                }
            });
        }
    }
}
