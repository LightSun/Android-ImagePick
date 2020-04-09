package com.heaven7.android.video.impl;

import android.content.Context;
import android.view.ViewGroup;

import com.heaven7.android.video.MediaViewDelegate;
import com.heaven7.android.video.ScaleManager;
import com.heaven7.android.video.view.MediaPlayerView;
import com.heaven7.android.video.view.TextureVideoView;

public class DefaultMediaViewDelegate extends MediaViewDelegate<MediaPlayerView, TextureVideoView> {

    public DefaultMediaViewDelegate(ViewGroup parent) {
        super(parent);
    }
    @Override
    protected TextureVideoView createVideoView(Context context) {
        TextureVideoView videoView = new TextureVideoView(context);
        videoView.setScaleType(ScaleManager.ScaleType_FIT_CENTER);
        videoView.setDebug(true);
        return videoView;
    }
}
