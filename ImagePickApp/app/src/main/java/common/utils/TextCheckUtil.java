package common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Administrator on 2017/5/12.
 */

public class TextCheckUtil {

    /**
     * 是否是准确的车牌号格式
     */
    public static boolean isCarNum(String str) throws PatternSyntaxException {
        String regExp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 是否是准确的车牌号格式
     */
    public static boolean isCarAndNewCarNum(String str) throws PatternSyntaxException {
//        String regExp2 = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
        String regExp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z]{1,2}[A-Z0-9]{4}[A-Z0-9挂学警港澳]$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    /**
     * 是否是准确的车牌号格式
     */
    public static boolean isContainsSymbol(String str) throws PatternSyntaxException {
        String regExp = "^[\\u4E00-\\u9FA5A-Za-z0-9]{1,20}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 是否是汉字
     */
    public static boolean isChinese(String str) throws PatternSyntaxException {
        String regExp = "^[\\u4E00-\\u9FA5]{2,20}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 是否是汉字
     */
    public static boolean isBankCard(String str) throws PatternSyntaxException {
        String regExp = "^\\d{16,19}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 身份证号验证
     */
    public static boolean isIDcard(String str) throws PatternSyntaxException {
        String regExp = "^[0-9Xx]{15,18}$";
//        String regExp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * Emoji表情校验
     *
     * @param string
     * @return
     */
    public static boolean isEmoji(String string) {
        //过滤Emoji表情
//        Pattern p = Pattern.compile("[^\\u0000-\\uFFFF]");
        //过滤Emoji表情和颜文字
//        Pattern p = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]|[\\ud83e\\udd00-\\ud83e\\uddff]|[\\u2300-\\u23ff]|[\\u2500-\\u25ff]|[\\u2100-\\u21ff]|[\\u0000-\\u00ff]|[\\u2b00-\\u2bff]|[\\u2d06]|[\\u3030]");
        Pattern p = Pattern.compile("^[\\u0020-\\u007e\\u4e00-\\u9fa5·！￥……*（）——、【】；’、。，《》？：“”±]{1,20}$");
        Matcher m = p.matcher(string);
        return m.find();
    }

    /**
     * 意见反馈，表情校验
     *
     * @param string
     * @return
     */
    public static boolean hasEmojiFeedback(String string) {
        //过滤Emoji表情
//        Pattern p = Pattern.compile("[^\\u0000-\\uFFFF]");
        //过滤Emoji表情和颜文字
//        Pattern p = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]|[\\ud83e\\udd00-\\ud83e\\uddff]|[\\u2300-\\u23ff]|[\\u2500-\\u25ff]|[\\u2100-\\u21ff]|[\\u0000-\\u00ff]|[\\u2b00-\\u2bff]|[\\u2d06]|[\\u3030]");
        Pattern p = Pattern.compile("^[\\u0020-\\u007e\\u4e00-\\u9fa5·！￥……*（）——、【】；’、。，《》？：“”±]{1,500}$");
        Matcher m = p.matcher(string);
        return m.find();
    }

}
