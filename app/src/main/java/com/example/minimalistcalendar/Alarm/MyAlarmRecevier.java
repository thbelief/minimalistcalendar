package com.example.minimalistcalendar.Alarm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.MainActivity;
import com.example.minimalistcalendar.NoteDetailsActivity;
import com.example.minimalistcalendar.R;
import com.example.minimalistcalendar.Util.AlarmManagerUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Alarm
 * @ClassName: MyAlarmRecevier
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/11 11:42
 */
public class MyAlarmRecevier extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    private Vibrator    vibrator;
    private Context context;

    private NotificationManager notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        String msg = intent.getStringExtra("msg");
        String name= intent.getStringExtra("name");
        int id = Integer.parseInt(intent.getStringExtra("id"));
        //Log.d("MainActivity","id"+id);
        long intervalMillis = intent.getLongExtra("intervalMillis", 0);
        int isMemorial=Integer.parseInt(intent.getStringExtra("isMemorial"));
        if(isMemorial==1){
            //如果是纪念日的话 这一次提醒之后还要顺延到下一年同一时间继续提醒
            DatabaseFunctions databaseFunctions=new DatabaseFunctions(context);
            DataBean dataBean=databaseFunctions.getDataById(id);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String alarmOther=null;
            int alarmyear=0;
            try {
                //年份往后推一即可
                alarmyear=1+Integer.parseInt(new SimpleDateFormat("yyyy").format(simpleDateFormat.parse(dataBean.getAlarmRemind())));
                alarmOther=(new SimpleDateFormat("MM-dd-HH-mm")).format(simpleDateFormat.parse(dataBean.getAlarmRemind()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Log.d("MainActivity","下一次提醒时间是"+alarmyear+"-"+alarmOther);
            dataBean.setId(databaseFunctions.getLastIDAdd1());
            dataBean.setAlarmRemind(alarmyear+"-"+alarmOther);
            //这次的闹钟响了之后 再创建新的提醒
            databaseFunctions.insertContainID(dataBean);
            AlarmManagerUtil.setAlarmInCreate(dataBean,context,1);
        }
//        Log.d("MainActivity", "onReceive: "+"进入了服务");
//        Toast.makeText(context,"测试一下 当前是闹钟提醒",Toast.LENGTH_SHORT).show();

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "alarmNotification";
            String channelName = "记事闹钟通知栏";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
        //这个是notification点击跳转的intent 跳转到某个界面
        Intent intent1 = new Intent(context,NoteDetailsActivity.class);
        //打开该ID指定的记事详情页 必须给id否则会报错。 注意这里是intent1 不是intent
        intent1.putExtra("id",intent.getStringExtra("id"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
        //创建Notification，传入Context和channelId
        Notification notification = new NotificationCompat.Builder(context, "alarmNotification")
                .setContentTitle(name)
                .setContentText(msg)
                .setVibrate(new long[]{0, 1000, 1000, 1000}) //通知栏消息震动
                .setLights(Color.GREEN, 1000, 2000) //通知栏消息闪灯(亮一秒间隔两秒再亮
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification_img)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent,true)
                .setContentIntent(pendingIntent)
                //在build()方法之前还可以添加其他方法
                .build();
        //发起Notification后，铃声和震动均只执行一次
        notification.flags=Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(1, notification);
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        //图标显示小红点
        channel.enableLights(true);
        channel.setLightColor(0xFFd50000);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setBypassDnd(true);
        channel.canShowBadge();
        channel.enableVibration(true);
        channel.getAudioAttributes();
        channel.shouldShowLights();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

}
