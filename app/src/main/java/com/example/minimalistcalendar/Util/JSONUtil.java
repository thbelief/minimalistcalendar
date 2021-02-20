package com.example.minimalistcalendar.Util;

import com.example.minimalistcalendar.Bean.DataBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Util
 * @ClassName: JSONUtil
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/18 11:39
 */
public class JSONUtil {
    //这个类用来将数据转化为json 方便和服务器交互
    public static JsonArray toJSONArray(List<DataBean> lists,int userID) throws JSONException {
        //传入DataBean数组 返回JSON的数组
        JsonArray jsonArray=new JsonArray();
        for(int i=0;i<lists.size();i++){
            JsonObject json= new JsonObject();
            json.addProperty("userID",userID);
            json.addProperty("_id",lists.get(i).getId());
            json.addProperty("title",lists.get(i).getTitle());
            json.addProperty("degree",lists.get(i).getDegree());
            json.addProperty("degreeColor",lists.get(i).getDegreeColor());
            json.addProperty("year",lists.get(i).getYear());
            json.addProperty("month",lists.get(i).getMonth());
            json.addProperty("day",lists.get(i).getDay());
            json.addProperty("isAlarm",lists.get(i).getIsAlarm());
            json.addProperty("alarmRemind",lists.get(i).getAlarmRemind());
            json.addProperty("description",lists.get(i).getDescription());

            jsonArray.add(json);
        }
        return jsonArray;
    }
    //单个DataBean的转换
    public static JSONObject toJSON(DataBean dataBean,String userID) throws JSONException {
        JSONObject json=new JSONObject();
        json.put("userID",userID);
        json.put("_id",dataBean.getId());
        json.put("title",dataBean.getTitle());
        json.put("degree",dataBean.getDegree());
        json.put("degreeColor",dataBean.getDegreeColor());
        json.put("year",dataBean.getYear());
        json.put("month",dataBean.getMonth());
        json.put("day",dataBean.getDay());
        json.put("isAlarm",dataBean.getIsAlarm());
        json.put("alarmRemind",dataBean.getAlarmRemind());
        json.put("description",dataBean.getDescription());
        return json;
    }
}
