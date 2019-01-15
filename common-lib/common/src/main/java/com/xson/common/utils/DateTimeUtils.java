package com.xson.common.utils;

import android.text.format.DateFormat;
import android.text.format.Time;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Jecelyin Peng <jecelyin@gmail.com>
 */

public class DateTimeUtils {
    public static CharSequence timestampToString(long timestampMillis) {
        if (timestampMillis <= 0)
            return "--";
        Date now = new Date();
        long dayStart = getStartOfDay(now);

        if (timestampMillis >= dayStart) {
            return DateFormat.format("hh:mm", timestampMillis);
        } else if (isSameYear(now.getTime(), timestampMillis)) {
            return DateFormat.format("MM-dd", timestampMillis);
        } else {
            return DateFormat.format("yyyy-MM-dd", timestampMillis);
        }
    }

    public static boolean isSameYear(long curr, long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;

        time.set(curr);
        return (thenYear == time.year);
    }

    public static long getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis();
    }
}
