package com.example.minimalistcalendar.Util;

import android.content.Context;

import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.Bean.HolidayBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Util
 * @ClassName: DateUtil
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/3 23:54
 */
public class DateUtil {
    //将String转成date类型再返回回去
    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }
    //对日期排序
    public static void sortListByDate(List<DataBean> list1){
        Collections.sort(list1, new Comparator<DataBean>() {
            @Override
            public int compare(DataBean dataBean, DataBean t1) {
                Date date1 = DateUtil.stringToDate(dataBean.getDate());
                Date date2 = DateUtil.stringToDate(t1.getDate());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.after(date2)) {
                    return 1;
                }
                return -1;
            }
        });
    }
    public static void sortListByDateToHoliday(List<HolidayBean> list1){
        Collections.sort(list1, new Comparator<HolidayBean>() {
            @Override
            public int compare(HolidayBean holidayBean, HolidayBean t1) {
                Date date1 = DateUtil.stringToDate(holidayBean.getSolar().toString());
                Date date2 = DateUtil.stringToDate(t1.getSolar().toString());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.after(date2)) {
                    return 1;
                }
                return -1;
            }
        });
    }

    // 获取当天的开始时间
    public static java.util.Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获取当天的结束时间
    public static java.util.Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
    // 获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    // 获取某个日期的结束时间
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }
    // 获取本周的开始时间
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    // 获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }
    // 获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    // 获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    // 获取本月的开始时间
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    // 获取本月的结束时间
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }
    // 两个日期相减得到的天数
    public static int getDiffDays(Date beginDate, Date endDate) {
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }
        long diff = (endDate.getTime() - beginDate.getTime())
                / (1000 * 60 * 60 * 24);
        int days = new Long(diff).intValue();
        return days;
    }

    //传入Databean判断当前是否可以设置闹钟 去除设置闹钟时间在目前之前的
    public static Boolean isSetAlarm(DataBean dataBean, Context context){
        DatabaseFunctions databaseFunctions=new DatabaseFunctions(context);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Date now = null;
        Date date=null;
        try {
            now=simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            date=simpleDateFormat.parse(dataBean.getAlarmRemind());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.after(now);
    }

}
