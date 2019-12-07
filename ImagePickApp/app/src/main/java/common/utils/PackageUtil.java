package common.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import java.io.File;
import java.util.List;

public final class PackageUtil {

    /**
     * 获取sdk版本号
     *
     * @return
     * @throws
     * @Description: TODO
     */
    public static int getSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {

        }
        return version;
    }

    public static int getVersionCode(Context mContext) {
        int versionCode = -1;
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(getPackageName(mContext), 0).versionCode;
            return versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean hasIntent(Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return null != infos && !infos.isEmpty();
    }

    public static String getVersionName(Context mContext) {
        String versionName = "";
        try {
            versionName = mContext.getPackageManager().getPackageInfo(getPackageName(mContext), 0).versionName;
            return versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPackageName(Context mContext) {
        if (mContext != null) {
            return mContext.getPackageName();
        }
        return "com.bdfint.logistics_driver";
    }

    /**
     * get currnet activity's name
     *
     * @param context
     * @return
     */
    public static String getActivityName(Context context) {
        if (context == null) {
            return "";
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (checkPermissions(context, "android.permission.GET_TASKS")) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String activityName = cn.getShortClassName();
            activityName = activityName.substring(activityName.lastIndexOf(".") + 1, activityName.length());
            return activityName;
        } else {
            return "";
        }
    }

    /**
     * checkPermissions
     *
     * @param context
     * @param permission
     * @return true or false
     */
    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    private static String channelId;

    /**
     * @param
     * @return String
     * @description 获取APPLICATION metadata的数据
     * @date 2014-12-2
     * @Exception
     */
    public static String getApplicationMetaData(Context context, String key) {
        String data = null;
        String defaultkey = "zlgx";
        if (context == null) {
            return defaultkey;
        }
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(getPackageName(context), PackageManager.GET_META_DATA);
            if (null == appInfo || null == appInfo.metaData || null == appInfo.metaData.get(key))
                return defaultkey;
            data = appInfo.metaData.get(key).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            data = defaultkey;
        }
        return data;
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    public static boolean isInstallByread(String packageName) {
        return new File("/CallBack/CallBack/" + packageName).exists();
    }

}
