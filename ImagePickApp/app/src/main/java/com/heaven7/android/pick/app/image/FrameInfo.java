package com.heaven7.android.pick.app.image;

import android.graphics.Bitmap;
import android.net.Uri;

import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.memory.util.Cacher;

import java.util.Arrays;
import java.util.Objects;

import lib.vida.video.ScaleManager;

/**
 * request frame info , may from local/net uri, may from image or video
 * Created by heaven7 on 2018/1/18 0018.
 */
public class FrameInfo {

    /** indicate scale type is the raw. see more {@linkplain Bitmap#createScaledBitmap(Bitmap, int, int, boolean)}. */
    public static final int SCALE_TYPE_RAW        = -1;

    public static final int SCALE_TYPE_SCALE_CLIP = -2;

    private static final Cacher<FrameInfo, Void> sCacher = new Cacher<FrameInfo, Void>(20) {
        @Override
        public FrameInfo create(Void aVoid) {
            return new FrameInfo();
        }
        @Override
        protected void onRecycleSuccess(FrameInfo frameInfo) {
            frameInfo.reset();
            frameInfo.inUse = false;
        }
    };
    private boolean inUse;

    private int width;
    private int height;

    private int maxWidth;
    private int maxHeight;

    private Uri uri;
    /**
     * true is local uri, false is network uri
     */
    private boolean isLocal;
    /**
     * if get frame from video, in time us.
     * */
    private long frameTime = -1;

    /** time mill-seconds */
    private long timeMsec = -1;

    private boolean fromVideo;

    /** the scale type: default is {@linkplain ScaleManager#ScaleType_CENTER_CROP} */
    private int scaleType = getDefaultScaleType(true);

    private FrameInfo() {}

    public void recycle(){
        if(inUse) {
            sCacher.recycle(this);
        }
    }

    public static int getDefaultScaleType(boolean video){
        return video ? SCALE_TYPE_SCALE_CLIP : SCALE_TYPE_RAW;
    }
    public static FrameInfo obtain(){
        FrameInfo info = sCacher.obtain();
        if(info.inUse){
            throw new IllegalStateException("this frame info is in use.");
        }
        info.inUse = true;
        return info;
    }

    public void reset(){
        width = 0;
        height = 0;
        maxWidth = 0;
        maxHeight = 0;
        uri = null;
        isLocal = false;
        frameTime = -1;
        timeMsec = -1;
        fromVideo = false;
        scaleType = getDefaultScaleType(true);
    }
    public static FrameInfo obtain(Uri uri, boolean localUri, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        FrameInfo info = obtain();
        info.isLocal = localUri;
        info.uri = uri;
        info.width = width;
        info.height = height;
        return info;
    }

    public boolean isInUse(){
        return inUse;
    }

    public String getKey() {
        if(uri == null){
            System.out.println("inUse = " + inUse);
        }
        return uri.toString() + "&"
                + "_st=" + scaleType
                + (fromVideo ? "_ft=" + frameTime : "")
                + (fromVideo ? "_msec=" + timeMsec : "")
                + "_w=" + width
                + "_h=" + height
                + "_maxw=" + maxWidth
                + "_maxh=" + maxHeight;
    }

    public static String getFrameTime(String key){
        String[] strs = key.split("_");
        String main = VisitServices.from(Arrays.asList(strs)).query(new PredicateVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                return s.startsWith("ft=");
            }
        });
        return main.substring(3);
    }

    @Override
    public String toString() {
        return "FrameInfo{" +
                "width=" + width +
                ", height=" + height +
                ", maxWidth=" + maxWidth +
                ", maxHeight=" + maxHeight +
                ", uri=" + uri +
                ", isLocal=" + isLocal +
                ", frameTime=" + frameTime +
                ", timeMsec=" + timeMsec +
                ", fromVideo=" + fromVideo +
                '}';
    }

    public int getMaxWidth() {
        return maxWidth;
    }
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public long getTimeMsec() {
        return timeMsec;
    }
    public void setTimeMsec(long timeMsec) {
        this.timeMsec = timeMsec;
    }

    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
    public Uri getUri() {
        return this.uri;
    }
    public boolean isLocal() {
        return this.isLocal;
    }
    /** time in frames */
    public long getFrameTime() {
        return this.frameTime;
    }
    public boolean isFromVideo() {
        return this.fromVideo;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setUri(Uri uri) {
        this.uri = uri;
    }
    public void setIsLocal(boolean local) {
        isLocal = local;
    }
    /** time in frames */
    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }
    public void setFromVideo(boolean fromVideo) {
        this.fromVideo = fromVideo;
    }

    public int getScaleType() {
        return scaleType;
    }

    /**
     * set the scale type.
     * @param scaleType . see {@linkplain #SCALE_TYPE_RAW},  {@linkplain ScaleManager#ScaleType_CENTER_CROP} and etc.
     */
    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrameInfo info = (FrameInfo) o;
        if(Objects.equals(uri, info.uri)){
            if (width != info.width) return false;
            if (height != info.height) return false;
            if(isFromVideo()){
                if(frameTime != info.frameTime){
                    return false;
                }
                if(timeMsec != info.timeMsec){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
