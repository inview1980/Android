package my_manage.tool;

import android.util.Pair;

import java.util.Calendar;

import my_manage.tool.menuEnum.CastUtils;

public final class DateUtils {
    private static String JoinerString = " ～ ";

    public static String date2String(Calendar date, int addMonth) {
        if (date == null) return "";
        String   first = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
        Calendar d2    = Calendar.getInstance();
        d2.setTimeInMillis(date.getTimeInMillis());
        d2.add(Calendar.MONTH, addMonth);
        d2.add(Calendar.DATE, -1);
        return first + JoinerString + d2.get(Calendar.YEAR) + "-" + (d2.get(Calendar.MONTH) + 1) + "-" + d2.get(Calendar.DAY_OF_MONTH);
    }

    public static String string2DateString(int year, int month, int day, int months) {
        Calendar d1 = Calendar.getInstance();
        d1.set(year, month, day);
        if (months == 0)
            return date2String(d1);
        return date2String(d1, months);
    }

    public static String date2String(Calendar date) {
        if (date == null) return "";
        return date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar string2Date(String text) {
        if (StrUtils.isBlank(text)) return null;
        String[] sNull = text.split(" ");
        String[] ss;
        if (sNull.length < 2)
            ss = text.split("-");
        else
            ss = sNull[0].split("-");

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
