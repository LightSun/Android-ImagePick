package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;

public class BigImageSelectParam implements Parcelable {

    private int mFlags;
    private int mSelectCount;
    private int mMaxSelectCount;
    private String mTopRightText;
    private int mCurrentOrder;  // from 1
    private int mTotalCount;    // max

    public void addFlags(int flags) {
        this.mFlags |= flags;
    }

    public void deleteFlags(int flags) {
        this.mFlags &= ~flags;
    }

    protected BigImageSelectParam(BigImageSelectParam.Builder builder) {
        this.mFlags = builder.mFlags;
        this.mSelectCount = builder.mSelectCount;
        this.mMaxSelectCount = builder.mMaxSelectCount;
        this.mTopRightText = builder.mTopRightText;
        this.mCurrentOrder = builder.mCurrentOrder;
        this.mTotalCount = builder.mTotalCount;
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

    public void setCurrentOrder(int index) {
        this.mCurrentOrder = index;
    }
    public void addSelectedCount(int count) {
        mSelectCount += count;
    }

    public static class Builder {
        private int mFlags;
        private int mSelectCount;
        private int mMaxSelectCount;
        private String mTopRightText;
        private int mCurrentOrder; //from 1
        private int mTotalCount;    // max

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

        public BigImageSelectParam build() {
            return new BigImageSelectParam(this);
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
    }

    protected BigImageSelectParam(Parcel in) {
        this.mFlags = in.readInt();
        this.mSelectCount = in.readInt();
        this.mMaxSelectCount = in.readInt();
        this.mTopRightText = in.readString();
        this.mCurrentOrder = in.readInt();
        this.mTotalCount = in.readInt();
    }

    public static final Creator<BigImageSelectParam> CREATOR = new Creator<BigImageSelectParam>() {
        @Override
        public BigImageSelectParam createFromParcel(Parcel source) {
            return new BigImageSelectParam(source);
        }

        @Override
        public BigImageSelectParam[] newArray(int size) {
            return new BigImageSelectParam[size];
        }
    };
}
