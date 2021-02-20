package com.example.minimalistcalendar.Util;

import android.content.Context;

import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Util
 * @ClassName: LoginUtil
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/19 16:19
 */
public class LoginUtil {
    //登陆相关的 主要就是判断是否登陆以及有网络
    public static Boolean isLogin(Context context){
        //如果已经登陆的话返回true
        if(MySharedPreferences.getData("isLoginStatus",context).equals("true")){
            return true;
        }
        return false;
    }
    public static String getUserID(Context context){
        return MySharedPreferences.getData("userID",context);
    }
}
