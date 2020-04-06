package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * simple image item
 */
public class ImageItem implements Parcelable, IImageItem {

    private boolean selected;
    private String filePath;
    private String url;
    private boolean video;
    private Parcelable extra;

    public static ImageItem of(String filePath, boolean image) {
        ImageItem imageItem = new ImageItem();
        imageItem.setFilePath(filePath);
        imageItem.setVideo(!image);
        return imageItem;
    }

    public void setExtra(Parcelable extra) {
        this.extra = extra;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public ImageItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.filePath);
        dest.writeString(this.url);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.extra, flags);
    }

    protected ImageItem(Parcel in) {
        this.selected = in.readByte() != 0;
        this.filePath = in.readString();
        this.url = in.readString();
        this.video = in.readByte() != 0;
        this.extra = in.readParcelable(ImageItem.class.getClassLoader());
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    protected ImageItem(ImageItem.Builder builder) {
        this.selected = builder.selected;
        this.filePath = builder.filePath;
        this.url = builder.url;
        this.video = builder.video;
        this.extra = builder.extra;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isVideo() {
        return this.video;
    }

    public Parcelable getExtra() {
        return this.extra;
    }

    public static class Builder {
        private boolean selected;
        private String filePath;
        private String url;
        private boolean video;
        private Parcelable extra;

        public Builder setSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setVideo(boolean video) {
            this.video = video;
            return this;
        }

        public Builder setExtra(Parcelable extra) {
            this.extra = extra;
            return this;
        }

        public ImageItem build() {
            return new ImageItem(this);
        }
    }
}
