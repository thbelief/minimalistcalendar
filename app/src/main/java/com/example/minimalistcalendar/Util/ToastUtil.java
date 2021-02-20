package com.example.minimalistcalendar.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Util
 * @ClassName: ToastUtil
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/19 11:14
 */
public class ToastUtil {
    //这个用于在子线程中 普通toast
    public static void ChildThreadToast(Context context,String msg){
        Looper.prepare();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    public static void ChildThreadErrorToast(Context context,String msg){
        Looper.prepare();
        Toasty.error(context,msg,Toasty.LENGTH_SHORT,true).show();
        Looper.loop();
    }
    public static void ChildThreadSuccessToast(Context context,String msg){
        Looper.prepare();
        Toasty.success(context,msg,Toasty.LENGTH_SHORT,true).show();
        Looper.loop();
    }
    public static void ChildThreadInfoToast(Context context,String msg){
        Looper.prepare();
        Toasty.info(context,msg,Toasty.LENGTH_SHORT,true).show();
        Looper.loop();
    }
    public static void ChildThreadWarningToast(Context context,String msg){
        Looper.prepare();
        Toasty.warning(context,msg,Toasty.LENGTH_SHORT,true).show();
        Looper.loop();
    }
    //子线程中调用dialog
    public static void ChildThreadDialog(Context context,String title,String content){
        Looper.prepare();
        DialogUtil.messageDialog(context,title,content);
        Looper.loop();
    }
}
