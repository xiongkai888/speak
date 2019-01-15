package com.lanmei.speak.utils;

import com.xson.common.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/8.
 */

public class FormatTime {

    private long time;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public FormatTime() {
        this.time = System.currentTimeMillis();
    }

    /**
     * @param time 毫秒
     */
    public FormatTime(long time) {
        this.time = time * 1000;
        calendar.setTimeInMillis(this.time);
    }

    /**
     * @param timeStr 毫秒 String类型
     */
    public FormatTime(String timeStr) {
        if (StringUtils.isEmpty(timeStr)) {
            timeStr = "0";
        }
        this.time = Long.parseLong(timeStr) * 1000;
        calendar.setTimeInMillis(this.time);
    }

    /**
     * @param time 毫秒
     */
    public void setTime(long time) {
        this.time = time * 1000;
        calendar.setTimeInMillis(this.time);

    }

    /**
     * @param timeStr 毫秒  String 类型
     */
    public void setTime(String timeStr) {
        if (StringUtils.isEmpty(timeStr)) {
            timeStr = "0";
        }
        this.time = Long.parseLong(timeStr) * 1000;
        calendar.setTimeInMillis(this.time);

    }

    /**
     * 时间戳格式为“yyyy-MM-dd  HH:mm:ss     ”
     */
    public String formatterTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 时间戳格式为“yyyy-MM-dd”
     */
    public String formatterTimeToDay() {
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 时间戳格式为“yyyy-MM-dd  HH:mm”
     */
    public String formatterTimeNoSeconds() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 将时间转换为时间戳
     */
    public long dateToStampLong(String s) throws ParseException {
        Date date = format.parse(s);
        long ts = date.getTime() / 1000;
        return ts;
    }



    //获取昨天的日期
    public String getYesterdayDate() {
        calendar.setTimeInMillis(this.time);
        calendar.add(Calendar.DATE, -1);
        return format.format(calendar.getTime());
    }

    //获取这个月的第一天
    public String getThisMonthDateS() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH,1);//
        return format.format(calendar.getTime());
    }
    //获取这个月的最后一天
    public String getThisMonthDateE() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH,0);//
        return format.format(calendar.getTime());
    }

    //获取上个月的第一天
    public String getLastMonthDateS() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH,1);//
        return format.format(calendar.getTime());
    }
    //获取上个月的最后一天
    public String getLastMonthDateE() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH,0);//
        return format.format(calendar.getTime());
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


}
