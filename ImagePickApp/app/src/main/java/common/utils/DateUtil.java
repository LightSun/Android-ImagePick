package common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/24.
 */

@SuppressLint("SimpleDateFormat")
public class DateUtil {


    public static String getTime(@TYPE String type, long time) {
        return getTime(type, time, false);
    }

    public static String splitDate(String date) {
        if (TextUtils.isEmpty(date)) return date;
        String[] split = date.split(" ");
        if (split.length > 1) return split[0];
        return date;
    }


    /**
     * 30分钟以内的显示【刚刚】；
     * 30分钟至1个小时的显示【30分钟】；
     * 1个小时到2个小时的为【1小时】；
     * 2个小时以上且是今天的，展示为【今天】；
     * 其余的展示发货日期。
     *
     * @param type
     * @param time
     * @param isCustom 自定义 返回
     * @return
     */
    public static String getTime(@TYPE String type, long time, boolean isCustom) {
        SimpleDateFormat format;
        long differTime = (System.currentTimeMillis() - time) / (60 * 1000);
        long currentTime = System.currentTimeMillis();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(currentTime);
        long productTime = Long.valueOf(time);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(productTime);
        if (isCustom) {
            if (differTime < 30 && differTime >= 0) {
                return "刚刚";
            } else if (differTime >= 30 && differTime < 60) {
                return "30分钟";
            } else if (differTime >= 60 && differTime < 60 * 2) {
                return "1小时";
            } else if (differTime < 24 * 60 && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
                return "今天";
            } else {
                format = new SimpleDateFormat(type);
                return format.format(new Date(time));
            }
        } else {
            format = new SimpleDateFormat(type);
            return format.format(new Date(time));
        }
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(@TYPE String type, String s) throws ParseException {
        if (TextUtils.isEmpty(s)) return new Date().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }

    public static final String TYPE0 = "yyyy-MM-dd HH:mm:ss";
    public static final String TYPE1 = "yyyy-MM-dd";
    public static final String TYPE2 = "yyyy年MM月dd日 HH:mm:ss ";
    public static final String TYPE3 = "yyyy/MM/dd";

    @StringDef({
            TYPE0,
            TYPE1,
            TYPE2,
            TYPE3
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface TYPE {
    }
}
