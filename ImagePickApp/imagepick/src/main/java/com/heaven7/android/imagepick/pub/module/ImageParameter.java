package com.heaven7.android.imagepick.pub.module;

import android.os.Parcel;
import android.os.Parcelable;

import static com.heaven7.android.imagepick.pub.PickConstants.FORMAT_RGB_565;

/**
 * the image parameter of output
 * @since 1.0.2
 */
public class ImageParameter implements Parcelable {

    public static final ImageParameter DEFAULT = new ImageParameter();

    private int maxWidth = 4000;
    private int maxHeight = 4000;
    private int format = FORMAT_RGB_565;

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setFormat(int format) {
        this.format = format;
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

    public ImageParameter() {
    }

    protected ImageParameter(Parcel in) {
        this.maxWidth = in.readInt();
        this.maxHeight = in.readInt();
        this.format = in.readInt();
    }

    public static final Parcelable.Creator<ImageParameter> CREATOR = new Parcelable.Creator<ImageParameter>() {
        @Override
        public ImageParameter createFromParcel(Parcel source) {
            return new ImageParameter(source);
        }

        @Override
        public ImageParameter[] newArray(int size) {
            return new ImageParameter[size];
        }
    };

    protected ImageParameter(ImageParameter.Builder builder) {
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

        public ImageParameter build() {
            return new ImageParameter(this);
        }
    }
}
