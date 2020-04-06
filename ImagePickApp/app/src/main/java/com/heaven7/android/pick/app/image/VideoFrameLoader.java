package com.heaven7.android.pick.app.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.heaven7.android.pick.app.utils.CommonUtils;
import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static lib.vida.video.ScaleManager.ScaleType_CENTER_CROP;
import static lib.vida.video.ScaleManager.getScaleMatrix;
import static lib.vida.video.ScaleManager.ofSize;

/**
 * Created by heaven7 on 2018/1/24 0024.
 */

/*public*/ class VideoFrameLoader implements ModelLoader<FrameInfo, InputStream> {

    private final Context context;

    public VideoFrameLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull FrameInfo model, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(model.getKey()), new DataFetcherImpl(context, model));
    }

    @Override
    public boolean handles(@NonNull FrameInfo frameInfo) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<FrameInfo, InputStream> {

        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ModelLoader<FrameInfo, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new VideoFrameLoader(context);
        }

        @Override
        public void teardown() {

        }
    }

    public static class DataFetcherImpl implements DataFetcher<InputStream> {

        private static final String TAG = "DataFetcherImpl";
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Context context;
        private FrameInfo info;

        private volatile boolean isCancelled;

        public DataFetcherImpl(Context mContext, FrameInfo info) {
            this.context = mContext;
            this.info = info;
        }

        private long getTime() {
            return info.getTimeMsec() >= 0 ? info.getTimeMsec() * 1000 : (long) (CommonUtils.frameToTime(
                    info.getFrameTime(), TimeUnit.MICROSECONDS));
        }
        private int getTargetWidth(){
            if(info.getWidth() > 0){
                return info.getWidth();
            }
            return info.getMaxWidth();
        }
        private int getTargetHeight(){
            if(info.getHeight() > 0){
                return info.getHeight();
            }
            return info.getMaxHeight();
        }

        public InputStream loadData(Priority priority) throws Exception {
            if (info == null) {
                return null;
            }
            Logger.d(TAG, "loadData", "" + info);
            File file = new File(context.getCacheDir() + "/glide_vf", "" + info.getKey().hashCode());
            if(file.exists()){
                if(file.isFile()){
                    return new FileInputStream(file);
                }else {
                    file.delete();
                }
            }else {
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
            }

            long startTime = SystemClock.elapsedRealtime();
            Bitmap bitmap;
            if (info.isFromVideo()) {
                bitmap = CommonUtils.getVideoThumbnail(context, info.getUri(), getTime());
            } else {
                InputStream in = null;
                try {
                    in = context.getContentResolver().openInputStream(info.getUri());
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
                    if(scaleWidth > 0 && scaleHeight > 0){
                        options.inSampleSize = (scaleWidth > scaleHeight ? scaleHeight : scaleWidth);
                    }
                    options.inJustDecodeBounds = false;
                    in = context.getContentResolver().openInputStream(info.getUri());
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    in.close();
                } catch (Exception e) {
                    throw e;
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
            if (isCancelled) {
                return null;
            }
            //scale
            if (info.getWidth() > 0 && info.getHeight() > 0  && (bitmap.getWidth() != info.getWidth()
                    || bitmap.getHeight() != info.getHeight())
            ) {
                final Bitmap result;
                if (info.getScaleType() == FrameInfo.SCALE_TYPE_RAW) {
                    result = Bitmap.createScaledBitmap(bitmap, info.getWidth(), info.getHeight(), false);
                } else if (info.getScaleType() == FrameInfo.SCALE_TYPE_SCALE_CLIP) {
                    result = CommonUtils.scaleAndClip(bitmap, info.getWidth(), info.getHeight());
                } else {
                    Matrix matrix = getScaleMatrix(ofSize(info.getWidth(), info.getHeight()),
                            ofSize(bitmap.getWidth(), bitmap.getHeight()), ScaleType_CENTER_CROP);
                    result = Bitmap.createBitmap(info.getWidth(), info.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(result);
                    canvas.drawBitmap(bitmap, matrix, mPaint);

                    //Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false); //use this can't fix bug
              /*  Logger.d("DataFetcherImpl", "loadData", "[ Res ] bitmap(w, h) = (" + bitmap.getWidth() + " ,"+  bitmap.getHeight()+")"
                       + "\n" + "[ Result ] bitmap(w, h) = (" + result.getWidth() + " ,"+  result.getHeight()+")"
                       + "\n" + "[ Request ] bitmap(w, h) = (" + info.getWidth() + " ,"+  info.getHeight()+")");*/
                }
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                bitmap = result;
            }
            if (isCancelled) {
                return null;
            }
            bitmap.setHasAlpha(true);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }finally {
                IOUtils.closeQuietly(out);
            }
            Logger.d(TAG, "loadData", "load cost time = " + (SystemClock.elapsedRealtime() - startTime));
            return new FileInputStream(file);


            /*
             * // lets create a new empty bitmap
             Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
             // create a canvas where we can draw on
             Canvas canvas = new Canvas(newBitmap);
             // create a paint instance with alpha
             Paint alphaPaint = new Paint();
             alphaPaint.setAlpha(42);
             // now lets draw using alphaPaint instance
             canvas.drawBitmap(originalBitmap, 0, 0, alphaPaint);

             // now lets store the bitmap to a file - the canvas has drawn on the newBitmap, so we can just store that one
             // please add stream handling with try/catch blocks
             FileOutputStream fos = new FileOutputStream(new File("/awesome/path/to/bitmap.png"));
             newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
             */
        }

        @Override
        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
            try {
                InputStream in = loadData(priority);
                callback.onDataReady(in);
            } catch (Exception e) {
                Logger.d(TAG, "load image failed." + info.getKey());
                callback.onLoadFailed(e);
            }
        }

        @Override
        public void cleanup() {
           /* if (info != null && info.isInUse()) {
                info.recycle();
                info = null;
            }*/
        }

        @Override
        public void cancel() {
            isCancelled = true;
        }

        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.DATA_DISK_CACHE;
        }
    }
}
