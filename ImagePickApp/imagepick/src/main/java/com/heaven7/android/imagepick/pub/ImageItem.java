package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageItem implements Parcelable, IImageItem {

    private boolean selected;
    private String filePath;
    private String url;

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
    }

    protected ImageItem(Parcel in) {
        this.selected = in.readByte() != 0;
        this.filePath = in.readString();
        this.url = in.readString();
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
