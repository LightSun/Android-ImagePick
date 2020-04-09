package com.heaven7.android.pick.app.image;

import android.content.Context;

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
import com.heaven7.android.video.load.FrameInfo;
import com.heaven7.android.video.load.VideoFrameLoader;
import com.heaven7.core.util.Logger;

import java.io.InputStream;

/**
 * Created by heaven7 on 2018/1/24 0024.
 */

/*public*/ class GlideVideoFrameLoader implements ModelLoader<FrameInfo, InputStream> {

    private final Context context;

    public GlideVideoFrameLoader(Context context) {
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
            return new GlideVideoFrameLoader(context);
        }

        @Override
        public void teardown() {

        }
    }

    public static class DataFetcherImpl implements DataFetcher<InputStream> {

        private static final String TAG = "DataFetcherImpl";
        private final VideoFrameLoader mLoader;

        public DataFetcherImpl(Context mContext, FrameInfo info) {
            mLoader = new VideoFrameLoader(mContext, info);
        }

        @Override
        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
            try {
                InputStream in = mLoader.loadData();
                callback.onDataReady(in);
            } catch (Exception e) {
                Logger.d(TAG, "load image failed." + mLoader.getFrameInfo().getKey());
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
            mLoader.cancel();
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
