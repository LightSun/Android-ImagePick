package lib.vida.video;

import android.util.Log;
import android.view.View;

/*public*/ final class RenderMeasure {

    private final String TAG = "RenderMeasure";

    private int mVideoWidth;
    private int mVideoHeight;

    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mMeasureWidth;
    private int mMeasureHeight;

    private int mCurrAspectRatio = AspectRatio.AspectRatio_FIT_PARENT;
    private int mVideoRotationDegree;
    private float pixelWidthHeightRatio;

    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            int tempSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempSpec;
        }

        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mCurrAspectRatio == AspectRatio.AspectRatio_MATCH_PARENT) {
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
                float displayAspectRatio;
                switch (mCurrAspectRatio) {
                    case AspectRatio.AspectRatio_16_9:
                        displayAspectRatio = 16.0f / 9.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case AspectRatio.AspectRatio_4_3:
                        displayAspectRatio = 4.0f / 3.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case AspectRatio.AspectRatio_FIT_PARENT:
                    case AspectRatio.AspectRatio_FILL_PARENT:
                    case AspectRatio.AspectRatio_ORIGIN:
                    default:
                        displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
                        if (mVideoSarNum > 0 && mVideoSarDen > 0)
                            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
                        break;
                }
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;

                switch (mCurrAspectRatio) {
                    case AspectRatio.AspectRatio_FIT_PARENT:
                    case AspectRatio.AspectRatio_16_9:
                    case AspectRatio.AspectRatio_4_3:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                    case AspectRatio.AspectRatio_FILL_PARENT:
                        if (shouldBeWider) {
                            // not high enough, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        } else {
                            // not wide enough, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        }
                        break;
                    case AspectRatio.AspectRatio_ORIGIN:
                    default:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }

        mMeasureWidth = width;
        mMeasureHeight = height;
    }

    public void doOnConfigurationChanged(int newConfig, View view) {
        float videoAspectRatio =
                (mVideoHeight == 0 || mVideoWidth == 0) ? 1 : (mVideoWidth * pixelWidthHeightRatio) / mVideoHeight;

        // Try to apply rotation transformation when our surface is a TextureView.
        if (newConfig != 0) {
            if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                // We will apply a rotation 90/270 degree to the output texture of the TextureView.
                // In this case, the output video's width and height will be swapped.
                videoAspectRatio = 1 / videoAspectRatio;
            }
        }
        if (videoAspectRatio < 1) {
            setVideoRotation(newConfig);
            view.setRotation(newConfig);
        }

    }

    void setPixelWidthHeightRatio(float pixelWidthHeightRatio) {
        this.pixelWidthHeightRatio = pixelWidthHeightRatio;
    }

    /**
     * Sets video sample aspect ratio.
     *
     * @param videoSarNum the video sar num
     * @param videoSarDen the video sar den
     */
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    /**
     * Set video size.
     *
     * @param videoWidth  the video width
     * @param videoHeight the video height
     */
    public void setVideoSize(int videoWidth, int videoHeight) {
        Log.d(TAG, "videoWidth = " + videoWidth + " videoHeight = " + videoHeight);
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
    }

    /**
     * Sets video rotation.
     *
     * @param videoRotationDegree the video rotation degree
     */
    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    /**
     * Set aspect ratio.
     *
     * @param aspectRatio the aspect ratio
     */
    public void setAspectRatio(@AspectRatio.ResizeMode int aspectRatio) {
        this.mCurrAspectRatio = aspectRatio;
    }

    /**
     * Gets curr aspect ratio.
     *
     * @return the curr aspect ratio
     */
    public int getmCurrAspectRatio() {
        return mCurrAspectRatio;
    }

    /**
     * Gets measure width.
     *
     * @return the measure width
     */
    public int getMeasureWidth() {
        return mMeasureWidth;
    }

    public int getMeasureHeight() {
        return mMeasureHeight;
    }
}
