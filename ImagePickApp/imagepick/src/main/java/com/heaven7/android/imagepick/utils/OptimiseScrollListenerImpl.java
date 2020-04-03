package com.heaven7.android.imagepick.utils;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.android.imagepick.ImagePickDelegateImpl;

/**
 * the optimise scroll listener which help load image smoothly.
 * Created by heaven7 on 2018/1/3 0003.
 */
public class OptimiseScrollListenerImpl extends RecyclerView.OnScrollListener{

    private final Activity activity;

    public OptimiseScrollListenerImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        switch (newState){
            case RecyclerView.SCROLL_STATE_SETTLING:
            case RecyclerView.SCROLL_STATE_DRAGGING:
                //Glide.with(recyclerView.getContext()).pauseRequests();
                ImagePickDelegateImpl.getDefault().getImageLoadDelegate().pauseRequests(activity);
                break;

            case RecyclerView.SCROLL_STATE_IDLE:
                //Glide.with(recyclerView.getContext()).resumeRequests();
                ImagePickDelegateImpl.getDefault().getImageLoadDelegate().resumeRequests(activity);
                break;
        }
    }

}