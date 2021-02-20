package com.example.minimalistcalendar.SharePreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.SharePreferences
 * @ClassName: MySharedPreferences
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/8 9:32
 */
public class MySharedPreferences {
    //这个类用来存取设置相关的内容

    //创建一个SharedPreferences    类似于创建一个数据库，库名为 data
    public static SharedPreferences share(Context context){
        return context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }
    public static void saveData(String key, String value, Context context){
        SharedPreferences.Editor e=share(context).edit();
        e.putString(key,value);
        e.apply();
    }
    public static String getData(String key, Context context){
        //后面的参数是获取失败的时候的默认返回值
        return share(context).getString(key,"nothing");
    }

}
