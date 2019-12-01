package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

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

    private @DrawableRes int mDefaultDirIconId;

    protected ImageSelectParameter(ImageSelectParameter.Builder builder) {
        this.mSpanCount = builder.mSpanCount;
        this.mSpace = builder.mSpace;
        this.mAspectX = builder.mAspectX;
        this.mAspectY = builder.mAspectY;
        this.mMaxSelect = builder.mMaxSelect;
        this.mFlags = builder.mFlags;
        this.mDefaultDirIconId = builder.mDefaultDirIconId;
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

    public int getDefaultDirIconId() {
        return this.mDefaultDirIconId;
    }

    public static class Builder {
        private int mSpanCount = 4;
        private int mSpace;
        private int mAspectX = 1;
        private int mAspectY = 1;
        private int mMaxSelect = 1;
        private int mFlags = PickConstants.FLAG_IMAGE;
        private @DrawableRes
        int mDefaultDirIconId;

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

        public Builder setDefaultDirIconId(int mDefaultDirIconId) {
            this.mDefaultDirIconId = mDefaultDirIconId;
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
        dest.writeInt(this.mDefaultDirIconId);
    }

    protected ImageSelectParameter(Parcel in) {
        this.mSpanCount = in.readInt();
        this.mSpace = in.readInt();
        this.mAspectX = in.readInt();
        this.mAspectY = in.readInt();
        this.mMaxSelect = in.readInt();
        this.mFlags = in.readInt();
        this.mDefaultDirIconId = in.readInt();
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
