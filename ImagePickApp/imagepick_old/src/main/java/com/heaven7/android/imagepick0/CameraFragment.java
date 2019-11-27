package com.heaven7.android.imagepick0;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

/**
 * camera fragment
 * @author heaven7
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    private CustomSurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private boolean hasSurface;
    private Camera camera;

    private Callback mCallback;
    private CameraHandler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.init(getActivity().getApplication());
        hasSurface = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSurfaceView.setOnTouchEventListener(new CustomSurfaceView.OnTouchEventListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                CameraManager cm = CameraManager.get();
                if(cm != null && cm.getCamera()  != null){
                    FocusUtils.handleFocus(event, cm.getCamera(), mSurfaceView.getWidth(), mSurfaceView.getHeight());
                }
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.frag_camera, container, false);
        mSurfaceView = view.findViewById(R.id.preview_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera(mSurfaceHolder);
        } else {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            Rect surfaceFrame = surfaceHolder.getSurfaceFrame();
            CameraManager.FRAME_WIDTH = surfaceFrame.width();
            CameraManager.FRAME_HEIGHT = surfaceFrame.height();
            CameraManager.get().openDriver(surfaceHolder);
            camera = CameraManager.get().getCamera();
        } catch (Exception e) {
            if (mCallback != null) {
                mCallback.onInitFailed(e);
            }
            return;
        }
        if(mCallback != null){
            mCallback.onInitSuccess();
        }
        if(mHandler == null){
            mHandler = new CameraHandler();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraManager.FRAME_WIDTH = width;
        CameraManager.FRAME_HEIGHT = height;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        if (camera != null) {
            if (CameraManager.get().isPreviewing()) {
                if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                    camera.setPreviewCallback(null);
                }
                camera.stopPreview();
                CameraManager.get().getPreviewCallback().setHandler(null, 0);
                CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
                CameraManager.get().setPreviewing(false);
            }
        }
    }

    public void requestBitmap(IBitmapDelegate delegate){
        mHandler.obtainMessage(CameraHandler.MSG_REQUEST_BITMAP, delegate).sendToTarget();
    }

    public void setCallback(Callback callback){
        this.mCallback = callback;
    }

    public interface Callback{
        void onInitFailed(Exception e);
        void onInitSuccess();
    }
    public interface IBitmapDelegate{
        void onBitmap(Bitmap bitmap);
    }

}
