package lib.vida.video;

import android.graphics.Matrix;

/**
 * the scale manager. copy from net.
 *
 * @author heaven7
 */
public final class ScaleManager {

    private static final int PivotPoint_LEFT_TOP = 1;
    private static final int PivotPoint_LEFT_CENTER = 2;
    private static final int PivotPoint_LEFT_BOTTOM = 3;
    private static final int PivotPoint_CENTER_TOP = 4;
    private static final int PivotPoint_CENTER = 5;
    private static final int PivotPoint_CENTER_BOTTOM = 6;
    private static final int PivotPoint_RIGHT_TOP = 7;
    private static final int PivotPoint_RIGHT_CENTER = 8;
    private static final int PivotPoint_RIGHT_BOTTOM = 9;

    public static final int ScaleType_NONE = 0;
    public static final int ScaleType_FIT_XY = 1;
    public static final int ScaleType_FIT_START = 2;
    public static final int ScaleType_FIT_CENTER = 3;
    public static final int ScaleType_FIT_END = 4;

    public static final int ScaleType_LEFT_TOP = 5;
    public static final int ScaleType_LEFT_CENTER = 6;
    public static final int ScaleType_LEFT_BOTTOM = 7;
    public static final int ScaleType_CENTER_TOP = 8;
    public static final int ScaleType_CENTER = 9;
    public static final int ScaleType_CENTER_BOTTOM = 10;
    public static final int ScaleType_RIGHT_TOP = 11;
    public static final int ScaleType_RIGHT_CENTER = 12;
    public static final int ScaleType_RIGHT_BOTTOM = 13;

    public static final int ScaleType_LEFT_TOP_CROP = 14;
    public static final int ScaleType_LEFT_CENTER_CROP = 15;
    public static final int ScaleType_LEFT_BOTTOM_CROP = 16;
    public static final int ScaleType_CENTER_TOP_CROP = 17;
    public static final int ScaleType_CENTER_CROP = 18;
    public static final int ScaleType_CENTER_BOTTOM_CROP = 19;
    public static final int ScaleType_RIGHT_TOP_CROP = 20;
    public static final int ScaleType_RIGHT_CENTER_CROP = 21;
    public static final int ScaleType_RIGHT_BOTTOM_CROP = 22;

    public static final int ScaleType_START_INSIDE = 23;
    public static final int ScaleType_CENTER_INSIDE = 24;
    public static final int ScaleType_END_INSIDE = 25;

    public static final int ScaleType_MATCH_WIDTH = 26;
    public static final int ScaleType_MATCH_HEIGHT = 27;

    /**
     * the dest size
     */
    private final Size mViewSize;
    /**
     * the src size
     */
    private final Size mVideoSize;

    //viewSize is the dest size
    ScaleManager(Size viewSize, Size videoSize) {
        mViewSize = viewSize;
        mVideoSize = videoSize;
    }

    public static Size ofSize(int width, int height) {
        return new Size(width, height);
    }

    public static Matrix getScaleMatrix(Size viewSize, Size videoSize, int scaleType) {
        return new ScaleManager(viewSize, videoSize).getScaleMatrix(scaleType);
    }

    Matrix getScaleMatrix(int scaleType) {
        switch (scaleType) {
            case ScaleType_NONE:
                return getNoScale();

            case ScaleType_FIT_XY:
                return fitXY();
            case ScaleType_FIT_CENTER:
                return fitCenter();
            case ScaleType_FIT_START:
                return fitStart();
            case ScaleType_FIT_END:
                return fitEnd();

            case ScaleType_LEFT_TOP:
                return getOriginalScale(PivotPoint_LEFT_TOP);
            case ScaleType_LEFT_CENTER:
                return getOriginalScale(PivotPoint_LEFT_CENTER);
            case ScaleType_LEFT_BOTTOM:
                return getOriginalScale(PivotPoint_LEFT_BOTTOM);
            case ScaleType_CENTER_TOP:
                return getOriginalScale(PivotPoint_CENTER_TOP);
            case ScaleType_CENTER:
                return getOriginalScale(PivotPoint_CENTER);
            case ScaleType_CENTER_BOTTOM:
                return getOriginalScale(PivotPoint_CENTER_BOTTOM);
            case ScaleType_RIGHT_TOP:
                return getOriginalScale(PivotPoint_RIGHT_TOP);
            case ScaleType_RIGHT_CENTER:
                return getOriginalScale(PivotPoint_RIGHT_CENTER);
            case ScaleType_RIGHT_BOTTOM:
                return getOriginalScale(PivotPoint_RIGHT_BOTTOM);

            case ScaleType_LEFT_TOP_CROP:
                return getCropScale(PivotPoint_LEFT_TOP);
            case ScaleType_LEFT_CENTER_CROP:
                return getCropScale(PivotPoint_LEFT_CENTER);
            case ScaleType_LEFT_BOTTOM_CROP:
                return getCropScale(PivotPoint_LEFT_BOTTOM);
            case ScaleType_CENTER_TOP_CROP:
                return getCropScale(PivotPoint_CENTER_TOP);
            case ScaleType_CENTER_CROP:
                return getCropScale(PivotPoint_CENTER);
            case ScaleType_CENTER_BOTTOM_CROP:
                return getCropScale(PivotPoint_CENTER_BOTTOM);
            case ScaleType_RIGHT_TOP_CROP:
                return getCropScale(PivotPoint_RIGHT_TOP);
            case ScaleType_RIGHT_CENTER_CROP:
                return getCropScale(PivotPoint_RIGHT_CENTER);
            case ScaleType_RIGHT_BOTTOM_CROP:
                return getCropScale(PivotPoint_RIGHT_BOTTOM);

            case ScaleType_START_INSIDE:
                return startInside();
            case ScaleType_CENTER_INSIDE:
                return centerInside();
            case ScaleType_END_INSIDE:
                return endInside();

            default:
                throw new UnsupportedOperationException();
        }
    }

    private Matrix getMatrix(float sx, float sy, float px, float py) {
        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy, px, py);
        return matrix;
    }

    private Matrix getMatrix(float sx, float sy, int pivotPoint) {
        switch (pivotPoint) {
            case PivotPoint_LEFT_TOP:
                return getMatrix(sx, sy, 0, 0);
            case PivotPoint_LEFT_CENTER:
                return getMatrix(sx, sy, 0, mViewSize.getHeight() / 2f);
            case PivotPoint_LEFT_BOTTOM:
                return getMatrix(sx, sy, 0, mViewSize.getHeight());
            case PivotPoint_CENTER_TOP:
                return getMatrix(sx, sy, mViewSize.getWidth() / 2f, 0);
            case PivotPoint_CENTER:
                return getMatrix(sx, sy, mViewSize.getWidth() / 2f, mViewSize.getHeight() / 2f);
            case PivotPoint_CENTER_BOTTOM:
                return getMatrix(sx, sy, mViewSize.getWidth() / 2f, mViewSize.getHeight());
            case PivotPoint_RIGHT_TOP:
                return getMatrix(sx, sy, mViewSize.getWidth(), 0);
            case PivotPoint_RIGHT_CENTER:
                return getMatrix(sx, sy, mViewSize.getWidth(), mViewSize.getHeight() / 2f);
            case PivotPoint_RIGHT_BOTTOM:
                return getMatrix(sx, sy, mViewSize.getWidth(), mViewSize.getHeight());
            default:
                throw new IllegalArgumentException("Illegal PivotPoint");
        }
    }

    private Matrix getNoScale() {
        float sx = mVideoSize.getWidth() / (float) mViewSize.getWidth();
        float sy = mVideoSize.getHeight() / (float) mViewSize.getHeight();
        return getMatrix(sx, sy, PivotPoint_LEFT_TOP);
    }

    private Matrix getFitScale(int pivotPoint) {
        float sx = (float) mViewSize.getWidth() / mVideoSize.getWidth();
        float sy = (float) mViewSize.getHeight() / mVideoSize.getHeight();
        float minScale = Math.min(sx, sy);
        sx = minScale / sx;
        sy = minScale / sy;
        return getMatrix(sx, sy, pivotPoint);
    }

    private Matrix fitXY() {
        return getMatrix(1, 1, PivotPoint_LEFT_TOP);
    }

    private Matrix fitStart() {
        return getFitScale(PivotPoint_LEFT_TOP);
    }

    private Matrix fitCenter() {
        return getFitScale(PivotPoint_CENTER);
    }

    private Matrix fitEnd() {
        return getFitScale(PivotPoint_RIGHT_BOTTOM);
    }

    private Matrix getOriginalScale(int pivotPoint) {
        float sx = mVideoSize.getWidth() / (float) mViewSize.getWidth();
        float sy = mVideoSize.getHeight() / (float) mViewSize.getHeight();
        return getMatrix(sx, sy, pivotPoint);
    }

    private Matrix getCropScale(int pivotPoint) {
        float sx = mViewSize.getWidth() * 1f / mVideoSize.getWidth();
        float sy = mViewSize.getHeight() * 1f / mVideoSize.getHeight();
        float maxScale = Math.max(sx, sy);
        sx = maxScale / sx;
        sy = maxScale / sy;

        return getMatrix(sx, sy, pivotPoint);
    }

    private Matrix startInside() {
        if (mVideoSize.getHeight() <= mViewSize.getWidth()
                && mVideoSize.getHeight() <= mViewSize.getHeight()) {
            // video is smaller than view size
            return getOriginalScale(PivotPoint_LEFT_TOP);
        } else {
            // either of width or height of the video is larger than view size
            return fitStart();
        }
    }

    private Matrix centerInside() {
        if (mVideoSize.getHeight() <= mViewSize.getWidth()
                && mVideoSize.getHeight() <= mViewSize.getHeight()) {
            // video is smaller than view size
            return getOriginalScale(PivotPoint_CENTER);
        } else {
            // either of width or height of the video is larger than view size
            return fitCenter();
        }
    }

    private Matrix endInside() {
        if (mVideoSize.getHeight() <= mViewSize.getWidth()
                && mVideoSize.getHeight() <= mViewSize.getHeight()) {
            // video is smaller than view size
            return getOriginalScale(PivotPoint_RIGHT_BOTTOM);
        } else {
            // either of width or height of the video is larger than view size
            return fitEnd();
        }
    }

    public static class Size {
        private final int mWidth;
        private final int mHeight;

        public Size(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }

        @Override
        public String toString() {
            return "Size{" +
                    "mWidth=" + mWidth +
                    ", mHeight=" + mHeight +
                    '}';
        }
    }
}
