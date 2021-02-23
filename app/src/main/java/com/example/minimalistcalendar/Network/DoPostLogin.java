package com.example.minimalistcalendar.Network;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.minimalistcalendar.EventBus.UpdateLoginEventBus;
import com.example.minimalistcalendar.EventBus.UpdateNoteEventBus;
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
 * @ClassName: DoPostLogin
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/19 13:31
 */
public class DoPostLogin {
    private static String url="http://159.75.108.98:8080/JavaWeb_war/Login";

    public static void login(Context context,String account_number,String account_password) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "开始登陆");
                FormBody formBody = new FormBody.Builder().
                        add("account_number", account_number).
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
                        ToastUtil.ChildThreadErrorToast(context,"登陆网络请求失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //不能直接toast 在子线程 中
                        //ToastUtil.ChildThreadSuccessToast(context,"登陆成功");
                        Looper.prepare();
                        String resp=response.body().string();
                        //返回的是userID
                        if(resp.equals("账号不存在或者密码错误")){
                            Toasty.error(context,resp,Toasty.LENGTH_SHORT).show();
                        }else{
                            //这个时候是ID
                            Toasty.success(context,"登录成功",Toasty.LENGTH_SHORT).show();
                            saveData(context,resp,account_number,account_password);
                            //通知该更新UI了
                            EventBus.getDefault().post(new UpdateLoginEventBus("updateLoginState"));
                        }
                        Looper.loop();
                    }
                });


            }
        }).start();
    }
    public static void saveData(Context context,String userID,String account_number,String account_password){
        //Toasty.info(context,"进来了",Toasty.LENGTH_SHORT).show();
        MySharedPreferences.saveData("isLoginStatus","true", context);
        MySharedPreferences.saveData("userID",userID, context);
        MySharedPreferences.saveData("account_number",account_number,context);
        MySharedPreferences.saveData("account_password",account_password,context);
    }
}
