package com.example.minimalistcalendar.Network;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.minimalistcalendar.AddMemorialDayActivity;
import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.Bean.DownloadBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.EventBus.UpdateNoteEventBus;
import com.example.minimalistcalendar.Util.AlarmManagerUtil;
import com.example.minimalistcalendar.Util.DateUtil;
import com.example.minimalistcalendar.Util.JSONUtil;
import com.example.minimalistcalendar.Util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Network
 * @ClassName: DoPostDownload
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/18 15:58
 */
public class DoPostDownload {
    //下载 从服务器把数据下载下来
    private static String url="http://www.thbelief.xyz/DownloadData";
    public static void downloadAllData(Context context,String userID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "开始下载");
                FormBody formBody = new FormBody.Builder().add("userID", userID).build();
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
                        ToastUtil.ChildThreadErrorToast(context,"下载失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Toast.makeText(context,"下载成功",Toast.LENGTH_SHORT).show();
                        //转化为json数组 然后存到本地
                        JsonArray jsonArray= new JsonParser().parse(response.body().string()).getAsJsonArray();
                        //Log.d("MainActivity","返回："+jsonArray.get(0).getAsJsonObject().get("title"));
                        //对本地的数据进行更新
                        DatabaseFunctions databaseFunctions=new DatabaseFunctions(context);
                        databaseFunctions.deleteAllData();
                        for(int i=0;i<jsonArray.size();i++){
                            DataBean dataBean=new DataBean();
                            dataBean.setId(jsonArray.get(i).getAsJsonObject().get("_id").getAsInt());
                            dataBean.setTitle(jsonArray.get(i).getAsJsonObject().get("title").getAsString());
                            dataBean.setDegree(jsonArray.get(i).getAsJsonObject().get("degree").getAsString());
                            dataBean.setDegreeColor(jsonArray.get(i).getAsJsonObject().get("degreeColor").getAsString());
                            dataBean.setYear(jsonArray.get(i).getAsJsonObject().get("year").getAsInt());
                            dataBean.setMonth(jsonArray.get(i).getAsJsonObject().get("month").getAsInt());
                            dataBean.setDay(jsonArray.get(i).getAsJsonObject().get("day").getAsInt());
                            dataBean.setIsAlarm(jsonArray.get(i).getAsJsonObject().get("isAlarm").getAsInt());
                            dataBean.setAlarmRemind(jsonArray.get(i).getAsJsonObject().get("alarmRemind").getAsString());
                            dataBean.setDescription(jsonArray.get(i).getAsJsonObject().get("description").getAsString());
                            databaseFunctions.insertContainID(dataBean);
                            int isMemorialDay=0;
                            if(jsonArray.get(i).getAsJsonObject().get("degree").getAsString().equals("MemorialDay")){
                                isMemorialDay=1;
                            }
                            //设置闹钟
                            if(jsonArray.get(i).getAsJsonObject().get("isAlarm").getAsInt()==1&& DateUtil.isSetAlarm(dataBean,context)){
                                //Log.d("MainActivity","设置  "+dataBean.getAlarmRemind());
                                AlarmManagerUtil.setAlarmInCreate(dataBean, context,isMemorialDay);
                            }
                        }
                        //不能直接toast 在子线程 中
                        ToastUtil.ChildThreadSuccessToast(context,"下载成功");
                    }
                });
            }
        }).start();


    }
}
