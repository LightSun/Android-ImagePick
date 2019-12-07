package common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/22.
 */

public class StringUtil {
    public static String valueOf(String data) {
        if (null == data) {
            return "";
        }
        return String.valueOf(data);
    }

    public static String buildUrl(String host, String path) {
        if (path.startsWith("http")) return path;
        StringBuilder url = new StringBuilder();
        url.append(host);
        if (host.endsWith("/")) {
            if (path.startsWith("/")) {
                url.append(path.substring(1));
            } else {
                url.append(path);
            }
        } else {
            if (path.startsWith("/")) {
                url.append(path);
            } else {
                url.append("/").append(path);
            }
        }
        return url.toString();
    }

    public static String buildAddress(String province, String city, String county) {
        if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city)) return "";
        StringBuilder result = new StringBuilder();
        result.append(province).append(".").append(city);
        if (!TextUtils.isEmpty(county)) {
            result.append(".").append(county);
        }
        return result.toString();
    }

    /**
     * 密码按如下规则进行计分，并根据不同的得分为密码进行安全等级划分。
     * 一、密码长度:
     * 5 分: 小于等于4 个字符
     * 10 分: 5 到7 字符
     * 25 分: 大于等于8 个字符
     * 二、字母:
     * 0 分: 没有字母
     * 10 分: 全都是小（大）写字母
     * 20 分: 大小写混合字母
     * 三、数字:
     * 0 分: 没有数字
     * 10 分: 1 个数字
     * 20 分: 大于1 个数字
     * 四、符号:
     * 0 分: 没有符号
     * 10 分: 1 个符号
     * 25 分: 大于1 个符号
     * 五、奖励:
     * 2 分: 字母和数字
     * 3 分: 字母、数字和符号
     * 5 分: 大小写字母、数字和符号
     * 最后的评分标准:
     * >= 90: 非常安全
     * >= 80: 安全（Secure）
     * >= 70: 非常强
     * >= 60: 强（Strong）
     * >= 50: 一般（Average）
     * >= 25: 弱（Weak）
     * >= 0:  非常弱
     *
     * @param pwd
     * @return level (0--100)
     */
    public static int getPwdSecurityLevel(String pwd) {
        int len = pwd.length();
        int num = 0;
        int lowerCase = 0;
        int upperCase = 0;
        int ch = 0;
        int score = 0;

        for (int i = 0; i < pwd.length(); i++) {
            char c = pwd.charAt(i);
            if (c >= '0' && c <= '9') {
                num++;
            } else if (c >= 'A' && c <= 'Z') {
                upperCase++;
            } else if (c >= 'a' && c <= 'z') {
                lowerCase++;
            } else if (c >= 0x21 && c <= 0x2F || c >= 0x3A && c <= 0x40
                    || c >= 0X5B && c <= 0x60 || c >= 0x7B && c <= 0x7E) {
                ch++;
            }
        }

        // 一、密码长度
        if (len <= 4) {
            score += 5;
        } else if (len <= 7) {
            score += 10;
        } else {
            score += 25;
        }

        // 二、字母
        if (lowerCase > 0) {
            score += 10;
        }

        if (upperCase > 0) {
            score += 10;
        }

        // 三、数字
        if (num == 1) {
            score += 10;
        } else if (num > 1) {
            score += 20;
        }

        // 四、符号
        if (ch == 1) {
            score += 10;
        } else if (ch > 1) {
            score += 25;
        }

        if (num > 0 && (upperCase > 0 || lowerCase > 0)) {
            score += 2;
            if (ch > 0) {
                score += 1;

                if (upperCase > 0 && lowerCase > 0) {
                    score += 2;
                }
            }
        }
        return score;
//        if (score >= 90) {
//            return "VERY_SECURE";
//        } else if (score >= 80) {
//            return "SECURE";
//        } else if (score >= 70) {
//            return "VERY_STRONG";
//        } else if (score > 60) {
//            return "STRONG";
//        } else if (score >= 50) {
//            return "AVERAGE";
//        } else if (score >= 25) {
//            return "WEAK";
//        } else {
//            return "VERY_WEAK";
//        }
    }

    /**
     * 判断必须满足规则：密码长度6~12位。且必须包含大写字母，小写字母和数字
     *
     * @param password
     * @return
     */
    public static boolean isPwdRule(String password) {
        int length = password.length();
        if (length < 6 || length > 12) {
            return false;
        }
        if (password.matches("\\w+")) {
            Pattern p1 = Pattern.compile("[a-z]+");
            Pattern p2 = Pattern.compile("[A-Z]+");
            Pattern p3 = Pattern.compile("[0-9]+");
            Matcher m = p1.matcher(password);
            if (!m.find())
                return false;
            else {
                m.reset().usePattern(p2);
                if (!m.find())
                    return false;
                else {
                    m.reset().usePattern(p3);
                    if (!m.find())
                        return false;
                    else {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
    }
}
