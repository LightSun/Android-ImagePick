package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * the camera parameter.
 * @author heaven7
 * @since 1.0.1
 */
public class CameraParameter implements Parcelable {

    public static final int FORMAT_RGB_565    = 1;
    public static final int FORMAT_ARGB_8888  = 2;
    public static final int FORMAT_RGBA_F16   = 3;
    private int maxWidth = 4000;
    private int maxHeight = 4000;
    private int format = FORMAT_RGB_565;

    protected CameraParameter(CameraParameter.Builder builder) {
        this.maxWidth = builder.maxWidth;
        this.maxHeight = builder.maxHeight;
        this.format = builder.format;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getFormat() {
        return this.format;
    }

    public static class Builder {
        private int maxWidth = 4000;
        private int maxHeight = 4000;
        private int format = FORMAT_RGB_565;

        public Builder setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public Builder setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder setFormat(int format) {
            this.format = format;
            return this;
        }

        public CameraParameter build() {
            return new CameraParameter(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxWidth);
        dest.writeInt(this.maxHeight);
        dest.writeInt(this.format);
    }

    protected CameraParameter(Parcel in) {
        this.maxWidth = in.readInt();
        this.maxHeight = in.readInt();
        this.format = in.readInt();
    }

    public static final Parcelable.Creator<CameraParameter> CREATOR = new Parcelable.Creator<CameraParameter>() {
        @Override
        public CameraParameter createFromParcel(Parcel source) {
            return new CameraParameter(source);
        }

        @Override
        public CameraParameter[] newArray(int size) {
            return new CameraParameter[size];
        }
    };
}
