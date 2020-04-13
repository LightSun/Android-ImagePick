package com.heaven7.android.video.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.SystemClock;

import com.heaven7.android.video.utils.CommonUtils;
import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static com.heaven7.android.video.ScaleManager.ScaleType_CENTER_CROP;
import static com.heaven7.android.video.ScaleManager.getScaleMatrix;
import static com.heaven7.android.video.ScaleManager.ofSize;

/**
 * the video frame loader used to load video frame.
 * this often can used for 'glide'.
 * @author heaven7
 */
public class VideoFrameLoader {

    private static final String TAG = "DataFetcherImpl";
    private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Context mContext;
    private FrameInfo mInfo;
    //option
    private int mFps = 30;
    private volatile boolean mCancelled;
    private DownloadExecutor mDownloadExecutor;

    public VideoFrameLoader(Context context, FrameInfo info) {
        this.mContext = context;
        this.mInfo = info;
    }

    public DownloadExecutor getDownloadExecutor() {
        return mDownloadExecutor;
    }

    public void setDownloadExecutor(DownloadExecutor downloadExecutor) {
        this.mDownloadExecutor = downloadExecutor;
    }

    public int getFps() {
        return mFps;
    }
    public void setFps(int mFps) {
        this.mFps = mFps;
    }

    public FrameInfo getFrameInfo() {
        return mInfo;
    }
    public void clearFrameInfo() {
        if (mInfo != null && mInfo.isInUse()) {
            mInfo.recycle();
            mInfo = null;
        }
    }
    public void cancel() {
        if(!mCancelled){
            mCancelled = true;
            if(mDownloadExecutor != null){
                mDownloadExecutor.cancel(mInfo.getUri().toString());
            }
        }
    }
    public InputStream loadData() throws Exception {
        if (mInfo == null) {
            return null;
        }
        Logger.d(TAG, "loadData", "" + mInfo);
        //define target file to store.
        File file = new File(mContext.getCacheDir() + "/glide_vf", "" + mInfo.getKey().hashCode());
        if (file.exists()) {
            if (file.isFile()) {
                return new FileInputStream(file);
            } else {
                file.delete();
            }
        } else {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        //check if need load
        Uri dst = mInfo.getUri();
        if (!mInfo.isLocal()) {
            if(mDownloadExecutor == null){
                throw new IllegalStateException("you must call setDownloadExecutor(...) for download net uri.");
            }
            dst = mDownloadExecutor.download(mContext, mInfo.getUri().toString());
            if (dst == null) {
                return null;
            }
        }
        //now start get thumb and scale
        long startTime = SystemClock.elapsedRealtime();
        Bitmap bitmap;
        if (mInfo.isFromVideo()) {
            bitmap = CommonUtils.getVideoThumbnail(mContext, dst, getTime());
        } else {
            InputStream in = null;
            try {
                in = mContext.getContentResolver().openInputStream(dst);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                in.close();

                //raw width,height of image
                int width = options.outWidth;
                int height = options.outHeight;
                //expect scale
                int scaleWidth = (getTargetWidth()) / width;
                int scaleHeight = (getTargetHeight()) / height;
                if (scaleWidth > 0 && scaleHeight > 0) {
                    options.inSampleSize = (scaleWidth > scaleHeight ? scaleHeight : scaleWidth);
                }
                options.inJustDecodeBounds = false;
                in = mContext.getContentResolver().openInputStream(dst);
                bitmap = BitmapFactory.decodeStream(in, null, options);
            } catch (Exception e) {
                throw e;
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
        if (mCancelled) {
            return null;
        }
        //scale
        if (mInfo.getWidth() > 0 && mInfo.getHeight() > 0
                && (bitmap.getWidth() != mInfo.getWidth() || bitmap.getHeight() != mInfo.getHeight()) ) {
            final Bitmap result;
            if (mInfo.getScaleType() == FrameInfo.SCALE_TYPE_RAW) {
                result = Bitmap.createScaledBitmap(bitmap, mInfo.getWidth(), mInfo.getHeight(), false);
            } else if (mInfo.getScaleType() == FrameInfo.SCALE_TYPE_SCALE_CLIP) {
                result = CommonUtils.scaleAndClip(bitmap, mInfo.getWidth(), mInfo.getHeight());
            } else {
                Matrix matrix = getScaleMatrix(ofSize(mInfo.getWidth(), mInfo.getHeight()),
                        ofSize(bitmap.getWidth(), bitmap.getHeight()), ScaleType_CENTER_CROP);
                result = Bitmap.createBitmap(mInfo.getWidth(), mInfo.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                canvas.drawBitmap(bitmap, matrix, sPaint);
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = result;
        }
        if (mCancelled) {
            return null;
        }
        //bitmap.setHasAlpha(true);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } finally {
            IOUtils.closeQuietly(out);
        }
        Logger.d(TAG, "loadData", "load cost time = " + (SystemClock.elapsedRealtime() - startTime));
        return new FileInputStream(file);
    }
    private long getTime() {
        return mInfo.getTimeMsec() >= 0 ? mInfo.getTimeMsec() * 1000 :
                (long) (CommonUtils.frameToTime(mFps, mInfo.getFrameTime(), TimeUnit.MICROSECONDS));
    }
    private int getTargetWidth() {
        if (mInfo.getWidth() > 0) {
            return mInfo.getWidth();
        }
        return mInfo.getMaxWidth();
    }
    private int getTargetHeight() {
        if (mInfo.getHeight() > 0) {
            return mInfo.getHeight();
        }
        return mInfo.getMaxHeight();
    }

}
