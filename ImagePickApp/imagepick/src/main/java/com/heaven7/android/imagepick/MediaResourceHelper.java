package com.heaven7.android.imagepick;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.android.util2.WeakContextOwner;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by heaven7 on 2017/12/22.
 */
public final class MediaResourceHelper{

   // private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final String[] IMAGE_MIMES = {"image/jpeg", "image/png", "image/jpg"};
    private static final String[] VIDEO_MIMES = {"video/mp4"};
  /*  private static final String[] VIDEO_MIMES = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb",
            "video/vob", "video/flv", "video/mkv", "video/mov", "video/mpg"};*/

    public static final int FLAG_IMAGE = 0x0001;
    public static final int FLAG_VIDEO = 0x0002;
    public static final int FLAG_IMAGE_AND_VIDEO = FLAG_IMAGE | FLAG_VIDEO;

    private final AtomicBoolean mDestroies = new AtomicBoolean();
    private final WeakContextOwner mOwner;
    private ExecutorService mService;

    public MediaResourceHelper(Context context) {
        this.mOwner = new WeakContextOwner(context);
    }
    /**
     * get the media resources. default filter the video of two seconds.
     *
     * @param flags    the flags, see {@linkplain #FLAG_IMAGE} and etc.
     * @param callback the callback
     */
    public void getMediaResource(final int flags, @NonNull final Callback callback) {
        getMediaResource(flags, 2000, Long.MAX_VALUE, callback);
    }
    /**
     * get the media resources.
     *
     * @param flags    the flags, see {@linkplain #FLAG_IMAGE} and etc.
     * @param minDuration    the min duration in  mill-seconds, for filter video
     * @param maxDuration    the max duration in  mill-seconds, for filter video
     * @param callback the callback
     */
    public void getMediaResource(final int flags, final long minDuration, final long maxDuration, @NonNull final Callback callback) {
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
                    photos = getAllLocalPhotos(getContext());
                } else {
                    photos = null;
                }
                if (mDestroies.get()) {
                    return;
                }
                final List<MediaResourceItem> videoes;
                if ((flags & FLAG_VIDEO) == FLAG_VIDEO) {
                    videoes = getAllLocalVideos(getContext(), minDuration, maxDuration);
                } else {
                    videoes = null;
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

    public void cancel(){
        if(mService != null){
            mService.shutdownNow();
            mService = null;
        }
    }

    private Context getContext() {
        return mOwner.getContext();
    }

    //note FileProvider
    private static Bitmap getVideoThumbnail(Context context, Uri uri) {
        return getVideoThumbnail(context, uri, 0);
    }
    public static Bitmap getVideoThumbnail(Context context, Uri uri, long us) {
        Bitmap b = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, uri);
            b = retriever.getFrameAtTime(us);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
            }
        }
        return b;
    }
    public static List<AudioBean> getAllAudioInfos(Context context){
        List<AudioBean> list = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media.DATA,      // path
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME,
        };
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri,
                projection, null, null, null);
        try {
            while (cursor.moveToNext()) {
                long album_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //
                AudioBean info = new AudioBean();
                info.setAlbum_id(album_id);
                info.setArtist(artist);
                info.setPath(path);
                info.setDisplayName(displayName);
                info.setTitle(title);
                info.setDuration(duration);
                list.add(info);
            }
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return list;
    }
    public static Bitmap getAlbumArt(Context context,long album_id) {
        if(context == null){
            return null;
        }
        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    private static List<MediaResourceItem> getAllLocalVideos(Context context, long minDuration, long maxDuration) {
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
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=?";
        List<MediaResourceItem> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, where, VIDEO_MIMES, MediaStore.Video.Media.DATE_ADDED + " DESC ");
        if (cursor == null) {
            return list;
        }
        try {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                Logger.d("MediaResourceHelper", "getAllLocalVideos", "path = " + path);

                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                // if (size < 600 * 1024 * 1024) {//<600M

                String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                String thumb = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.RESOLUTION));
                //for some mobile like xiaomi8 . width and height is zero
                if((width == 0 || height == 0) && !TextUtils.isEmpty(resolution)){
                    String[] strs = resolution.split("x");
                    width = Integer.parseInt(strs[0]);
                    height = Integer.parseInt(strs[1]);
                }

                //drop invalid resource
                if(size == 0 || width == 0 || height == 0){
                    continue;
                }
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                if(duration == 0 || duration < minDuration || duration > maxDuration){  //< 2s
                    continue;
                }

                //file not exist
                if(!new File(path).exists()){
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
    private static List<MediaResourceItem> getAllLocalPhotos(Context context) {
        List<MediaResourceItem> list = new ArrayList<>();
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
        };
        //all
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, IMAGE_MIMES,
                MediaStore.Images.Media.DATE_MODIFIED + " desc ");
        if (cursor == null) {
            return list;
        }
        try {
            while (cursor.moveToNext()) {
                MediaResourceItem materialBean = new MediaResourceItem();

                materialBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)); // 大小

                String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                //drop invalid resource
                if(size == 0 || width == 0 || height == 0){
                    continue;
                }

                materialBean.setMime(mime);
                materialBean.setWidth(width);
                materialBean.setHeight(height);

                String path = new String(data, 0, data.length - 1);
               // Logger.d("MediaResourceHelper", "getAllLocalPhotos", "path = " + path);
                File file = new File(path);
                //file not exist
                if(!file.exists()){
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

    public interface Callback {
        void onCallback(List<MediaResourceItem> photoes, List<MediaResourceItem> videoes);
    }
    public static class AudioBean {
        private String path;
        private long album_id;
        private String title;
        private String displayName;
        private long duration; //in msec
        private String artist;

        public String getArtist() {
            return artist;
        }
        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }

        public long getAlbum_id() {
            return album_id;
        }
        public void setAlbum_id(long album_id) {
            this.album_id = album_id;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public long getDuration() {
            return duration;
        }
        public void setDuration(long duration) {
            this.duration = duration;
        }

    }

    public static class MediaResourceItem extends BaseSelector implements Parcelable {
        private String title;
        private long time; //time of photo/video .the last modified
        private String filePath;
        private String thumbPath;
        private long fileSize;
        private String mime;
        private int width;
        private int height;

        private float ratio; //height / width in album
        private long duration;

        public float getImageRatio() {
            return ratio;
        }
        public void setImageRatio(float ratio) {
            this.ratio = ratio;
        }

        public boolean isImage() {
            return mime.startsWith("image");
        }
        public boolean isVideo() {
            return mime.startsWith("video");
        }

        public String getThumbPath() {
            return thumbPath;
        }
        public void setThumbPath(String thumbPath) {
            this.thumbPath = thumbPath;
        }

        public int getWidth() {
            return width;
        }
        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        /** in mills */
        public long getTime() {
            return time;
        }
        public void setTime(long time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public String toString() {
            return "MediaResourceItem{" +
                    ", title='" + title + '\'' +
                    ", time='" + time + '\'' +
                    ", duration='" + duration + '\'' +
                    ", filePath='" + filePath + '\'' +
                    ", fileSize=" + fileSize +
                    ", mime='" + mime + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }

        public MediaResourceItem() {
        }
        public MediaResourceItem(MediaResourceItem item) {
            title = item.title;
            time = item.time;
            filePath = item.filePath;
            fileSize = item.fileSize;
            mime = item.mime;
            width = item.width;
            height = item.height;
            thumbPath = item.thumbPath;
            ratio = item.ratio;
            duration = item.duration;
        }

        protected MediaResourceItem(Parcel in) {
            title = in.readString();
            time = in.readLong();
            filePath = in.readString();
            fileSize = in.readLong();
            mime = in.readString();
            width = in.readInt();
            height = in.readInt();
            thumbPath = in.readString();
            ratio = in.readFloat();
            duration = in.readLong();
        }

        public static final Creator<MediaResourceItem> CREATOR = new Creator<MediaResourceItem>() {
            @Override
            public MediaResourceItem createFromParcel(Parcel in) {
                return new MediaResourceItem(in);
            }

            @Override
            public MediaResourceItem[] newArray(int size) {
                return new MediaResourceItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeLong(time);
            dest.writeString(filePath);
            dest.writeLong(fileSize);
            dest.writeString(mime);
            dest.writeInt(width);
            dest.writeInt(height);
            dest.writeString(thumbPath);
            dest.writeFloat(ratio);
            dest.writeLong(duration);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MediaResourceItem item = (MediaResourceItem) o;

            return filePath != null ? filePath.equals(item.filePath) : item.filePath == null;
        }
        @Override
        public int hashCode() {
            return filePath.hashCode();
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
        /** in mill seconds */
        public long getDuration() {
            return duration;
        }

    }

}
