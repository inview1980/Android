package my_manage.tool.menuEnum;

import android.util.Pair;

import my_manage.tool.StrUtils;

public final class CastUtils {
    public static Pair<Boolean, Integer> parseInt(String str) {
        try {
            if (StrUtils.isBlank(str)) return new Pair<>(false, 0);
            int n = Integer.parseInt(str);
            return new Pair<>(true, n);
        } catch (Exception e) {
            return new Pair<>(false, 0);
        }
    }

    public static Pair<Boolean, Double> parseDouble(String str) {
        try{
            if (StrUtils.isNotBlank(str)) {
                double n = Double.parseDouble(str);
                return new Pair<>(true,n);
            }
            return new Pair<Boolean, Double>(false,0D);
        } catch (NumberFormatException e) {
            return new Pair<Boolean, Double>(false,0D);
        }
    }
}
