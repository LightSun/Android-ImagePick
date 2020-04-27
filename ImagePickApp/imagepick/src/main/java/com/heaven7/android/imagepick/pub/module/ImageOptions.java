package com.heaven7.android.imagepick.pub.module;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageOptions implements Parcelable {

    public static final int CACHE_FLAG_RESOURCE = 1;
    public static final int CACHE_FLAG_DATA = 2;

    private float round;
    private float border;
    private int borderColor;
    private int cacheFlags;
    //load image as target
    private int targetWidth;
    private int targetHeight;

    protected ImageOptions(ImageOptions.Builder builder) {
        this.round = builder.round;
        this.border = builder.border;
        this.borderColor = builder.borderColor;
        this.cacheFlags = builder.cacheFlags;
        this.targetWidth = builder.targetWidth;
        this.targetHeight = builder.targetHeight;
    }

    public ImageOptions(){}

    public void setRound(float round) {
        this.round = round;
    }
    public void setBorder(float border) {
        this.border = border;
    }
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
    public void setCacheFlags(int cacheFlags) {
        this.cacheFlags = cacheFlags;
    }

    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public float getRound() {
        return this.round;
    }

    public float getBorder() {
        return this.border;
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public int getCacheFlags() {
        return this.cacheFlags;
    }

    public int getTargetWidth() {
        return this.targetWidth;
    }

    public int getTargetHeight() {
        return this.targetHeight;
    }

    public static class Builder {
        private float round;
        private float border;
        private int borderColor;
        private int cacheFlags;
        //load image as target
        private int targetWidth;
        private int targetHeight;

        public Builder setRound(float round) {
            this.round = round;
            return this;
        }

        public Builder setBorder(float border) {
            this.border = border;
            return this;
        }

        public Builder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder setCacheFlags(int cacheFlags) {
            this.cacheFlags = cacheFlags;
            return this;
        }

        public Builder setTargetWidth(int targetWidth) {
            this.targetWidth = targetWidth;
            return this;
        }

        public Builder setTargetHeight(int targetHeight) {
            this.targetHeight = targetHeight;
            return this;
        }

        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.round);
        dest.writeFloat(this.border);
        dest.writeInt(this.borderColor);
        dest.writeInt(this.cacheFlags);
        dest.writeInt(this.targetWidth);
        dest.writeInt(this.targetHeight);
    }

    protected ImageOptions(Parcel in) {
        this.round = in.readFloat();
        this.border = in.readFloat();
        this.borderColor = in.readInt();
        this.cacheFlags = in.readInt();
        this.targetWidth = in.readInt();
        this.targetHeight = in.readInt();
    }

    public static final Creator<ImageOptions> CREATOR = new Creator<ImageOptions>() {
        @Override
        public ImageOptions createFromParcel(Parcel source) {
            return new ImageOptions(source);
        }

        @Override
        public ImageOptions[] newArray(int size) {
            return new ImageOptions[size];
        }
    };
}
