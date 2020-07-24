package my_manage.tool;

import android.util.Pair;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import lombok.val;
import my_manage.tool.menuEnum.CastUtils;

public final class DateUtils {
    private static String                  JoinerString = " ～ ";
    public static  ThreadLocal<DateFormat> threadLocal  = new ThreadLocal<DateFormat>() {
        @Nullable
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-M-d");
        }
    };

    public static String date2String(Calendar date, int addMonth) {
        if (date == null) return "";
        String   first = threadLocal.get().format(date.getTime());
        Calendar d2    = Calendar.getInstance();
        d2.setTimeInMillis(date.getTimeInMillis());
        d2.add(Calendar.MONTH, addMonth);
        d2.add(Calendar.DATE, -1);
        return first + JoinerString + threadLocal.get().format(d2.getTime());
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
        return threadLocal.get().format(date.getTime());
    }

    public static Calendar string2Date(String text) {
        if (StrUtils.isBlank(text)) return null;


        String[] sNull = text.split(JoinerString);
        String   ss;
        if (sNull.length < 2)
            ss = text;
        else
            ss = sNull[0];
        try {
            Calendar result = Calendar.getInstance();
            result.setTime(threadLocal.get().parse(ss));
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
