package com.heaven7.android.imagepick.pub;

public class ImageOptions {

    public static final int FLAG_RESOURCE = 1;
    public static final int FLAG_DATA     = 2;

    private float round;
    private float border;
    private int borderColor;
    private int cacheFlags;

    protected ImageOptions(ImageOptions.Builder builder) {
        this.round = builder.round;
        this.border = builder.border;
        this.borderColor = builder.borderColor;
        this.cacheFlags = builder.cacheFlags;
    }

    public ImageOptions(){}

    public void setRound(float round) {
        this.round = round;
    }

    public void setBorder(float border) {
        this.border = border;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public float getRound() {
        return this.round;
    }

    public float getBorder() {
        return this.border;
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public int getCacheFlags() {
        return this.cacheFlags;
    }

    public static class Builder {
        private float round;
        private float border;
        private int borderColor;
        private int cacheFlags;

        public Builder setRound(float round) {
            this.round = round;
            return this;
        }

        public Builder setBorder(float border) {
            this.border = border;
            return this;
        }

        public Builder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder setCacheFlags(int cacheFlags) {
            this.cacheFlags = cacheFlags;
            return this;
        }

        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
}
