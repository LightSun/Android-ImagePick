package com.heaven7.android.video.load;

import android.content.Context;
import android.net.Uri;

/**
 * the download executor
 * @author heaven7
 */
public interface DownloadExecutor {
    /**
     * download the url sync and return the file uri. note the android 6.0+. uri access.
     *
     * @param context the context to help you get uri
     * @param url the url
     * @return the file uri for access. or null if failed.
     * @throws Exception if occurs
     */
    Uri download(Context context, String url) throws Exception;

    /**
     * cancel download
     */
    void cancel();
}
