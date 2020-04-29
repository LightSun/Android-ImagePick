package com.heaven7.android.imagepick.page;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.heaven7.adapter.page.PageViewProvider;
import com.heaven7.android.imagepick.internal.GestureImageUtils;
import com.heaven7.android.imagepick.internal.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.pub.module.IImageItem;

public class MediaPageProviderManager extends PageProviderManager<IImageItem> {

    /**
     * the callback of bind image item.
     */
    public interface Callback{
        /**
         * called on bind image item to view
         * @param iv the image view
         * @param pos the position
         * @param realPos the real Position
         * @param data the data
         * @return true if bind success. false to use default bind
         */
        boolean bindImageItem(ImageView iv, int pos, int realPos, IImageItem data);
        /**
         * called on click image view from page
         * @param iv the image view
         * @param pos the position
         * @param realPos the real Position
         * @param data the data
         */
        void onClickPageImageView(ImageView iv, int pos, int realPos, IImageItem data);
    }

    private final Callback mCallback;
    private final ImageViewProvider mImageViewProvider;
    private boolean mSupportGesture;

    public MediaPageProviderManager(FragmentActivity context, Callback callback) {
        super(context);
        this.mCallback = callback;
        this.mImageViewProvider = new ImageViewProvider(context);
    }

    public ImageViewProvider getImageViewProvider() {
        return mImageViewProvider;
    }

    public void setSupportGesture(boolean mSupportGesture) {
        this.mSupportGesture = mSupportGesture;
    }

    private class ImageViewProvider extends PageViewProvider<IImageItem> {

        public ImageViewProvider(Context context) {
            super(context);
        }
        @Override
        public View createItemView(ViewGroup viewGroup, int pos, int realPos, IImageItem t) {
            if(t.isVideo()){
                return null;
            }
            //if enable gesture image.
            if(mSupportGesture){
                ImageView imageView = GestureImageUtils.createGestureImageView(getContext());
                if(imageView != null){
                    return imageView;
                }
            }
            // if is image and not use gesture.
            ImageView iv = new ImageView(getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return iv;
        }
        @Override
        public void onBindItemView(View view, final int pos, final int realPos, final IImageItem t) {
            if(!t.isVideo()){
                ImageView iv = (ImageView) view;
                if(!mCallback.bindImageItem((ImageView) view, pos, realPos, t)){
                    ImagePickDelegateImpl.getDefault().getImageLoadDelegate()
                            .loadImage((FragmentActivity) getContext(),iv, t, null);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallback.onClickPageImageView((ImageView) v, pos, realPos, t);
                        }
                    });
                }
            }
        }
    }
}
