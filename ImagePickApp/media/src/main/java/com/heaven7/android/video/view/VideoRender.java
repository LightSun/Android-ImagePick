package com.heaven7.android.video.view;

/**
 * the video render help improve the performance of play video.
 * @since 2.0.1
 */
public interface VideoRender {

    /**
     * set the render callback .
     *
     * @param cb the callback
     */
    void setRenderCallback(Callback cb);

    /**
     * the callback of render
     */
    interface Callback {

        /**
         * called on start render
         */
        void onStartRender();
    }
}
