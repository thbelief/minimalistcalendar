package com.example.minimalistcalendar.Network;

import android.content.Context;
import android.util.Log;

import com.example.minimalistcalendar.Util.ToastUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Network
 * @ClassName: DoPostRegister
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/19 13:36
 */
public class DoPostRegister {
    private static String url="http://www.thbelief.xyz/Registered";

    //log是找回密码的标识
    public static void register(Context context, String account_number, String account_password,String userMark) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "开始注册");
                FormBody formBody = new FormBody.Builder().
                        add("account_number", account_number).
                        add("account_password",account_password).
                        add("userMark",userMark).build();
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
                        ToastUtil.ChildThreadErrorToast(context,"注册网络请求失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //不能直接toast 在子线程 中
                        ToastUtil.ChildThreadSuccessToast(context,response.body().string());
                    }
                });


            }
        }).start();


    }
}
