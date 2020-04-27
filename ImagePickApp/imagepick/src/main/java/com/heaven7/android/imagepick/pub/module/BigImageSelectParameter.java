package com.heaven7.android.imagepick.pub.module;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * the bit image select parameter
 * @author heaven7
 */
public class BigImageSelectParameter implements Parcelable {

    private int mFlags;
    private int mSelectCount;
    private int mMaxSelectCount;
    private String mTopRightText;
    private int mCurrentOrder;           // from 1
    private int mTotalCount;             // max
    // true if support gesture image. if set to true . the gradle should include.
    // implementation 'com.github.chrisbanes:PhotoView:latest.release.here'
    private boolean mSupportGestureImage;

    public boolean hasFlag(int flag){
        return (getFlags() & flag) == flag;
    }

    public void addFlags(int flags) {
        this.mFlags |= flags;
    }

    public void deleteFlags(int flags) {
        this.mFlags &= ~flags;
    }

    public void setCurrentOrder(int index) {
        this.mCurrentOrder = index;
    }
    public void addSelectedCount(int count) {
        mSelectCount += count;
    }

    protected BigImageSelectParameter(BigImageSelectParameter.Builder builder) {
        this.mFlags = builder.mFlags;
        this.mSelectCount = builder.mSelectCount;
        this.mMaxSelectCount = builder.mMaxSelectCount;
        this.mTopRightText = builder.mTopRightText;
        this.mCurrentOrder = builder.mCurrentOrder;
        this.mTotalCount = builder.mTotalCount;
        this.mSupportGestureImage = builder.mSupportGestureImage;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getSelectCount() {
        return this.mSelectCount;
    }

    public int getMaxSelectCount() {
        return this.mMaxSelectCount;
    }

    public String getTopRightText() {
        return this.mTopRightText;
    }

    public int getCurrentOrder() {
        return this.mCurrentOrder;
    }

    public int getTotalCount() {
        return this.mTotalCount;
    }

    public boolean isSupportGestureImage() {
        return this.mSupportGestureImage;
    }

    public static class Builder {
        private int mFlags;
        private int mSelectCount;
        private int mMaxSelectCount;
        private String mTopRightText;
        private int mCurrentOrder;           // from 1
        private int mTotalCount;             // max
        private boolean mSupportGestureImage;

        public Builder setFlags(int mFlags) {
            this.mFlags = mFlags;
            return this;
        }

        public Builder setSelectCount(int mSelectCount) {
            this.mSelectCount = mSelectCount;
            return this;
        }

        public Builder setMaxSelectCount(int mMaxSelectCount) {
            this.mMaxSelectCount = mMaxSelectCount;
            return this;
        }

        public Builder setTopRightText(String mTopRightText) {
            this.mTopRightText = mTopRightText;
            return this;
        }

        public Builder setCurrentOrder(int mCurrentOrder) {
            this.mCurrentOrder = mCurrentOrder;
            return this;
        }

        public Builder setTotalCount(int mTotalCount) {
            this.mTotalCount = mTotalCount;
            return this;
        }

        public Builder setSupportGestureImage(boolean mSupportGestureImage) {
            this.mSupportGestureImage = mSupportGestureImage;
            return this;
        }

        public BigImageSelectParameter build() {
            return new BigImageSelectParameter(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mSelectCount);
        dest.writeInt(this.mMaxSelectCount);
        dest.writeString(this.mTopRightText);
        dest.writeInt(this.mCurrentOrder);
        dest.writeInt(this.mTotalCount);
        dest.writeByte(this.mSupportGestureImage ? (byte) 1 : (byte) 0);
    }

    protected BigImageSelectParameter(Parcel in) {
        this.mFlags = in.readInt();
        this.mSelectCount = in.readInt();
        this.mMaxSelectCount = in.readInt();
        this.mTopRightText = in.readString();
        this.mCurrentOrder = in.readInt();
        this.mTotalCount = in.readInt();
        this.mSupportGestureImage = in.readByte() != 0;
    }

    public static final Creator<BigImageSelectParameter> CREATOR = new Creator<BigImageSelectParameter>() {
        @Override
        public BigImageSelectParameter createFromParcel(Parcel source) {
            return new BigImageSelectParameter(source);
        }

        @Override
        public BigImageSelectParameter[] newArray(int size) {
            return new BigImageSelectParameter[size];
        }
    };
}
