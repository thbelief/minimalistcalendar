package com.example.minimalistcalendar.Util;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Util
 * @ClassName: NotificationUtil
 * @Description: 显示到任务栏中的notification
 * @Author: 作者名
 * @CreateDate: 2021/2/11 14:20
 */
public class NotificationUtil {
    //发送通知栏消息必须先创建通知栏渠道
    private static NotificationManager notificationManager;
    private static void InitNotificationManager(Context context){
        if (notificationManager == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
        }
        //判断是否为8.0以上：Build.VERSION_CODES.O为26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道ID
            String channelId = "alarmNotification";
            //创建通知渠道名称
            String channelName = "记事闹钟通知栏";
            //创建通知渠道重要性
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(context, channelId, channelName, importance);
        }
    }
    //创建通知渠道
    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context, String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        //为NotificationManager设置通知渠道
        notificationManager.createNotificationChannel(channel);
    }
}
