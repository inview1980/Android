package my_manage.tool;

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
}
