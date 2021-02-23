package com.example.minimalistcalendar.Network;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.minimalistcalendar.EventBus.UpdateLoginEventBus;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.example.minimalistcalendar.Util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Network
 * @ClassName: DoPostModifyPassword
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/23 11:22
 */
public class DoPostModifyPassword {
    private static String url="http://159.75.108.98:8080/JavaWeb_war/ModifyPassword";

    public static void modify(Context context,String account_number,String account_password,String userMark) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "开始修改");
                FormBody formBody = new FormBody.Builder().
                        add("account_number", account_number).
                        add("userMark", userMark).
                        add("account_password",account_password).build();
                OkHttpClient client =new OkHttpClient();
                Request request=new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call= client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d("MainActivity","请求失败");
                        //不能直接toast 在子线程 中
                        ToastUtil.ChildThreadErrorToast(context,"修改密码网络请求失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Looper.prepare();
                        String resp=response.body().string();
                        //返回的是userID
                        if(resp.equals("输入错误 修改失败")){
                            Toasty.error(context,"输入格式有误 修改失败",Toasty.LENGTH_SHORT).show();
                        }else if(resp.equals("账号或标识错误")){
                            Toasty.error(context,"账号或标识错误",Toasty.LENGTH_SHORT).show();
                        }else if(resp.equals("密码修改成功")){
                            Toasty.success(context,resp,Toasty.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    }
                });


            }
        }).start();
    }
}
