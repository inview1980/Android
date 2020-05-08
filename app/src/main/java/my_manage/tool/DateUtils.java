package my_manage.tool;

import android.util.Pair;

import java.util.Calendar;

import my_manage.tool.menuEnum.CastUtils;

public final class DateUtils {
    public static String date2String(Calendar date) {
        if (date == null) return "";
        return date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar string2Date(String text) {
        if (StrUtils.isBlank(text)) return null;
        String[] ss = text.split("-");
        if (ss.length >= 3) {
            Pair<Boolean, Integer> year  = CastUtils.parseInt(ss[0]);
            Pair<Boolean, Integer> month = CastUtils.parseInt(ss[1]);
            Pair<Boolean, Integer> day   = CastUtils.parseInt(ss[2]);
            if (year.first && month.first && day.first) {
                Calendar c = Calendar.getInstance();
                c.set(year.second, month.second - 1, day.second);
                return c;
            }
        }
        return null;
    }
}
