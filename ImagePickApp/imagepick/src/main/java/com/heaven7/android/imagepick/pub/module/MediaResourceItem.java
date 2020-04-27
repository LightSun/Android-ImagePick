package com.heaven7.android.imagepick.pub.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.heaven7.adapter.BaseSelector;

/**
 * media resource item.
 * @author heaven7
 */
public class MediaResourceItem extends BaseSelector implements Parcelable, IImageItem {

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
    private Parcelable extra;

    @Override
    public Parcelable getExtra() {
        return extra;
    }
    @Override
    public void setExtra(Parcelable extra) {
        this.extra = extra;
    }

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

    /**
     * in mills
     */
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String getUrl() {
        return null;
    }
    @Override
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
        setSelected(item.isSelected());
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
        setSelected(in.readByte() == 1);
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
        extra = in.readParcelable(MediaResourceItem.class.getClassLoader());
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
        dest.writeByte((byte) (isSelected() ? 1 : 0));
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
        dest.writeParcelable(extra, flags);
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

    /**
     * in mill seconds
     */
    public long getDuration() {
        return duration;
    }

    public boolean isGif() {
        return getMime().contains("image/gif");
    }
}