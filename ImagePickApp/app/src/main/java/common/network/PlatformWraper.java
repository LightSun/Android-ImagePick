package common.network;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import common.utils.MapUtil;

/**
 * Created by Administrator on 2017/8/18.
 */

public class PlatformWraper {
    private HashMap<String, Object> mPlatforms = MapUtil.empty();
    private HashMap<String, PlatformCode> mPlatformsCode = new HashMap<>();
    private String mDefaultPlatform;
    private PlatformCode mDefaultPlatformCode;

    public PlatformWraper appendPlatform(@Platforms String platform, @NonNull String url, boolean isDefault) {
        return appendPlatform(platform, url, null, isDefault);
    }

    public PlatformWraper appendPlatform(@Platforms String platform, @NonNull String url, PlatformCode platformCode, boolean isDefault) {
        if (isDefault) {
            mDefaultPlatform = url;
            mDefaultPlatformCode = platformCode;
        }
        mPlatformsCode.put(platform, platformCode);
        mPlatforms.put(platform, url);
        return this;
    }

//    public PlatformWraper appendPlatformCode(@Platforms String platform, @NonNull PlatformCode platformCode) {
//        mPlatformsCode.put(platform, platformCode);
//        return this;
//    }

    public String getDefaultPlatform() {
        return mDefaultPlatform;
    }

    public PlatformCode getDefaultPlatformCode() {
        return mDefaultPlatformCode;
    }

    public String getPlatform(@Platforms String platform) {
        return mPlatforms.get(platform).toString();
    }

    public PlatformCode getPlatformCode(@Platforms String platform) {
        return mPlatformsCode.get(platform);
    }

    final public static String PLATFORM_TAG_H5 = "platform_tag_h5";
    final public static String PLATFORM_TAG_API = "platform_tag_api";

    @StringDef({
            PLATFORM_TAG_H5, PLATFORM_TAG_API
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlatformTag {
    }


    final public static String API_PLATFORM_VALUE_TRADE = "trade";
    final public static String API_PLATFORM_VALUE_LOGISTICS = "logistics";
    final public static String API_PLATFORM_VALUE_PURCHASE = "purchase";
    final public static String API_PLATFORM_VALUE_SSO = "sso";

    @StringDef({
            API_PLATFORM_VALUE_PURCHASE, API_PLATFORM_VALUE_TRADE, API_PLATFORM_VALUE_LOGISTICS, API_PLATFORM_VALUE_SSO
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Platforms {
    }

    public static class PlatformCode {

        private int success;
        private int noToken;
        private int errorToken;

        public PlatformCode(int success, int noAccess, int errorToken) {
            this.success = success;
            this.noToken = noAccess;
            this.errorToken = errorToken;
        }

        public int getSuccess() {
            return success;
        }

        public int getNoToken() {
            return noToken;
        }

        public int getErrorToken() {
            return errorToken;
        }
    }

    public static class PlatformInner {

        @Platforms
        String platform;
        @NonNull
        String url;
        @PlatformTag
        String tag;
        boolean isDefault;

        public PlatformInner(String platform, @NonNull String url, String tag, boolean isDefault) {
            this.platform = platform;
            this.url = url;
            this.tag = tag;
            this.isDefault = isDefault;
        }
    }
}
