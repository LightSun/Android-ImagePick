package com.heaven7.android.imagepick0;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class CustomSurfaceView extends SurfaceView {

    private OnTouchEventListener mTouchEvent;

    public CustomSurfaceView(Context context) {
        super(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            mTouchEvent.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public void setOnTouchEventListener(OnTouchEventListener onTouchEvent) {
        mTouchEvent = onTouchEvent;
    }

    public interface OnTouchEventListener {
        void onTouchEvent(MotionEvent event);
    }
}

