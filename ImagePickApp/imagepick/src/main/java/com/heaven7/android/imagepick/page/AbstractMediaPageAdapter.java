package com.heaven7.android.imagepick.page;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.heaven7.android.imagepick.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.memory.util.Cacher;

import java.util.List;

import internal.GestureImageUtils;

/**
 * the media page adapter .which support image and video
 * @author heaven7
 * @since 1.0.5
 */
public abstract class AbstractMediaPageAdapter extends AbstractPagerAdapter<IImageItem, View> {

    private final Cacher<View, ItemViewContext> mVideoViewCaher;
    private boolean supportGestureImage;

    public AbstractMediaPageAdapter(List<? extends IImageItem> mDatas) {
        super(false, mDatas);
        mVideoViewCaher = new Cacher<View, ItemViewContext>() {
            @Override
            public View create(ItemViewContext context) {
                return onCreateItemView(context);
            }
        };
    }
    public void setSupportGestureImage(boolean supportGestureImage) {
        this.supportGestureImage = supportGestureImage;
    }

    public void onPause(Context context, int pos, View view){
        if(view == null){
            return;
        }
        IImageItem item = getItemAt(pos);
        if(item.isVideo()){
            VideoManageDelegate vm = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(vm != null){
                vm.pauseVideo(context, view);
            }
        }
    }
    public void onResume(Context context, int pos, View view){
        if(view == null){
            return;
        }
        IImageItem item = getItemAt(pos);
        if(item.isVideo()){
            VideoManageDelegate vm = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(vm != null){
                vm.resumeVideo(context, view);
            }
        }
    }
    public void onDestroy(Context context, int pos, View view){
        if(view == null){
            return;
        }
        IImageItem item = getItemAt(pos);
        if(item.isVideo()){
            VideoManageDelegate vm = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(vm != null){
                vm.destroyVideo(context, view);
            }
        }
    }

    public void startPlay(Context context, int pos, View view){
        if(view == null){
            return;
        }
        IImageItem item = getItemAt(pos);
        if(item.isVideo()){
            VideoManageDelegate vm = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(vm != null){
                vm.startPlay(context, view, item);
            }
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        MediaLog.instantiateItem(getPositionActually(position));
        return super.instantiateItem(container, position);
    }

    @Override
    public final void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        MediaLog.destroyItem(position);
        int index = getPositionActually(position);
        View view = (View) object;
        VideoManageDelegate videoM = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(videoM != null && videoM.isVideoView(view, getItemAt(index))){
            videoM.destroyVideo(container.getContext(), view);
        }
        super.destroyItem(container, position, object);
    }

    @Override
    protected View onCreateItemView(ItemViewContext context) {
        MediaLog.createItem(context.position);
        IImageItem data = (IImageItem) context.data;
        if(data.isVideo()){
            VideoManageDelegate videoM = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(videoM == null){
                throw new IllegalStateException("for video item. you must assign the VideoManageDelegate!");
            }
            return videoM.createVideoView(context.context, data);
        }
        //if enable gesture image.
        if(supportGestureImage){
            ImageView imageView = GestureImageUtils.createGestureImageView(context.context);
            if(imageView != null){
                return imageView;
            }
        }
        // if is image and not use gesture.
        ImageView iv = new ImageView(context.context);
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return iv;
    }
    @Override
    protected final void onBindItem(View iv, int index, IImageItem data) {
        MediaLog.onBindItem(index);
        if(data.isVideo()){
            VideoManageDelegate videoM = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(videoM == null){
                throw new IllegalStateException("for video item. you must assign the VideoManageDelegate!");
            }
            videoM.setMediaData(iv.getContext(), iv, data);
        }else {
            onBindImageItem((ImageView) iv, index, data);
        }
    }

    @Override
    protected View obtainItemView(ItemViewContext p) {
        MediaLog.obtainItem(p.position);
        IImageItem data = (IImageItem) p.data;
        if(data.isVideo()){
            return mVideoViewCaher.obtain(p);
        }
        return super.obtainItemView(p);
    }

    @Override
    protected boolean shouldRecycle(int position, View view) {
        MediaLog.recycleItem(position);
        VideoManageDelegate videoM = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(videoM != null && videoM.isVideoView(view, getItemAt(position))){
            mVideoViewCaher.recycle(view);
            return false;
        }
        return true;
    }

    /**
     * called on bind image item
     * @param iv the image view
     * @param index the index of data
     * @param data the data
     */
    protected abstract void onBindImageItem(ImageView iv, int index, IImageItem data);

}
