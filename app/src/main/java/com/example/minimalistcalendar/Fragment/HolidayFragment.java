package com.example.minimalistcalendar.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minimalistcalendar.Adapter.HolidayRecycleAdapter;
import com.example.minimalistcalendar.Bean.HolidayBean;
import com.example.minimalistcalendar.R;
import com.example.minimalistcalendar.Util.DateUtil;
import com.ldoublem.loadingviewlib.view.LVCircularRing;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Fragment
 * @ClassName: HolidayFragment
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/5 17:08
 */
public class HolidayFragment extends Fragment {
    //今年一共多少天
    private int thisYearDaysNum = 365;
    //
    private List<HolidayBean> holidayBeanList=new ArrayList<HolidayBean>();
    //是否已经加载过数据了
    private boolean isLoadDate=false;

    private RecyclerView recyclerView=null;

    //handler
    private Handler handler;
    //加载动画 loading view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_holiday, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        //绑定recyclerView
        recyclerView=getActivity().findViewById(R.id.holiday_fragment_recyclerview);
        //加载动画的绑定
        LVCircularRing lvCircularRing=getActivity().findViewById(R.id.lvc_ircularring);
        lvCircularRing.setViewColor(R.color.N_stretchTextColor);
        lvCircularRing.setBarColor(Color.YELLOW);
        //转圈
        lvCircularRing.startAnim();

        //recyclerView相关
        LinearLayoutManager manager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    //更新UI
                    lvCircularRing.stopAnim();
                    lvCircularRing.setVisibility(View.GONE);
                    //这里出过大问题 不小心把findviewbyID写到这个线程里面了，如果切换到其它的fragment的时候
                    //这个线程刚好执行到这一步的话 是找不到绑定的控件的 因为已经在其它的fragment里面了 会提示空指针异常。
                    //引以为戒 findviewbyid的位置一定要找好
                    //开始加载items
                    HolidayRecycleAdapter holidayRecycleAdapter = new HolidayRecycleAdapter(holidayBeanList,getActivity());
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(holidayRecycleAdapter);
                }
            }
        };
        //如果还没加载数据的话 就加载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //加载数据 只有刚创建的时候需要加载
                if(!isLoadDate){
                    loadNotes();
                }
                //加载数据之后发送消息给handler 更新ui
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();

//        //延时 加载 因为数据加载需要一定时间 防止卡顿
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 1500);//3秒后执行Runnable中的run方法

    }

    //recyclerView加载方法 加载数据到reclerview中
    public void loadNotes(){
        //已经加载过数据 这个必须写到前面 不然可能出现错误
        isLoadDate=true;
        //先获取节日信息到holidayBeanList
        getHolidayData();
        //根据日期排序
        //Log.d("MainActivity", "loadNotes: "+holidayBeanList.toString());
        sortDataByDate();
        //Log.d("MainActivity","所有的数据："+holidayBeanList.toString());
    }
    //根据日期排序
    public void sortDataByDate(){
        DateUtil.sortListByDateToHoliday(holidayBeanList);
//        for(int i=0;i<holidayBeanList.size();i++){
//            Log.d("MainActivity","所有的数据："+ holidayBeanList.get(i).getSolar()+" "+holidayBeanList.get(i).getHolidays().toString()+i);
//        }
    }
    //获取到节假日数据
    public void getHolidayData(){
        //获取今天
        Solar today=Solar.fromDate(new Date());
//        //String转Date
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date todayDate=simpleDateFormat.parse(today.toYmd());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        //判断是不是闰年 闰年366天  平年365天
        Boolean isLeapYear=today.isLeapYear();
        if(isLeapYear){
            thisYearDaysNum=366;
        }else{
            thisYearDaysNum=365;
        }
        //加入节气
        Lunar lunar_today = today.getLunar();
        HashMap map=new HashMap();
        for(int k=0;k<24;k++){
            //加入二十四节气
            //Log.d("MainActivity","Test： "+k);
            map.put(lunar_today.getNextJieQi().getSolar(),lunar_today.getNextJieQi().getName()+"[节气]");
            //Log.d("MainActivity","Test： "+lunar_today);
            //Log.d("MainActivity","Test： "+lunar_today.getNextJieQi().getSolar());
            Solar nexts=lunar_today.getNextJieQi().getSolar();
            lunar_today=nexts.next(1).getLunar();
            //Log.d("MainActivity","Test1： "+lunar_today);
        }
        //Log.d("MainActivity","Test： "+map.values().toString());
        Set<HashMap.Entry<Solar, String>> entryseSet=map.entrySet();
        for (HashMap.Entry<Solar, String> entry : entryseSet) {
//            try {
//                hb.setSolar(Solar.fromDate(simpleDateFormat.parse(entry.getKey())));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            HolidayBean hb=new HolidayBean();
            hb.setSolar(entry.getKey());
            List<String> l=new ArrayList<String>();
            l.add(entry.getValue());
            hb.setHolidays(l);
            //Log.d("MainActivity","Test： "+hb.getSolar()+" "+hb.getHolidays().toString());
            holidayBeanList.add(hb);
        }
        //Log.d("MainActivity","看： "+holidayBeanList.toString());
        for(int i=0;i<thisYearDaysNum;i++){
            //循环今年一共多少天
            HolidayBean holidayBean=new HolidayBean();
            holidayBean.setSolar(today.next(i));
            //添加节假日
            // 先转阴历然后查阳历阴历的节假日一起传入
            Lunar lunarToday = today.next(i).getLunar();

            //分别获取节日 然后再合并(要判断是否为空)
            List<String> holidayLists_Solar = new ArrayList<String>();
            List<String> holidayLists_Lunar = new ArrayList<String>();
            List<String> holidayLists=new ArrayList<String>();
            if(!Solar.fromYmd(today.next(i).getYear(), today.next(i).getMonth(),today.next(i).getDay()).getFestivals().isEmpty() ||
                    !Lunar.fromYmd(lunarToday.getYear(), lunarToday.getMonth(),lunarToday.getDay()).getFestivals().isEmpty()){
                //只要有一个为真就进去
                if(!Solar.fromYmd(today.next(i).getYear(), today.next(i).getMonth(),
                        today.next(i).getDay()).getFestivals().isEmpty()){
                    holidayLists_Solar.addAll(Solar.fromYmd(today.next(i).getYear(), today.next(i).getMonth(),
                            today.next(i).getDay()).getFestivals());
                    holidayLists.addAll(holidayLists_Solar);
                    holidayBean.setHolidays(holidayLists);
                    //Log.d("MainActivity","前： "+holidayLists.toString());
                    holidayBeanList.add(holidayBean);
                }
                if(!Lunar.fromYmd(lunarToday.getYear(), lunarToday.getMonth(),lunarToday.getDay()).getFestivals().isEmpty()){
                    holidayLists_Lunar.addAll(Lunar.fromYmd(lunarToday.getYear(), lunarToday.getMonth(),
                            lunarToday.getDay()).getFestivals());
                    holidayLists.addAll(holidayLists_Lunar);
                    holidayBean.setHolidays(holidayLists);
                    //Log.d("MainActivity","前： "+holidayLists.toString());
                    holidayBeanList.add(holidayBean);
                }else{
                    continue;
                }
            }
//            //节气
//            Lunar d = Lunar.fromYmd(today.getYear(),today.getMonth(),today.getDay());
//            Map<String, Solar> map= (Map<String, Solar>) d.getJieQiTable();
//            //Log.d("MainActivity","测试 "+map.get("雨水")+" "+Solar.fromYmd(today.next(i).getYear(), today.next(i).getMonth(),today.next(i).getDay()));
//            //Log.d("MainActivity",map.toString());
//            if(map.containsValue(Solar.fromYmd(today.next(i).getYear(), today.next(i).getMonth(),today.next(i).getDay()))){
//                //如果这个日期有节气
//                Log.d("MainActivity","进入");
//                holidayLists.add(getKey(map,today.next(i).toYmd()));
//                holidayBean.setHolidays(holidayLists);
//                //Log.d("MainActivity","前： "+holidayLists.toString());
//                holidayBeanList.add(holidayBean);
//            }
        }
    }
//    //输入value返回key
//    public static String getKey(Map map, String value){
//        String key1= "";
//        Set<Map.Entry<Object, Object>> entryseSet=map.entrySet();
//        for (HashMap.Entry<Object, Object> entry : entryseSet) {
//            Log.d("MainActivity","每一个list： "+entry.getValue().toString()+"  "+value);
//            if(entry.getValue().toString().equals(value)){
//
//                key1= entry.getKey().toString();
//                break;
//            }
//        }
//        return key1;
//    }


}
