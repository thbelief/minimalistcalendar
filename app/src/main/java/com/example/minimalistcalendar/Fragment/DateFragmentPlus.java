package com.example.minimalistcalendar.Fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.minimalistcalendar.Adapter.ItemNullRecyclerViewAdapter;
import com.example.minimalistcalendar.Adapter.RecycleAdapter;
import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.EventBus.UpdateNoteEventBus;
import com.example.minimalistcalendar.MainActivity;
import com.example.minimalistcalendar.Manager.TopLayoutManager;
import com.example.minimalistcalendar.Network.DoPostDownload;
import com.example.minimalistcalendar.NoteDetailsActivity;
import com.example.minimalistcalendar.R;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.example.minimalistcalendar.Util.DateUtil;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.nlf.calendar.Solar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import es.dmoral.toasty.Toasty;


public class DateFragmentPlus extends Fragment {

    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    CalendarLayout mCalendarLayout;
    ImageView mImageView;

    //显示周次
    private TextView week_num_tv;
    //recyclerView
    private RecyclerView recyclerView;
    private RecycleAdapter recycleAdapter;
    //是否获取数据更新
    private boolean      isUpdateData = false;
    //按照日期排好序的databean
    private List<DataBean> listsortbydatabean;
    //临时特殊日期的集合
    private List<DataBean> templistMemorialDay=new ArrayList<DataBean>();
    //标记的map
    private Map<String, Calendar> map = new HashMap<>();

    //如果开启了工作日功能的 话 用此变量存储 保存的设置 0~6的下标 代表的事周一到周末
    private List<Integer> workDaysSetting= Arrays.asList(0,0,0,0,0,0,0);

    //天气部分的控件
    private LinearLayout linearLayout_weather;
    private ImageView ic_weather;
    private TextView tv_weather;
    private TextView tv_temperature;

    //如果开启了自动同步的话 打开软件的时候默认下载数据同步
    //当数据下载完成之后再绘制UI
    private Handler handler;

    @Override
    public void onResume() {
        //只有第一次之后才可以直接加载
        if(isUpdateData){
            mCalendarView.scrollToCurrent();
            loadNotes();
        }
        super.onResume();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //注册EventBus 最好加上判断 因为fragment生命周期后面还会在调用这个create 免得重复注册报错
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        return inflater.inflate(R.layout.fragment_date_plus, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑EventBUS
        EventBus.getDefault().unregister(this);
    }
    //EventBus响应函数
    @Subscribe
    public void onEventMainThread(UpdateNoteEventBus event) {
        //传递消息过来说明该更新UI了。
        //String msg = event.getMsg();
        //Log.d("DateFragmentPlus","已收到消息"+msg);
        //Toast.makeText(getActivity(),"DateFragmentPlus已更新UI",Toast.LENGTH_SHORT).show();
        if(event.getMsg().equals("update")){
            loadNotes();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        mTextMonthDay=getActivity().findViewById(R.id.dpf_MonthDay);
        mTextYear=getActivity().findViewById(R.id.dpf_year);
        mTextLunar=getActivity().findViewById(R.id.dpf_lunar);
        mTextCurrentDay=getActivity().findViewById(R.id.dpf_today);
        mCalendarLayout=getActivity().findViewById(R.id.calendarLayout);
        mCalendarView=getActivity().findViewById(R.id.calendarView);
        mRelativeTool=getActivity().findViewById(R.id.date_plus_fragment_rl);
        recyclerView=getActivity().findViewById(R.id.recyclerView);
        mImageView=getActivity().findViewById(R.id.fold_calendar);

        //天气的控件
        linearLayout_weather=getActivity().findViewById(R.id.weather_l);
        ic_weather=getActivity().findViewById(R.id.ic_weather);
        tv_weather=getActivity().findViewById(R.id.tv_weather);
        tv_temperature=getActivity().findViewById(R.id.tv_temperature);
        //周次
        //显示周次
        week_num_tv=getActivity().findViewById(R.id.week_num_tv);

        //点击回到今天
        mTextCurrentDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.info(getActivity(),"已回到今天",Toasty.LENGTH_SHORT).show();
                mCalendarView.scrollToCurrent();
            }
        });
        //点击收起日历
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCalendarLayout.isExpand()){
                    mCalendarLayout.shrink();
                    mImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }else{
                    mCalendarLayout.expand();
                    mImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                }
            }
        });
        //监听滑动 修改是否收起的图标
        mCalendarView.setOnViewChangeListener(new CalendarView.OnViewChangeListener() {
            @Override
            public void onViewChange(boolean isMonthView) {
                if(isMonthView){
                    mImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                }else{
                    mImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }
            }
        });
        //Toast.makeText(getActivity(),mCalendarView.getCurrentMonthCalendars().size()+"",Toast.LENGTH_SHORT).show();
        //监听选择了哪一个日期 如果有记事则直接显示出该天的记事
        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
                mTextYear.setText(String.valueOf(calendar.getYear()));
                mTextLunar.setText(calendar.getLunar());
                //同时修改周次
                //显示周次的 需要配合设置
                //如果 周次已经显示的话点击会改变 如果没有的话 不能修改 否则报错
                if(week_num_tv.getVisibility()==View.VISIBLE){
                    //显示周数
                    week_num_tv.setText("周次："+weekNum(calendar.getYear(),calendar.getMonth(),calendar.getDay()));
                }

                getNoteByDate(calendar.getYear(),calendar.getMonth(),calendar.getDay());
            }
        });
        //点击mTextMonthDay可以选时间 直接跳转到相应时间
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择 时间 然后跳转到该时间
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //如果选择了的话
                        mCalendarView.scrollToCalendar(Integer.parseInt((new SimpleDateFormat("yyyy")).format(date)),
                                Integer.parseInt((new SimpleDateFormat("MM")).format(date)),
                                Integer.parseInt((new SimpleDateFormat("dd")).format(date)));
                        //因为这个跳转管不到 year变化 那个没用 所以这里还要写一遍
                        for(int i=0;i<templistMemorialDay.size();i++){
                            map.put(getSchemeCalendar(Integer.parseInt((new SimpleDateFormat("yyyy")).format(date)),templistMemorialDay.get(i).getMonth(),templistMemorialDay.get(i).getDay(), 0xFF13acf0, "事").toString(),
                                    getSchemeCalendar(Integer.parseInt((new SimpleDateFormat("yyyy")).format(date)),templistMemorialDay.get(i).getMonth(),templistMemorialDay.get(i).getDay(), 0xFF13acf0, "事"));
                            schemeToCalendar();
                        }
                    }
                }).setOutSideCancelable(true).setTitleText("选择跳转时间").setTextColorCenter(getResources().
                        getColor(R.color.colorPrimary)).isDialog(true).build();
                pvTime.show();
            }
        });
        //监控月份改变 如果改变的话添加工作日的标记
        mCalendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                //显示工作日的 需要配合配置
                if(MySharedPreferences.getData("isOpenWorkDayFunction",getActivity()).equals("true")){
                    //默认是从周日开始的 周日~周六 共七天
                    workDaysSetting.set(0,is0Or1(0));
                    workDaysSetting.set(1,is0Or1(1));
                    workDaysSetting.set(2,is0Or1(2));
                    workDaysSetting.set(3,is0Or1(3));
                    workDaysSetting.set(4,is0Or1(4));
                    workDaysSetting.set(5,is0Or1(5));
                    workDaysSetting.set(6,is0Or1(6));
                    //加载数据进去
                    android.icu.util.Calendar calendar = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        calendar = android.icu.util.Calendar.getInstance();
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.set(year, month, 0); //输入类型为int类型
                        //Toast.makeText(getActivity(), ""+c.get(java.util.Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
                        //c.get(java.util.Calendar.DAY_OF_MONTH)是本月一共多少天
                        for(int i=1;i<=c.get(java.util.Calendar.DAY_OF_MONTH);i++){
                            //calendar.set(year,month,i);
                            //这里用solar来获取周几 用calendar要出错。
                            Solar solar=Solar.fromYmd(year,month,i);//0~6 从周日开始
                            //Log.d("MainActivity", i+"号是周"+solar.getWeek()+" 年 - 月 "+year+" "+month);
                            if(isInWorkDays(solar.getWeek())){
                                //Toast.makeText(getActivity(), "测试的东西"+mCalendarView.getCurYear()+"月"+i, Toast.LENGTH_SHORT).show();
                                map.put(getSchemeCalendar(year,month,i, 0xFFff6f00, "工").toString(),
                                        getSchemeCalendar(year,month,i, 0xFFff6f00, "工"));
                                schemeToCalendar();
                            }
                        }

                    }
                }
            }
        });

        //当年份变化的时候标记今年所有的特殊日期
         mCalendarView.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                //Toasty.normal(getActivity(),year+"",Toasty.LENGTH_SHORT).show();
                for(int i=0;i<templistMemorialDay.size();i++){
                    map.put(getSchemeCalendar(year,templistMemorialDay.get(i).getMonth(),templistMemorialDay.get(i).getDay(), 0xFF13acf0, "事").toString(),
                            getSchemeCalendar(year,templistMemorialDay.get(i).getMonth(),templistMemorialDay.get(i).getDay(), 0xFF13acf0, "事"));
                    schemeToCalendar();
                }
            }
        });
        //第一次数据已加载
        isUpdateData=true;
        loadNotes();
    }
    //标记到日历上面
    private void schemeToCalendar(){
        mCalendarView.setSchemeDate(map);
    }
    //获取标记calendar
    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }
    //点击了日历上面的日期之后显示记事 如果没有提示没有记事
    public void getNoteByDate(int year,int month,int day){
        DatabaseFunctions databaseFunctions=new DatabaseFunctions(getActivity().getApplicationContext());
        java.util.Calendar calendar= java.util.Calendar.getInstance();
        calendar.set(year,month,day);
        //Log.d("MainActivity", "getNoteByDate: "+calendar.get(java.util.Calendar.YEAR)+calendar.get(java.util.Calendar.MONTH));
        List<DataBean> d = databaseFunctions.getDataByDayPlus(calendar);

        if(d.size()==0){
            //Log.d("MainActivity", "无");
            //int month=calendar.get(java.util.Calendar.MONTH)+1;
        }else{
            //Log.d("MainActivity", "有");
            int position = 0;
            int id_item=d.get(0).getId();
            for(int i=0;i<listsortbydatabean.size();i++){
                if(id_item==listsortbydatabean.get(i).getId()){
                    position=i;
                    //Log.d("MainActivity", "getNoteByDate: position "+position);
                }
            }
            //如果点击的时候有的话 先收起 再滑动
            if(mCalendarLayout.isExpand()){
                mCalendarLayout.shrink();
                mImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
            }
            recyclerView.smoothScrollToPosition(position);
        }
    }
    //根据存储的教学周起始日期 计算当前具体的教学周周次
    public int weekNum(int year,int month,int day){
        String startDate=MySharedPreferences.getData("weekStartDate", getActivity());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Toast.makeText(TeachWeekActivity.this,"测试"+startDate,Toast.LENGTH_SHORT).show();
        Date today=null;
        Date startDateDay=null;
        try {
            today=simpleDateFormat.parse(year+"-"+month+"-"+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            startDateDay=simpleDateFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //返回周数
        return (int) ((today.getTime()-startDateDay.getTime())/(1000*3600*24*7));

    }
    //显示工作日的时候的数据需要从truefalse转换为0 1
    public int is0Or1(int i){
        List<String> lists=new ArrayList<String>();
        //从周日开始到周六结束
        lists.add(MySharedPreferences.getData("Sun",getActivity()));
        lists.add(MySharedPreferences.getData("Mon",getActivity()));
        lists.add(MySharedPreferences.getData("Tue",getActivity()));
        lists.add(MySharedPreferences.getData("Wed",getActivity()));
        lists.add(MySharedPreferences.getData("Thur",getActivity()));
        lists.add(MySharedPreferences.getData("Fri",getActivity()));
        lists.add(MySharedPreferences.getData("Sat",getActivity()));
        //因为i是1~7 而数组的小标是从0开始的
        if(lists.get(i).equals("true")){
            return 1;
        }
        return 0;
    }
    //判断dayofweek在不在工作日设置中
    public boolean isInWorkDays(int dayOfWeek){
        //如果是工作日就标记
        return workDaysSetting.get(dayOfWeek) == 1;
    }
    //返回本月的最后一天是多少号
    public int localMonthLastNum(){
        java.util.Calendar cale= java.util.Calendar.getInstance();
        // 获取当月第一天和最后一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String lastday;
        // 获取前月的最后一天
        cale = java.util.Calendar.getInstance();
        cale.add(java.util.Calendar.MONTH, 1);
        cale.set(java.util.Calendar.DAY_OF_MONTH, 0);
        lastday = format.format(cale.getTime());
        Date d=null;
        try {
            d=format.parse(lastday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.util.Calendar calendar= java.util.Calendar.getInstance();
        calendar.setTime(d);
        int result=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result= calendar.get(android.icu.util.Calendar.DATE);
        }
        return result;
    }
    public void loadNotes(){
        //每次都要清空否则数据不对
        map.clear();
        //显示工作日的 需要配合配置
        if(MySharedPreferences.getData("isOpenWorkDayFunction",getActivity()).equals("true")){
            //默认是从周日开始的 周日~周六 共七天
            workDaysSetting.set(0,is0Or1(0));
            workDaysSetting.set(1,is0Or1(1));
            workDaysSetting.set(2,is0Or1(2));
            workDaysSetting.set(3,is0Or1(3));
            workDaysSetting.set(4,is0Or1(4));
            workDaysSetting.set(5,is0Or1(5));
            workDaysSetting.set(6,is0Or1(6));

            //加载数据进去
            android.icu.util.Calendar calendar = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //calendar = android.icu.util.Calendar.getInstance();
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.set(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 0); //输入类型为int类型
                //Toast.makeText(getActivity(), ""+c.get(java.util.Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
                //c.get(java.util.Calendar.DAY_OF_MONTH)是本月一共多少天
                for(int i=1;i<=c.get(java.util.Calendar.DAY_OF_MONTH);i++){
                    //calendar.set(year,month,i);
                    //这里用solar来获取周几 用calendar要出错。
                    Solar solar=Solar.fromYmd(mCalendarView.getCurYear(), mCalendarView.getCurMonth(),i);//0~6 从周日开始
                    //Log.d("MainActivity", i+"号是周"+solar.getWeek()+" 年 - 月 "+year+" "+month);
                    if(isInWorkDays(solar.getWeek())){
                        //Toast.makeText(getActivity(), "测试的东西"+mCalendarView.getCurYear()+"月"+i, Toast.LENGTH_SHORT).show();
                        map.put(getSchemeCalendar(mCalendarView.getCurYear(), mCalendarView.getCurMonth(),i, 0xFFff6f00, "工").toString(),
                                getSchemeCalendar(mCalendarView.getCurYear(), mCalendarView.getCurMonth(),i, 0xFFff6f00, "工"));
                        //schemeToCalendar();
                    }
                }

            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                calendar = android.icu.util.Calendar.getInstance();
//                //localMonthLastNum是本月最后一天的号数
//                for(int i=1;i<=localMonthLastNum();i++){
//                    calendar.set(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),i);
//                    if(isInWorkDays(calendar.get(android.icu.util.Calendar.DAY_OF_WEEK)-1)){
//                        //Toast.makeText(getActivity(), "测试的东西"+mCalendarView.getCurYear()+"月"+i, Toast.LENGTH_SHORT).show();
//                        map.put(getSchemeCalendar(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),i, 0xFFff6f00, "工").toString(),
//                                getSchemeCalendar(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),i, 0xFFff6f00, "工"));
//                    }
//                }
//
//            }
        }
        //显示周次的 需要配合设置
        if(MySharedPreferences.getData("isTurnOnFunction",getActivity()).equals("true")){
            //显示周数
            week_num_tv.setVisibility(View.VISIBLE);
            week_num_tv.setText("周次："+weekNum(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),mCalendarView.getCurDay())+"");
        }
        //显示天气的 配合设置
        if(MySharedPreferences.getData("isDisplayWeather",getActivity()).equals("true")){
            //显示天气
            linearLayout_weather.setVisibility(View.VISIBLE);
            //根据返回的icon 显示天气图标
            ic_weather.getDrawable().setLevel(Integer.parseInt(MySharedPreferences.getData("weatherIcon",getActivity())));
            //修改天气状态
            tv_weather.setText(MySharedPreferences.getData("weatherState",getActivity()));
            //修改温度
            tv_temperature.setText(MySharedPreferences.getData("weatherTemp",getActivity())+"℃");
        }
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        DatabaseFunctions databaseFunctions=new DatabaseFunctions(getActivity().getApplicationContext());
        //从数据库添加数据到界面
        List<DataBean> list1=new ArrayList<>();
        list1=databaseFunctions.getAllDataBean();
        //在日历上面标记出有记事的日期
        //Log.d("MainActivity","这里"+map.toString());
        for(int i=0;i<list1.size();i++){
            //找出特殊日期 加入到临时集合 方便标记每年的特殊日期
            if(list1.get(i).getDegree().equals("MemorialDay")){
                //Log.d("MainActivity",list1.get(i).getDegree());
                templistMemorialDay.add(list1.get(i));
            }
            //要判断一下是否已经有值了 如果有的话 改为多显示
            if(map.containsKey(getSchemeCalendar(list1.get(i).getYear(), list1.get(i).getMonth(), list1.get(i).getDay(), 0xFF13acf0, "事").toString())){
                map.put(getSchemeCalendar(list1.get(i).getYear(), list1.get(i).getMonth(), list1.get(i).getDay(), 0xFFe53935, "多").toString(),
                        getSchemeCalendar(list1.get(i).getYear(), list1.get(i).getMonth(), list1.get(i).getDay(), 0xFFe53935, "多"));
            }else{
                map.put(getSchemeCalendar(list1.get(i).getYear(), list1.get(i).getMonth(), list1.get(i).getDay(), 0xFF13acf0, "事").toString(),
                        getSchemeCalendar(list1.get(i).getYear(), list1.get(i).getMonth(), list1.get(i).getDay(), 0xFF13acf0, "事"));
            }

        }
        //将标记绘制到calendar上
        schemeToCalendar();
        //对于日期排序
        DateUtil.sortListByDate(list1);
        //将日期排序之后的数据保存下来
        listsortbydatabean=list1;
        LinearLayoutManager manager=new TopLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        //判断是否记事列表为空 如果为空的话 切换为null adapter
        if(list1.isEmpty()){
            //Toast.makeText(getActivity(),"为空",Toast.LENGTH_SHORT).show();
            ItemNullRecyclerViewAdapter itemNullRecyclerViewAdapter=new ItemNullRecyclerViewAdapter(getActivity());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(itemNullRecyclerViewAdapter);
        }else{
            recycleAdapter = new RecycleAdapter(list1,getActivity());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(recycleAdapter);
            //item的点击事件
            itemClick(recycleAdapter);
        }
    }
    private void itemClick(RecycleAdapter recycleAdapter){
        //item点击事件
        recycleAdapter.setOnMyItemListener(new RecycleAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView item_id=(TextView) view.findViewById(R.id.id_item);
                if(!item_id.getText().toString().equals("0")){
                    //不为0的时候 因为存的时候id是从1开始的
                    Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
                    intent.putExtra("id",item_id.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }

}
