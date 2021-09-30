package org.ponking.gih.util;

/**
 * @Author: huang
 * @Date: 2021/10/1/0:57
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotBank(String str) {
        return !isBank(str);
    }

    public static boolean isBank(String str) {
        return "".equals(str) || str.length() == 0;
    }
}
