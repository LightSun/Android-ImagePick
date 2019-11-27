package com.heaven7.android.imagepick0;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/*public*/ class SimplePreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = "SimplePreviewCallback";
    private final CameraConfigurationManager configManager;
    private Handler previewHandler;
    private int previewMessage;

    SimplePreviewCallback(CameraConfigurationManager configManager) {
        this.configManager = configManager;
    }

    public void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = configManager.getCameraResolution();
        if (previewHandler != null) {
            Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.x,
                    cameraResolution.y, data);
            message.sendToTarget();
        } else {
            Log.d(TAG, "Got preview callback, but no handler for it");
        }
    }
}
