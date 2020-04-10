package com.heaven7.android.imagepick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.cameraview.CameraView;
import com.heaven7.android.imagepick.pub.CameraParameter;
import com.heaven7.android.imagepick.pub.ImageItem;
import com.heaven7.android.imagepick.pub.ImageOptions;
import com.heaven7.android.imagepick.pub.ImageParameter;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.core.util.DimenUtil;
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

    private ImageView mIv_image;
    private ImageView mIv_camera;
    private ImageView mIv_flash;

    private TextView mTv_finish;

    private PictureCallback mPictureCallback;
    private ActionCallback mActionCallback;
    private final ThreadHelper mThreadHelper = new ThreadHelper();

    private File mSaveDir;
    private final AtomicBoolean mProcessing = new AtomicBoolean(false);
    private ImageParser mImgParser;
    private CameraParameter mCameraParam;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_pick_frag_camera_main, container, false);
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
        handleArguments();

        mCameraView.setFlash(CameraView.FLASH_AUTO);
        mCameraView.addCallback(mPictureCallback);
        mCameraView.addCallback(new InternalCallback());
        mIv_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProcessing.get()){
                    return;
                }
                if(mCameraView.getFlash() == CameraView.FLASH_AUTO){
                    mIv_flash.setImageResource(R.drawable.lib_pick_ic_flash_off);
                    mCameraView.setFlash(CameraView.FLASH_OFF);
                }else {
                    mIv_flash.setImageResource(R.drawable.lib_pick_ic_flash_on);
                    mCameraView.setFlash(CameraView.FLASH_ON);
                }
            }
        });
        mIv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProcessing.get()){
                    return;
                }
                if(v.getTag() != null){
                    //re-camera
                    setCameraEnabled(true);
                }else {
                    //normal
                    if(mCameraParam != null && mCameraParam.getMaxCount() > 0 &&
                            ImagePickDelegateImpl.getDefault().getImages().size() >= mCameraParam.getMaxCount()){
                        setCameraEnabled(false);
                        setReachMax(true);
                        Toaster.show(v.getContext(), getString(R.string.lib_pick_camera_reach_max, mCameraParam.getMaxCount()));
                        return;
                    }
                    mCameraView.takePicture();
                }
            }
        });
        mIv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProcessing.get()){
                    return;
                }
                if(mActionCallback != null){
                    mActionCallback.onClickImage();
                }
            }
        });
        mTv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProcessing.get()){
                    return;
                }
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
        List<String> images = ImagePickDelegateImpl.getDefault().getImages();
        //Logger.d("CameraFragment", "onResume", "" + images);
        if(images.isEmpty()){
            mIv_image.setVisibility(View.GONE);
        }
        //check if max reach and remove some.
        if(mCameraParam != null && mCameraParam.getMaxCount() > 0 &&
                images.size() < mCameraParam.getMaxCount() ){
            setReachMax(false);
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

    public void setActionCallback(ActionCallback mActionCallback) {
        this.mActionCallback = mActionCallback;
    }

    public void setPictureCallback(PictureCallback pictureCallback) {
        this.mPictureCallback = pictureCallback;
    }

    private void setReachMax(boolean reachMax) {
        if(reachMax){
            mIv_camera.setEnabled(false);
        }else {
            mIv_camera.setEnabled(true);
        }
    }
    private void handleArguments() {
        Bundle arguments = getArguments();
        if(arguments != null){
            CameraParameter cp = arguments.getParcelable(PickConstants.KEY_PARAMS);
            mCameraParam = cp;
            if(cp == null){
                mImgParser = new ImageParser(4000, 4000,
                        Bitmap.Config.RGB_565, true);
                mCameraView.setAutoFocus(true);
            }else {
                ImageParameter ip = cp.getImageParameter();
                mImgParser = Utils.createImageParser(ip, true);
                mCameraView.setAutoFocus(cp.isAutoFocus());
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
                 ViewGroup.LayoutParams lp = mIv_image.getLayoutParams();
                 int round = DimenUtil.dip2px(context, 8);
                 setCameraEnabled(false);

                 ImageOptions options = new ImageOptions.Builder()
                         .setRound(round)
                         .setBorder(1)
                         .setBorderColor(Color.TRANSPARENT)
                         .setCacheFlags(ImageOptions.CACHE_FLAG_DATA | ImageOptions.CACHE_FLAG_RESOURCE)
                         .setTargetWidth(lp.width)
                         .setTargetHeight(lp.height)
                         .build();
                 ImagePickDelegateImpl.getDefault().getImageLoadDelegate().loadImage(CameraFragment.this,mIv_image, ImageItem.ofImage(file), options);
             }
         });
    }
    private void setCameraEnabled(boolean enabled){
        if(enabled){
            mIv_camera.setImageResource(R.drawable.lib_pick_ic_camera);
            mIv_camera.setTag(null);
            mIv_image.setVisibility(View.GONE);
            mTv_finish.setVisibility(View.GONE);
        }else {
            mIv_camera.setImageResource(R.drawable.lib_pick_ic_re_camera);
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
                setCameraEnabled(false);
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
                            setImageFile(file.getAbsolutePath());
                        }
                    });
                }
            });
        }
    }
}
