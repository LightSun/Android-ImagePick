package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * the image select parameter
 * @author heaven7
 */
public class ImageSelectParameter implements Parcelable {

    private int mSpanCount;
    private int mSpace;
    private int mAspectX;
    private int mAspectY;
    private int mMaxSelect;

    protected ImageSelectParameter(ImageSelectParameter.Builder builder) {
        this.mSpanCount = builder.mSpanCount;
        this.mSpace = builder.mSpace;
        this.mAspectX = builder.mAspectX;
        this.mAspectY = builder.mAspectY;
        this.mMaxSelect = builder.mMaxSelect;
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

    public static class Builder {
        private int mSpanCount = 4;
        private int mSpace = 0;
        private int mAspectX = 1;
        private int mAspectY = 1;
        private int mMaxSelect = 1; //i means single select

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
    }

    protected ImageSelectParameter(Parcel in) {
        this.mSpanCount = in.readInt();
        this.mSpace = in.readInt();
        this.mAspectX = in.readInt();
        this.mAspectY = in.readInt();
        this.mMaxSelect = in.readInt();
    }

    public static final Parcelable.Creator<ImageSelectParameter> CREATOR = new Parcelable.Creator<ImageSelectParameter>() {
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
