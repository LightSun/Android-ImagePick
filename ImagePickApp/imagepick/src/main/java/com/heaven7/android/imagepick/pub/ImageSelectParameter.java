package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;

/**
 * the image select parameter
 * @author heaven7
 */
public class ImageSelectParameter implements Parcelable {

    private int mSpanCount = 4;
    private int mSpace;
    private int mAspectX = 1;
    private int mAspectY = 1;
    private int mMaxSelect = 1;
    private int mFlags = PickConstants.FLAG_IMAGE;

    /** filter options */
    private int mMaxImageSize;  //in bytes
    private long mMaxVideoSize; //in bytes

    private @DrawableRes int mDefaultDirIconId;

    private ImageParameter imageParameter;
    /** the cache dir of file */
    private String cacheDir;

    protected ImageSelectParameter(ImageSelectParameter.Builder builder) {
        this.mSpanCount = builder.mSpanCount;
        this.mSpace = builder.mSpace;
        this.mAspectX = builder.mAspectX;
        this.mAspectY = builder.mAspectY;
        this.mMaxSelect = builder.mMaxSelect;
        this.mFlags = builder.mFlags;
        this.mMaxImageSize = builder.mMaxImageSize;
        this.mMaxVideoSize = builder.mMaxVideoSize;
        this.mDefaultDirIconId = builder.mDefaultDirIconId;
        this.imageParameter = builder.imageParameter;
        this.cacheDir = builder.cacheDir;
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public int getSpace() {
        return this.mSpace;
    }

    public int getAspectX() {
        return this.mAspectX;
    }

    public int getAspectY() {
        return this.mAspectY;
    }

    public int getMaxSelect() {
        return this.mMaxSelect;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getMaxImageSize() {
        return this.mMaxImageSize;
    }

    public long getMaxVideoSize() {
        return this.mMaxVideoSize;
    }

    public int getDefaultDirIconId() {
        return this.mDefaultDirIconId;
    }

    public ImageParameter getImageParameter() {
        return this.imageParameter;
    }

    public String getCacheDir() {
        return this.cacheDir;
    }

    public static class Builder {
        private int mSpanCount = 4;
        private int mSpace;
        private int mAspectX = 1;
        private int mAspectY = 1;
        private int mMaxSelect = 1;
        private int mFlags = PickConstants.FLAG_IMAGE;
        /** filter options */
        private int mMaxImageSize;  //in bytes
        private long mMaxVideoSize; //in bytes
        private @DrawableRes
        int mDefaultDirIconId;
        private ImageParameter imageParameter;
        private String cacheDir;

        public Builder setSpanCount(int mSpanCount) {
            this.mSpanCount = mSpanCount;
            return this;
        }

        public Builder setSpace(int mSpace) {
            this.mSpace = mSpace;
            return this;
        }

        public Builder setAspectX(int mAspectX) {
            this.mAspectX = mAspectX;
            return this;
        }

        public Builder setAspectY(int mAspectY) {
            this.mAspectY = mAspectY;
            return this;
        }

        public Builder setMaxSelect(int mMaxSelect) {
            this.mMaxSelect = mMaxSelect;
            return this;
        }

        public Builder setFlags(int mFlags) {
            this.mFlags = mFlags;
            return this;
        }

        public Builder setMaxImageSize(int mMaxImageSize) {
            this.mMaxImageSize = mMaxImageSize;
            return this;
        }

        public Builder setMaxVideoSize(long mMaxVideoSize) {
            this.mMaxVideoSize = mMaxVideoSize;
            return this;
        }

        public Builder setDefaultDirIconId(int mDefaultDirIconId) {
            this.mDefaultDirIconId = mDefaultDirIconId;
            return this;
        }

        public Builder setImageParameter(ImageParameter imageParameter) {
            this.imageParameter = imageParameter;
            return this;
        }

        public Builder setCacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        public ImageSelectParameter build() {
            return new ImageSelectParameter(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSpanCount);
        dest.writeInt(this.mSpace);
        dest.writeInt(this.mAspectX);
        dest.writeInt(this.mAspectY);
        dest.writeInt(this.mMaxSelect);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mMaxImageSize);
        dest.writeLong(this.mMaxVideoSize);
        dest.writeInt(this.mDefaultDirIconId);
        dest.writeParcelable(this.imageParameter, flags);
        dest.writeString(this.cacheDir);
    }

    protected ImageSelectParameter(Parcel in) {
        this.mSpanCount = in.readInt();
        this.mSpace = in.readInt();
        this.mAspectX = in.readInt();
        this.mAspectY = in.readInt();
        this.mMaxSelect = in.readInt();
        this.mFlags = in.readInt();
        this.mMaxImageSize = in.readInt();
        this.mMaxVideoSize = in.readLong();
        this.mDefaultDirIconId = in.readInt();
        this.imageParameter = in.readParcelable(ImageParameter.class.getClassLoader());
        this.cacheDir = in.readString();
    }

    public static final Creator<ImageSelectParameter> CREATOR = new Creator<ImageSelectParameter>() {
        @Override
        public ImageSelectParameter createFromParcel(Parcel source) {
            return new ImageSelectParameter(source);
        }

        @Override
        public ImageSelectParameter[] newArray(int size) {
            return new ImageSelectParameter[size];
        }
    };
}
