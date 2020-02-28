package com.heaven7.android.imagepick;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.heaven7.android.imagepick.pub.ImageSelectParameter;
import com.heaven7.android.imagepick.pub.MediaOption;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.util2.WeakContextOwner;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by heaven7 on 2017/12/22.
 */
public final class MediaResourceHelper {

    public static final int FLAG_IMAGE = 0x0001;
    public static final int FLAG_VIDEO = 0x0002;
    public static final int FLAG_IMAGE_AND_VIDEO = FLAG_IMAGE | FLAG_VIDEO;

    private final WeakContextOwner mOwner;
    private final ImageSelectParameter mParam;
    private final AtomicBoolean mDestroies = new AtomicBoolean();
    private ExecutorService mService;

    public MediaResourceHelper(Context context, ImageSelectParameter mParam) {
        this.mOwner = new WeakContextOwner(context);
        this.mParam = mParam;
    }

    /**
     * get the media resources.
     *
     * @param flags    the flags, see {@linkplain #FLAG_IMAGE} and etc.
     * @param callback the callback
     */
    public void getMediaResource(final int flags, final Callback callback) {
        if (flags <= 0) {
            throw new IllegalArgumentException();
        }
        if (mService == null) {
            mService = Executors.newCachedThreadPool();
        }
        mDestroies.set(false);
        mService.submit(new Runnable() {
            @Override
            public void run() {
                final List<MediaResourceItem> photos;
                if ((flags & FLAG_IMAGE) == FLAG_IMAGE) {
                    photos = getAllLocalPhotos(getContext(), mParam.getMediaOption());
                } else {
                    photos = new ArrayList<>();
                }
                if (mDestroies.get()) {
                    return;
                }
                final List<MediaResourceItem> videoes;
                if ((flags & FLAG_VIDEO) == FLAG_VIDEO) {
                    videoes = getAllLocalVideos(getContext(), mParam.getMediaOption());
                } else {
                    videoes = new ArrayList<>();
                }
                if (mDestroies.get()) {
                    return;
                }
                MainWorker.post(new Runnable() {
                    @Override
                    public void run() {
                        cancel();
                        callback.onCallback(photos, videoes);
                    }
                });
            }
        });
    }

    public void cancel() {
        if (mService != null) {
            mService.shutdownNow();
            mService = null;
        }
    }

    private Context getContext() {
        return mOwner.getContext();
    }

    private static List<MediaResourceItem> getAllLocalVideos(Context context, MediaOption mediaOption) {
        List<String> videoMimes = mediaOption.getVideoMimes();
        String[] projection = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Thumbnails.DATA,
        };
        String where = createMimeWhere(videoMimes);
        List<MediaResourceItem> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, where, videoMimes.toArray(new String[videoMimes.size()]), MediaStore.Video.Media.DATE_ADDED + " DESC ");
        if (cursor == null) {
            return list;
        }
        try {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                Logger.d("MediaResourceHelper", "getAllLocalVideos", "path = " + path);

                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                // if (size < 600 * 1024 * 1024) {//<600M
                if(size == 0 || size > mediaOption.getMaxVideoSize()){
                    continue;
                }

                String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                String thumb = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.RESOLUTION));
                //for some mobile like xiaomi8 . width and height is zero
                if ((width == 0 || height == 0) && !TextUtils.isEmpty(resolution)) {
                    String[] strs = resolution.split("x");
                    width = Integer.parseInt(strs[0]);
                    height = Integer.parseInt(strs[1]);
                }

                //drop invalid resource
                if (width == 0 || height == 0) {
                    continue;
                }
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                if (duration == 0 || duration < mediaOption.getVideoMinDuration() || duration > mediaOption.getVideoMaxDuration()) {
                    continue;
                }

                //file not exist
                if (!new File(path).exists()) {
                    continue;
                }
                MediaResourceItem materialBean = new MediaResourceItem();
                materialBean.setMime(mime);
                materialBean.setWidth(width);
                materialBean.setHeight(height);
                materialBean.setThumbPath(thumb);

                materialBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                materialBean.setFilePath(path);

                //formatDuration.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                //String t = TimeUtils.formatDuration(duration);
                final File file = new File(path);

                materialBean.setDuration(duration);
                materialBean.setTime(file.lastModified());
                materialBean.setFileSize(size);
                list.add(materialBean);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * get all images
     */
    private static List<MediaResourceItem> getAllLocalPhotos(Context context, MediaOption mediaOption) {
        List<MediaResourceItem> list = new ArrayList<>();
        List<String> imageMimes = mediaOption.getImageMimes();
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
        };
        //all
        String where = createMimeWhere(imageMimes);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, imageMimes.toArray(new String[imageMimes.size()]),
                MediaStore.Images.Media.DATE_MODIFIED + " desc ");
        if (cursor == null) {
            return list;
        }
        try {
            while (cursor.moveToNext()) {
                MediaResourceItem materialBean = new MediaResourceItem();

                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                if(size == 0 || size > mediaOption.getMaxImageSize()){
                    continue;
                }
                materialBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                //drop invalid resource
                if (width == 0 || height == 0) {
                    continue;
                }

                materialBean.setMime(mime);
                materialBean.setWidth(width);
                materialBean.setHeight(height);

                String path = new String(data, 0, data.length - 1);
                // Logger.d("MediaResourceHelper", "getAllLocalPhotos", "path = " + path);
                File file = new File(path);
                //file not exist
                if (!file.exists()) {
                    continue;
                }

                //if (size < 5 * 1024 * 1024) {//<5M
                long time = file.lastModified();

                //String t = FORMATER.format(time);
                materialBean.setTime(time);
                materialBean.setFilePath(path);
                materialBean.setFileSize(size);
                list.add(materialBean);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    private static String createMimeWhere(List<String> mimes) {
       /*  String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";*/
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = mimes.size() ; i < size ; i ++){
            sb.append(MediaStore.MediaColumns.MIME_TYPE)
                    .append("=?");
            if(i != size - 1){
                sb.append(" or ");
            }
        }
        return sb.toString();
    }

    public interface Callback {
        /**
         * called on scan image and video done.
         *
         * @param photoes the image items
         * @param videoes the video items
         */
        void onCallback(List<MediaResourceItem> photoes, List<MediaResourceItem> videoes);
    }
}
