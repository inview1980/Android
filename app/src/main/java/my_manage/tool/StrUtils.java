package my_manage.tool;

import java.util.Random;

public final class StrUtils {
    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0 || string.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isAnyBlank(String... strs) {
        if (strs == null) return true;
        for (String s : strs) {
            if (s == null || s.length() == 0 || s.trim().length() == 0) return true;
        }
        return false;
    }

    public static boolean isAllNotBlank(String... strings) {
        if(strings==null) return false;
        for (String s : strings) {
            if (s == null || s.length() == 0 || s.trim().length() == 0) return false;
        }
        return true;
    }

    public static boolean isNotBlank(String s) {
        if (s == null || s.length() == 0 || s.trim().length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 生成指定长度的字符串
     *
     * @param length 用户要求产生字符串的长度
     */
    public static String getRandomString(int length) {
        String       str    = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random       random = new Random();
        StringBuffer sb     = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
