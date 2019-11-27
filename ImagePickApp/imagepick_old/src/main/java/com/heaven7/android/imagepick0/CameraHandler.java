package com.heaven7.android.imagepick0;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CameraHandler extends Handler {

    public static final int MSG_PREVIEW     = 1;
    public static final int MSG_AUTO_FOCUS  = 2;
    public static final int MSG_REQUEST_BITMAP = 3;

    public static final int STATE_NONE    = 2;
    public static final int STATE_PREVIEW = 3;

    private int mState = STATE_NONE;
    private final ImageParam mParam = new ImageParam();
    private ExecutorService mService;

    public CameraHandler() {
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case MSG_PREVIEW:
                System.out.println("MSG_PREVIEW");
                mParam.set((byte[]) msg.obj, msg.arg1, msg.arg2);
                break;

            case MSG_AUTO_FOCUS:
                System.out.println("MSG_AUTO_FOCUS");
                CameraManager.get().requestAutoFocus(this, MSG_AUTO_FOCUS);
                CameraManager.get().requestPreviewFrame(this, MSG_PREVIEW);
                break;

            case MSG_REQUEST_BITMAP:
                System.out.println("MSG_REQUEST_BITMAP");
                CameraFragment.IBitmapDelegate delegate = (CameraFragment.IBitmapDelegate) msg.obj;
                if(mParam.data == null){
                    delegate.onBitmap(null);
                }else {
                    mService.submit(new Runner(new ImageParam(mParam), delegate, this));
                }
                break;
        }
    }

    public void quitSynchronously() {
        if(mService != null){
            mService.shutdownNow();
            mService = null;
        }
        CameraManager.get().stopPreview();
        removeMessages(MSG_AUTO_FOCUS);
        removeMessages(MSG_PREVIEW);
        mState = STATE_NONE;
    }

    private void restartPreviewAndDecode() {
        if (mState == STATE_NONE) {
            mState = STATE_PREVIEW;
            System.out.println("restartPreviewAndDecode: >> STATE_PREVIEW");
            CameraManager.get().requestPreviewFrame(this, MSG_PREVIEW);
            CameraManager.get().requestAutoFocus(this, MSG_AUTO_FOCUS);
        }
        if(mService == null){
            mService = Executors.newSingleThreadExecutor();
        }
    }

    private static class Runner implements Runnable{
        final ImageParam source;
        final CameraFragment.IBitmapDelegate delegate;
        final Handler main;

        public Runner(ImageParam source, CameraFragment.IBitmapDelegate delegate, Handler main) {
            this.source = source;
            this.delegate = delegate;
            this.main = main;
        }
        @Override
        public void run() {
          /*  PlanarYUVLuminanceSource decode = PlanarYUVLuminanceSource.from(source.data, source.width, source.height);
            final Bitmap bitmap = decode.renderCroppedGreyscaleBitmap();*/
           // final Bitmap bitmap = yuv420spToBitmap(source.data, source.width, source.height);
            //final Bitmap bitmap = yuv420spToBitmap(source.data, source.width, source.height);
            final Bitmap bitmap = getBitmap(source.data, CameraManager.get().getCamera(), false);
            //final Bitmap bitmap = PlanarYUVLuminanceSource.from(source.data, source.width, source.height).renderCroppedGreyscaleBitmap();
            System.out.println("Runner  >> bitmap");
            main.post(new Runnable() {
                @Override
                public void run() {
                    delegate.onBitmap(bitmap);
                }
            });
        }
    }
    public static Bitmap getBitmap(byte[] data, Camera camera, boolean mIsFrontalCamera) {
        int width = camera.getParameters().getPreviewSize().width;
        int height = camera.getParameters().getPreviewSize().height;
        YuvImage yuvImage = new YuvImage(data, camera.getParameters()
                .getPreviewFormat(), width, height, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 80,
                byteArrayOutputStream);
        byte[] jpegData = byteArrayOutputStream.toByteArray();
        // 获取照相后的bitmap
        Bitmap tmpBitmap = BitmapFactory.decodeByteArray(jpegData, 0,
                jpegData.length);
        Matrix matrix = new Matrix();
        matrix.reset();
        if (mIsFrontalCamera) {
            matrix.setRotate(-90);
        } else {
            matrix.setRotate(90);
        }
        tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(),
                tmpBitmap.getHeight(), matrix, true);
        tmpBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888, true);

        int hight = tmpBitmap.getHeight() > tmpBitmap.getWidth() ? tmpBitmap
                .getHeight() : tmpBitmap.getWidth();

        float scale = hight / 800.0f;

        if (scale > 1) {
            tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap,
                    (int) (tmpBitmap.getWidth() / scale),
                    (int) (tmpBitmap.getHeight() / scale), false);
        }
        return tmpBitmap;
    }

    //没有旋转
    public static Bitmap yuv420spToBitmap(byte[] data, int width, int height) {
        Bitmap bitmap = null;
        YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
        if (image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
