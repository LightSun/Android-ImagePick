package com.heaven7.android.imagepick;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

/**
 * the optimise scroll listener which help load image smoothly.
 * Created by heaven7 on 2018/1/3 0003.
 */
public class OptimiseScrollListenerImpl extends RecyclerView.OnScrollListener{

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        switch (newState){
            case RecyclerView.SCROLL_STATE_SETTLING:
            case RecyclerView.SCROLL_STATE_DRAGGING:
                Glide.with(recyclerView.getContext()).pauseRequests();
                break;

            case RecyclerView.SCROLL_STATE_IDLE:
                Glide.with(recyclerView.getContext()).resumeRequests();
                break;
        }
    }

}