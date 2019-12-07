package common.network;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017/8/16.
 */

public class NetworkConfig {

    //    private static String mH5Url;
    private static Context mContext;
    private static boolean mCacheable;
    private static String mVersionName;
    private static String mChannel;
    private static String mToken;
//    @PlatformWraper.Platforms
//    private static String mMainPlatform;

    public static String getApiPlatform(@PlatformWraper.Platforms String platform) {
        if (null == mPlatforms)
            throw new NullPointerException("initNetworkConfig method must be called");
        return mPlatforms.getPlatform(platform);
    }

    private static PlatformWraper mPlatforms;
    private static PlatformWraper mH5Platforms;

//    public static void initNetworkConfig(Context appContext, PlatformWraper platforms, @PlatformWraper.Platforms String mainPlatform, String h5Url, String versionName, String channel, boolean cacheable) {
//        mContext = appContext;
//        mPlatforms = platforms;
//        mMainPlatform = mainPlatform;
//        mH5Url = h5Url;
//        mVersionName = versionName;
//        mChannel = channel;
//        mCacheable = cacheable;
//    }

    public static void initNetworkConfig(Context appContext, PlatformWraper platforms, PlatformWraper h5Platforms, String versionName, String channel, boolean cacheable) {
        mContext = appContext;
        mPlatforms = platforms;
        mH5Platforms = h5Platforms;
        mVersionName = versionName;
        mChannel = channel;
        mCacheable = cacheable;
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getDefaultUrl() {
        String defaultPlatform = mPlatforms.getDefaultPlatform();
        if (TextUtils.isEmpty(defaultPlatform)) {
            throw new NullPointerException("mMainPlatform must be set");
        }
        return defaultPlatform;
    }

    public static void setToken(String token) {
        mToken = token;
    }

    public static String getVersionName() {
        return mVersionName;
    }

    public static String getChannel() {
        return mChannel;
    }

    public static String getToken() {
        return mToken;
    }

    public static String getUrl(@PlatformWraper.Platforms String platform) {
        if (null == mPlatforms) throw new NullPointerException("mPlatforms must be seted");
        String platformUrl = mPlatforms.getPlatform(platform);
        if (TextUtils.isEmpty(platformUrl))
            throw new NullPointerException(platform + " not found");
        return platformUrl;
    }

    public static PlatformWraper.PlatformCode getPlatformCode(@PlatformWraper.Platforms String platform) {
        if (null == mPlatforms) throw new NullPointerException("mPlatforms must be seted");
        PlatformWraper.PlatformCode platformCode = mPlatforms.getPlatformCode(platform);
        if (null == platformCode)
            throw new NullPointerException(platformCode + "not found");
        return platformCode;
    }

    public static PlatformWraper.PlatformCode getDefaultPlatformCode() {
        return mPlatforms.getDefaultPlatformCode();
    }


    public static String getH5DefaultUrl() {
        String defaultPlatform = mH5Platforms.getDefaultPlatform();
        if (TextUtils.isEmpty(defaultPlatform)) {
            throw new NullPointerException("mH5Url must be set");
        }
        return defaultPlatform;
    }

    public static String getH5Url(@PlatformWraper.Platforms String platform) {
        return mH5Platforms.getPlatform(platform);
    }

    public static boolean useCache() {
        return mCacheable;
    }
}
