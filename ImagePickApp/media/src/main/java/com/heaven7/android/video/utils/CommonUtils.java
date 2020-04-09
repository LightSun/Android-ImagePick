package com.heaven7.android.video.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.RestrictTo;

import java.util.concurrent.TimeUnit;

/**
 * @author heaven7
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class CommonUtils {

    public static Bitmap getVideoThumbnail(Context context, Uri uri, long frameTime) {
        Bitmap b;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, uri);
            b = retriever.getFrameAtTime(frameTime);
        }finally {
            retriever.release();
        }
        return b;
    }
    //scale and clip in center.
    public static Bitmap scaleAndClip(Bitmap src, int requireW, int requireH){
        int w = src.getWidth();
        int h = src.getHeight();
        float scaleW = requireW * 1f / w;
        float scaleH = requireH * 1f / h;
        float scale = Math.max(scaleW, scaleH);

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap scaled = Bitmap.createBitmap(src, 0, 0, w, h, matrix, false);

        int sWidth = scaled.getWidth();
        int sHeight = scaled.getHeight();
        int left = 0, top = 0;
        if(sWidth > requireW){
            left = sWidth / 2 - requireW / 2;
        }
        if(sHeight > requireH){
            top = sHeight / 2 - requireH/ 2;
        }
        return Bitmap.createBitmap(scaled, left, top, requireW, requireH);
    }
    public static float frameToTime(int fps,long frames, TimeUnit unit) {
        switch (unit) {
            case SECONDS:
                return frames * 1f / fps;

            case MILLISECONDS:
                return frames * 1000f /fps;

            case MICROSECONDS:
                return frames * 1000000f / fps;

            default:
                throw new UnsupportedOperationException("+" + unit);
        }
    }
    public static long timeToFrame(int fps, float time, TimeUnit unit) {
        switch (unit) {
            case SECONDS:
                return (int) (time * fps);

            case MILLISECONDS:
                return (int) (time * fps / 1000);

            case MICROSECONDS:
                return (int) (time * fps / 1000000);

            default:
                throw new UnsupportedOperationException("+" + unit);
        }
    }
}
