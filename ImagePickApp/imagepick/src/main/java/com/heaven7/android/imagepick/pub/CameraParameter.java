package com.heaven7.android.imagepick.pub;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * the camera parameter.
 * @author heaven7
 * @since 1.0.1
 */
public class CameraParameter implements Parcelable {

    private ImageParameter imageParameter = ImageParameter.DEFAULT;
    private boolean autoFocus = true;
    private int maxCount;

    protected CameraParameter(CameraParameter.Builder builder) {
        this.imageParameter = builder.imageParameter;
        this.autoFocus = builder.autoFocus;
        this.maxCount = builder.maxCount;
    }

    public ImageParameter getImageParameter() {
        return this.imageParameter;
    }

    public boolean isAutoFocus() {
        return this.autoFocus;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public static class Builder {
        private ImageParameter imageParameter = ImageParameter.DEFAULT;
        private boolean autoFocus = true;
        private int maxCount;

        public Builder setImageParameter(ImageParameter imageParameter) {
            this.imageParameter = imageParameter;
            return this;
        }

        public Builder setAutoFocus(boolean autoFocus) {
            this.autoFocus = autoFocus;
            return this;
        }

        public Builder setMaxCount(int maxCount) {
            this.maxCount = maxCount;
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
        dest.writeParcelable(this.imageParameter, flags);
        dest.writeByte(this.autoFocus ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxCount);
    }

    protected CameraParameter(Parcel in) {
        this.imageParameter = in.readParcelable(ImageParameter.class.getClassLoader());
        this.autoFocus = in.readByte() != 0;
        this.maxCount = in.readInt();
    }

    public static final Creator<CameraParameter> CREATOR = new Creator<CameraParameter>() {
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
