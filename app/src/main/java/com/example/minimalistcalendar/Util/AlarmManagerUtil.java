package com.example.minimalistcalendar.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.minimalistcalendar.AddMemorialDayActivity;
import com.example.minimalistcalendar.Alarm.MyAlarmRecevier;
import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.MainActivity;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Util
 * @ClassName: AlarmManagerUtil
 * @Description: 用于闹钟的提醒 注意由于国产厂商各种魔改的rom有可能失效 国内的有些手机厂商 为了保护用户的安全 对应用做了权限限制 只有在允许应用自动启动的时候  才允许自动唤醒我们的应用进程  也就是说 我们的应用被用户加入白名单的时候 我们才能唤醒我们的应用
 * @Author: 作者名
 * @CreateDate: 2021/2/11 11:36
 */
public class AlarmManagerUtil {
    //注册表中recevier的action anrdoid name
    public  static final String ALARM_ACTION="com.example.minimalistcalendar.alarmRecevier";
    public static void cancelAlarm(Context context, int id) {
        Intent intent = new Intent(context, MyAlarmRecevier.class);
        intent.setAction(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }
//    //实现id自增
//    public static int getIDFromSharedPreferences(Context context){
//        int id=1;
//        //如果没有的话直接创建 实现自增
//        if(MySharedPreferences.getData("notification_id",context).equals("nothing")){
//            MySharedPreferences.saveData("notification_id","1",context);
//            id=1;
//        }else{
//            id=Integer.parseInt(MySharedPreferences.getData("notification_id",context))+1;
//            //自加之后再放回去
//            MySharedPreferences.saveData("notification_id",id+"",context);
//        }
//        return id;
//    }
    public static void setAlarm(Context context,long intervalMillis,int id,String tipsName,String tips,int isMemorial) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
//        long intervalMillis=0;
//        intervalMillis=24 * 3600 * 1000 *day+3600*1000*hour+1000*60*minute;
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get
                (Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), 10);
        Intent intent = new Intent(context, MyAlarmRecevier.class);
        intent.setAction(ALARM_ACTION);
        intent.putExtra("msg", tips);
        intent.putExtra("name", tipsName);
        intent.putExtra("id", id+"");
        //是不是纪念日 如果是的话需要重复创建
        intent.putExtra("isMemorial",isMemorial+"");
        //如果PendingIntent已经存在，那么当前的PendingIntent会取消掉，然后产生一个新的PendingIntent。
        PendingIntent sender = PendingIntent.getBroadcast(context,id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断版本 如果版本不支持的话 使用的函数也不同
            am.setWindow(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + intervalMillis,intervalMillis, sender);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
    //设置闹钟
    public  static void setAlarmInCreate(DataBean dataBean,Context context,int isMemorial){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Date alarmTime=null;//提醒时间
        Date todayTime = null;//今天的时间
        try {
            alarmTime=simpleDateFormat.parse(dataBean.getAlarmRemind());
            todayTime=simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        int alarmDay=DateUtil.getDiffDays(todayTime,alarmTime);
//        int alarmHour=Integer.parseInt(new SimpleDateFormat("HH").format(alarmTime));
//        int alarmMinute=Integer.parseInt(new SimpleDateFormat("mm").format(alarmTime));

        //Log.d("MainActivity",alarmTime.getTime()+"  "+todayTime.getTime());
        //这里-1000*50 是因为本身设置提醒时间最小单位是分钟 最小的也只能是60s差距 等太久了
        long intervalMillis=alarmTime.getTime()-todayTime.getTime();
        AlarmManagerUtil.setAlarm(context,intervalMillis,
                dataBean.getId(),dataBean.getDate(),dataBean.getTitle(),isMemorial);
    }
}
