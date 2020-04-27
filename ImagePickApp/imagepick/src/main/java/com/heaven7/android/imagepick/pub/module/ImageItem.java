package com.heaven7.android.imagepick.pub.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.heaven7.java.base.util.FileUtils;

/**
 * simple image item
 */
public class ImageItem implements Parcelable, IImageItem {

    private boolean selected;
    private String filePath;
    private String url;
    private boolean video;
    private Parcelable extra;
    private String mine;

    public static ImageItem ofImage(String filePath) {
        String ext = FileUtils.getFileExtension(filePath).toLowerCase();
        ImageItem imageItem = new ImageItem();
        imageItem.setFilePath(filePath);
        imageItem.setVideo(false);
        imageItem.setMine("image/"+ ext);
        return imageItem;
    }

    public static ImageItem of(String filePath, String mine, boolean image) {
        return new ImageItem.Builder()
                .setFilePath(filePath)
                .setVideo(!image)
                .setMine(mine)
                .build();
    }

    public void setMine(String mine) {
        this.mine = mine;
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

    protected ImageItem(ImageItem.Builder builder) {
        this.selected = builder.selected;
        this.filePath = builder.filePath;
        this.url = builder.url;
        this.video = builder.video;
        this.extra = builder.extra;
        this.mine = builder.mine;
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

    @Override
    public boolean isGif() {
        return "image/gif".equals(mine);
    }

    public String getMine() {
        return this.mine;
    }

    public static class Builder {
        private boolean selected;
        private String filePath;
        private String url;
        private boolean video;
        private Parcelable extra;
        private String mine;

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

        public Builder setMine(String mine) {
            this.mine = mine;
            return this;
        }

        public ImageItem build() {
            return new ImageItem(this);
        }
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
        dest.writeString(this.mine);
    }

    protected ImageItem(Parcel in) {
        this.selected = in.readByte() != 0;
        this.filePath = in.readString();
        this.url = in.readString();
        this.video = in.readByte() != 0;
        this.extra = in.readParcelable(Parcelable.class.getClassLoader());
        this.mine = in.readString();
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
}
