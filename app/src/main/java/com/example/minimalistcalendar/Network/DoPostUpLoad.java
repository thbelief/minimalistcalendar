package com.example.minimalistcalendar.Network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.MainActivity;
import com.example.minimalistcalendar.Util.JSONUtil;
import com.example.minimalistcalendar.Util.ToastUtil;
import com.google.gson.JsonArray;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Url;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Network
 * @ClassName: DoPostUpLoad
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/17 10:04
 */
public class DoPostUpLoad {
    private static String url="http://159.75.108.98:8080/JavaWeb_war/UploadData";
    //上传本地所有的记事数据 然后将服务器上的该用户的数据替换掉
    public static void uploadAllData(Context context,String userID) {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "开始上传");
                //获取全部databean
                DatabaseFunctions databaseFunctions=new DatabaseFunctions(context);
                JsonArray jsonArray=new JsonArray();
                try {
                    //这里要把用户ID一起放进去 才可以做到同步
                    jsonArray= JSONUtil.toJSONArray(databaseFunctions.getAllDataBean(),Integer.parseInt(userID));
                    //Log.d("MainActivity","数据"+jsonArray);
                    RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonArray));
                    OkHttpClient client =new OkHttpClient();
                    Request request=new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Call call= client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //Log.d("MainActivity","请求失败");
                            //不能直接toast 在子线程 中
                            ToastUtil.ChildThreadErrorToast(context,"上传失败");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //Log.d("MainActivity","返回："+response.body().string());
                            //不能直接toast 在子线程 中
                            ToastUtil.ChildThreadSuccessToast(context,"上传成功");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();


    }
}

