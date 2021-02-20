package com.example.minimalistcalendar.Adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.Bean.HolidayBean;
import com.example.minimalistcalendar.R;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Adapter
 * @ClassName: HolidayRecycleAdapter
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/6 9:09
 */
public class HolidayRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HolidayBean> list;
    private Context           context;
    //构造方法，传入数据
    public HolidayRecycleAdapter(List<HolidayBean> list,Context ctx){
        this.list = list;
        context=ctx;
    }

    @Override
    public HolidayRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =View.inflate(parent.getContext(),R.layout.item_recyclerview_holiday,null);
        return new HolidayRecycleAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position) {
        //将数据和控件绑定
        HolidayBean holidayBean=(HolidayBean) list.get(position);
        HolidayRecycleAdapter.MyViewHolder myViewHolder=(HolidayRecycleAdapter.MyViewHolder) holder;
        //具体绑定之后的填充数据
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date holidayDate= null;
        try {
            holidayDate = simpleDateFormat.parse(holidayBean.getSolar().toYmd());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date today=new Date();
        Date todayDate= null;
        try {
            todayDate = simpleDateFormat.parse(simpleDateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (holidayDate != null) {
            myViewHolder.distanceToday.setText("距今天"+getDistanceNum(holidayDate,todayDate)+"天");
        }
        myViewHolder.holidayDate.setText(holidayBean.getSolar().toYmd());
        //这里？
        myViewHolder.holidayName.setText(holidayBean.getHolidays());
    }

    //计算日期cha多少天
    public int getDistanceNum(Date a, Date b){
        //先判断大小 用大的减去小的
        Date max=new Date();
        Date min=new Date();
        if(a.before(b)){
            //如果a早于b的话
            max=b;
            min=a;
        }else{
            max=a;
            min=b;
        }
        int num= (int) ((max.getTime()-min.getTime())/(3600*24*1000));
        //返回相差多少天
        return num;
    }
    @Override
    public int getItemCount() {
        //返回Item总条数
        return list.size();
    }

    //内部类，绑定控件
    class MyViewHolder extends RecyclerView.ViewHolder{
        //绑定距离今日时间 节日时间 节日名称
        private TextView distanceToday,holidayDate,holidayName;
        public MyViewHolder(View itemView) {
            super(itemView);
            distanceToday=itemView.findViewById(R.id.holiday_fragment_distance_today);
            holidayDate=itemView.findViewById(R.id.holiday_fragment_holiday_time);
            holidayName=itemView.findViewById(R.id.holiday_fragment_holiday_name);
        }
    }


}
