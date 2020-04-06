package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;

public class SeeImageParameter implements Parcelable {

    private int mSpanCount = 3;
    private int mSpace;
    private int mAspectX = 1;
    private int mAspectY = 1;

    private @DrawableRes int pauseIconRes;


    protected SeeImageParameter(SeeImageParameter.Builder builder) {
        this.mSpanCount = builder.mSpanCount;
        this.mSpace = builder.mSpace;
        this.mAspectX = builder.mAspectX;
        this.mAspectY = builder.mAspectY;
        this.pauseIconRes = builder.pauseIconRes;
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


    public int getPauseIconRes() {
        return this.pauseIconRes;
    }

    public static class Builder {
        private int mSpanCount = 3;
        private int mSpace;
        private int mAspectX = 1;
        private int mAspectY = 1;
        private @DrawableRes int pauseIconRes;

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

        public Builder setPauseIconRes(int pauseIconRes) {
            this.pauseIconRes = pauseIconRes;
            return this;
        }

        public SeeImageParameter build() {
            return new SeeImageParameter(this);
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
        dest.writeInt(this.pauseIconRes);
    }

    protected SeeImageParameter(Parcel in) {
        this.mSpanCount = in.readInt();
        this.mSpace = in.readInt();
        this.mAspectX = in.readInt();
        this.mAspectY = in.readInt();
        this.pauseIconRes = in.readInt();
    }

    public static final Creator<SeeImageParameter> CREATOR = new Creator<SeeImageParameter>() {
        @Override
        public SeeImageParameter createFromParcel(Parcel source) {
            return new SeeImageParameter(source);
        }

        @Override
        public SeeImageParameter[] newArray(int size) {
            return new SeeImageParameter[size];
        }
    };
}
