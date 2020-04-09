package com.heaven7.android.imagepick;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.RestrictTo;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.CameraParameter;
import com.heaven7.android.imagepick.pub.ExceptionHandler;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.ImageLoadDelegate;
import com.heaven7.android.imagepick.pub.ImagePickDelegate;
import com.heaven7.android.imagepick.pub.ImageSelectParameter;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.SeeImageParameter;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.android.imagepick.pub.delegate.DefaultSeeBigImageDelegate;
import com.heaven7.android.imagepick.pub.delegate.DefaultSeeImageDelegate;
import com.heaven7.android.imagepick.pub.delegate.SeeBigImageDelegate;
import com.heaven7.android.imagepick.pub.delegate.SeeImageDelegate;
import com.heaven7.android.util2.LauncherIntent;
import com.heaven7.core.util.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.heaven7.android.imagepick.pub.PickConstants.REQ_BROWSE_BIG_IMAGE;
import static com.heaven7.android.imagepick.pub.PickConstants.REQ_BROWSE_IMAGE;
import static com.heaven7.android.imagepick.pub.PickConstants.REQ_CAMERA;
import static com.heaven7.android.imagepick.pub.PickConstants.REQ_GALLERY;

/**
 * the image pick manager. used for com.heaven7.android.imagepick.internal
 * @author heaven7
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ImagePickDelegateImpl implements ImagePickDelegate {

    private static final String TAG = "ImagePickImpl";
    private static ImagePickDelegateImpl sInstance;
    private OnImageProcessListener mImageListener;
    private ExceptionHandler mHandler;
    private VideoManageDelegate mVideoManager;
    private ImageLoadDelegate mImageLoadDelegate;
    private ViewPager.OnPageChangeListener mPageListener;

    private ImagePickDelegateImpl(){}

    private List<String> mImages = new ArrayList<>(5);
    private List<IImageItem> mItems;
    private List<ImagePickDelegate.OnSelectStateChangedListener> mSelectListeners;

    public static ImagePickDelegateImpl getDefault(){
        if(sInstance == null){
            sInstance = new ImagePickDelegateImpl();
        }
        return sInstance;
    }
    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener l) {
        mPageListener = l;
    }
    @Override
    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return mPageListener;
    }
    @Override
    public void setImageLoadDelegate(ImageLoadDelegate delegate) {
        mImageLoadDelegate = delegate;
    }
    @Override
    public ImageLoadDelegate getImageLoadDelegate() {
        return mImageLoadDelegate;
    }

    public List<String> getImages(){
        return mImages;
    }
    public void addImagePath(String file){
        if(!mImages.contains(file)){
            mImages.add(file);
        }
        //System.out.println("addImagePath left: " + mImages);
    }
    public void removeImagePath(String file){
        mImages.remove(file);
        //System.out.println("removeImagePath left: " + mImages);
    }
    public List<IImageItem> getImageItems() {
        Logger.d(TAG, "getImageItems", "out size = " + mItems.size());
        return mItems;
    }
    public void setImageItems(List<? extends IImageItem> mItems) {
        Logger.d(TAG, "setImageItems", "in size = " + mItems.size());
        this.mItems = new ArrayList<>(mItems);
    }
    public void clearImages() {
        mImages.clear();
    }
    public void dispatchSelectStateChanged(IImageItem item, boolean select) {
        if(mSelectListeners != null){
            for (OnSelectStateChangedListener l : mSelectListeners){
                l.onSelectStateChanged(item, select);
            }
        }
    }

    @Override
    public void setExceptionHandler(ExceptionHandler handler) {
        this.mHandler = handler;
    }

    @Override
    public void setVideoManageDelegate(VideoManageDelegate delegate) {
        this.mVideoManager = delegate;
    }
    @Override
    public VideoManageDelegate getVideoManageDelegate() {
        return mVideoManager;
    }
    @Override
    public ExceptionHandler getExceptionHandler() {
        return mHandler;
    }
    @Override
    public void setOnImageProcessListener(OnImageProcessListener l) {
        mImageListener = l;
    }
    @Override
    public OnImageProcessListener getOnImageProcessListener() {
        return mImageListener;
    }
    @Override
    public void addOnSelectStateChangedListener(OnSelectStateChangedListener l) {
        if(mSelectListeners == null){
            mSelectListeners = new ArrayList<>(3);
        }
        if(!mSelectListeners.contains(l)){
            mSelectListeners.add(l);
        }
    }

    @Override
    public void removeOnSelectStateChangedListener(OnSelectStateChangedListener l) {
        if(mSelectListeners != null){
            mSelectListeners.remove(l);
        }
    }

    @Override
    public void startCamera(Activity context) {
        new LauncherIntent.Builder()
                .setClass(context, CameraActivity.class)
                .putExtra(PickConstants.KEY_PARAMS, new CameraParameter.Builder().build())
                .build()
                .startActivityForResult(REQ_CAMERA);
    }

    @Override
    public void startCamera(Activity context, CameraParameter parameter) {
        new LauncherIntent.Builder()
                .setClass(context, CameraActivity.class)
                .putExtra(PickConstants.KEY_PARAMS, parameter)
                .build()
                .startActivityForResult(REQ_CAMERA);
    }
    @Override
    public void startBrowseImages2(Activity context, Class<? extends SeeImageDelegate> clazz, SeeImageParameter parameter, Bundle extra){
        if(extra == null) {
            extra = new Bundle();
        }
        new LauncherIntent.Builder()
                .setClass(context, SeeImageActivity.class)
                .putExtra(PickConstants.KEY_PARAMS, parameter)
                .putExtra(PickConstants.KEY_DELEGATE, clazz.getName())
                .putExtras(extra)
                .build()
                .startActivityForResult(REQ_BROWSE_IMAGE);
    }

    @Override
    public void startBrowseImages2(Activity context, SeeImageParameter parameter) {
        startBrowseImages2(context, DefaultSeeImageDelegate.class, parameter, null);
    }

    @Override
    public void startBrowseImages(Activity activity, ImageSelectParameter param) {
        if(param == null){
            param = new ImageSelectParameter.Builder().build();
        }
        new LauncherIntent.Builder()
                .setClass(activity, ImageSelectActivity.class)
                .putExtra(PickConstants.KEY_PARAMS, param)
                .build()
                .startActivityForResult(REQ_GALLERY);
    }

    @Override
    public void startBrowseBigImages(Activity context, BigImageSelectParameter param, List<? extends IImageItem> allItems, IImageItem single) {
        startBrowseBigImages(context, param, DefaultSeeBigImageDelegate.class, null,allItems, single);
    }

    @Override
    public void startBrowseBigImages(Activity context, BigImageSelectParameter param, Class<? extends SeeBigImageDelegate> clazz, Bundle extra,
                                     List<? extends IImageItem> allItems, IImageItem single) {
        if(param == null || allItems == null){
            throw new IllegalArgumentException();
        }
        setImageItems(allItems);
        LauncherIntent.Builder builder = new LauncherIntent.Builder()
                .setClass(context, SeeBigImageActivity.class)
                .putExtra(PickConstants.KEY_DELEGATE, clazz.getName())
                .putExtra(PickConstants.KEY_PARAMS, param)
                .putExtra(PickConstants.KEY_SINGLE_ITEM, single);
        if(extra != null){
            builder.putExtras(extra);
        }
        builder.build().startActivityForResult(REQ_BROWSE_BIG_IMAGE);
    }

    /*public*/ void onImageProcessStart(final Activity activity, final int count) {
        if(activity == null){
            return;
        }
        final ImagePickDelegate.OnImageProcessListener dd = ImagePickDelegateImpl.getDefault().getOnImageProcessListener();
        if(dd != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dd.onProcessStart(activity, count);
                }
            });
        }
    }
    /*public*/ void onImageProcessEnd(final Activity activity, final Runnable next) {
        if(activity == null){
            return;
        }
        final ImagePickDelegate.OnImageProcessListener dd = ImagePickDelegateImpl.getDefault().getOnImageProcessListener();
        if(dd != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!dd.onProcessEnd(next)){
                        next.run();
                    }
                }
            });
        }else {
            activity.runOnUiThread(next);
        }
    }
    /*public*/ void onImageProcessUpdate(final Activity activity, final int update, final int total) {
        if(activity == null){
            return;
        }
        final ImagePickDelegate.OnImageProcessListener dd = ImagePickDelegateImpl.getDefault().getOnImageProcessListener();
        if(dd != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dd.onProcessUpdate(activity, update, total);
                }
            });
        }
    }

    /*public*/ boolean onImageProcessException(final Activity activity, final int order,
                                               final int size, MediaResourceItem item, final Exception e) {
        final ImagePickDelegate.OnImageProcessListener dd = ImagePickDelegateImpl.getDefault().getOnImageProcessListener();
        return dd != null && dd.onProcessException(activity, order, size, item, e);
    }

    /*public*/ boolean handleException(FragmentActivity activity, int code, Exception e) {
        return mHandler != null && mHandler.handleException(activity, code, e);
    }
}
