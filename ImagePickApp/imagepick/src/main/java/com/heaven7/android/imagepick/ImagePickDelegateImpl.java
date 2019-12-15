package com.heaven7.android.imagepick;

import android.app.Activity;
import android.support.annotation.RestrictTo;
import android.support.v4.app.FragmentActivity;

import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.CameraParameter;
import com.heaven7.android.imagepick.pub.ExceptionHandler;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.ImagePickDelegate;
import com.heaven7.android.imagepick.pub.ImageSelectParameter;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.android.util2.LauncherIntent;

import java.util.ArrayList;
import java.util.List;

import static com.heaven7.android.imagepick.pub.PickConstants.REQ_BROWSE_BIG_IMAGE;
import static com.heaven7.android.imagepick.pub.PickConstants.REQ_CAMERA;
import static com.heaven7.android.imagepick.pub.PickConstants.REQ_GALLERY;

/**
 * the image pick manager. used for internal
 * @author heaven7
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ImagePickDelegateImpl implements ImagePickDelegate {

    private static ImagePickDelegateImpl sInstance;
    private OnImageProcessListener mImageListener;
    private ExceptionHandler mHandler;
    private VideoManageDelegate mVideoManager;

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
        return mItems;
    }
    public void setImageItems(List<? extends IImageItem> mItems) {
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
        if(param == null || allItems == null){
            throw new IllegalArgumentException();
        }
        setImageItems(allItems);
        new LauncherIntent.Builder()
                .setClass(context, SeeBigImageActivity.class)
                .putExtra(PickConstants.KEY_PARAMS, param)
                .putExtra(PickConstants.KEY_SINGLE_ITEM, single)
                .build()
                .startActivityForResult(REQ_BROWSE_BIG_IMAGE);
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
                    dd.onProcessEnd(next);
                }
            });
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
