package com.heaven7.android.imagepick.page;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.heaven7.android.imagepick.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.internal.GestureImageUtils;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.VideoManageDelegate;
import com.heaven7.memory.util.Cacher;

import java.util.List;

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

    public void onPause(int pos, View view){
        if(view == null){
            return;
        }
        int realPos = getPositionActually(pos);
        IImageItem item = getItemAt(realPos);
        if(item.isVideo()){
            VideoManageDelegate vm = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(vm != null){
                vm.pauseVideo(view, realPos, item);
            }
        }
    }
    public void onResume(int pos, View view){
        if(view == null){
            return;
        }
        int position = getPositionActually(pos);
        IImageItem item = getItemAt(position);
        if(item.isVideo()){
            VideoManageDelegate vm = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(vm != null){
                vm.resumeVideo(view, position, item);
            }
        }
    }
    public void onDestroy(int pos, View view){
        if(view == null){
            return;
        }
        int position = getPositionActually(pos);
        IImageItem item = getItemAt(position);
        if(item.isVideo()){
            VideoManageDelegate vm = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
            if(vm != null){
                vm.releaseVideo(view, position, item);
            }
        }
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        int index = getPositionActually(position);
        IImageItem data = getItemAt(index);
        View view = (View)object;
        VideoManageDelegate videoM = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(videoM != null && data.isVideo()){
            videoM.setPrimaryItem(view, index, data);
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
        IImageItem item = getItemAt(index);
        VideoManageDelegate videoM = ImagePickDelegateImpl.getDefault().getVideoManageDelegate();
        if(videoM != null && item.isVideo()){
            videoM.onDestroyItem(view, position, item);
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
            return videoM.createVideoView(context.getContext(), context.parent, data);
        }
        //if enable gesture image.
        if(supportGestureImage){
            ImageView imageView = GestureImageUtils.createGestureImageView(context.getContext());
            if(imageView != null){
                return imageView;
            }
        }
        // if is image and not use gesture.
        ImageView iv = new ImageView(context.getContext());
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
            videoM.onBindItem(iv, index, data);
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
        if(videoM != null && getItemAt(position).isVideo()){
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
