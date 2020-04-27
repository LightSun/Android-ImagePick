package com.heaven7.android.imagepick.pub.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * the media option of filter.
 * @author heaven7
 * @since 1.0.5
 */
public class MediaOption implements Parcelable {

    public static final MediaOption DEFAULT = new MediaOption.Builder()
            .setImageMimes(Arrays.asList("image/jpeg", "image/png", "image/jpg")) // "image/gif" allow width height = 0.
            .setVideoMimes(Arrays.asList("video/mp4"))
            .build();

    private List<String> mImageMimes;
    private List<String> mVideoMimes;

    private long mVideoMinDuration;
    private long mVideoMaxDuration = Long.MAX_VALUE;

    private int mMaxImageSize = Integer.MAX_VALUE;  // in bytes
    private long mMaxVideoSize = Long.MAX_VALUE;    // in bytes

    public static MediaOption withGif(){
        return new MediaOption.Builder()
                .setImageMimes(Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/gif"))
                .setVideoMimes(Arrays.asList("video/mp4"))
                .build();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mImageMimes);
        dest.writeStringList(this.mVideoMimes);
        dest.writeLong(this.mVideoMinDuration);
        dest.writeLong(this.mVideoMaxDuration);
        dest.writeInt(this.mMaxImageSize);
        dest.writeLong(this.mMaxVideoSize);
    }

    protected MediaOption(Parcel in) {
        this.mImageMimes = in.createStringArrayList();
        this.mVideoMimes = in.createStringArrayList();
        this.mVideoMinDuration = in.readLong();
        this.mVideoMaxDuration = in.readLong();
        this.mMaxImageSize = in.readInt();
        this.mMaxVideoSize = in.readLong();
    }

    public static final Creator<MediaOption> CREATOR = new Creator<MediaOption>() {
        @Override
        public MediaOption createFromParcel(Parcel source) {
            return new MediaOption(source);
        }

        @Override
        public MediaOption[] newArray(int size) {
            return new MediaOption[size];
        }
    };

    protected MediaOption(MediaOption.Builder builder) {
        this.mImageMimes = builder.mImageMimes;
        this.mVideoMimes = builder.mVideoMimes;
        this.mVideoMinDuration = builder.mVideoMinDuration;
        this.mVideoMaxDuration = builder.mVideoMaxDuration;
        this.mMaxImageSize = builder.mMaxImageSize;
        this.mMaxVideoSize = builder.mMaxVideoSize;
    }

    public List<String> getImageMimes() {
        return this.mImageMimes;
    }

    public List<String> getVideoMimes() {
        return this.mVideoMimes;
    }

    public long getVideoMinDuration() {
        return this.mVideoMinDuration;
    }

    public long getVideoMaxDuration() {
        return this.mVideoMaxDuration;
    }

    public int getMaxImageSize() {
        return this.mMaxImageSize;
    }

    public long getMaxVideoSize() {
        return this.mMaxVideoSize;
    }

    public static class Builder {
        private List<String> mImageMimes;
        private List<String> mVideoMimes;
        private long mVideoMinDuration;
        private long mVideoMaxDuration = Long.MAX_VALUE;
        private int mMaxImageSize = Integer.MAX_VALUE;  // in bytes
        private long mMaxVideoSize = Long.MAX_VALUE;    // in bytes

        public Builder setImageMimes(List<String> mImageMimes) {
            this.mImageMimes = mImageMimes;
            return this;
        }

        public Builder setVideoMimes(List<String> mVideoMimes) {
            this.mVideoMimes = mVideoMimes;
            return this;
        }

        public Builder setVideoMinDuration(long mVideoMinDuration) {
            this.mVideoMinDuration = mVideoMinDuration;
            return this;
        }

        public Builder setVideoMaxDuration(long mVideoMaxDuration) {
            this.mVideoMaxDuration = mVideoMaxDuration;
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

        public MediaOption build() {
            return new MediaOption(this);
        }
    }
}
