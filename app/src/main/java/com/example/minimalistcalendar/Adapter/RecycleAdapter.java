package com.example.minimalistcalendar.Adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.R;

import java.util.List;


/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Adapter
 * @ClassName: RecycleAdapter
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/1/29 16:47
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DataBean> list;
    private Context context;
    private MyItemClickListener listener;
    //构造方法，传入数据
    public RecycleAdapter(List<DataBean> list,Context ctx){
        this.list = list;
        context=ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
//        inflater = LayoutInflater.from(context).inflate(R.layout.item_recyclerview,parent,false);
//        MyViewHolder myViewHolder = new MyViewHolder(inflater);
//        return myViewHolder;
        View view =View.inflate(parent.getContext(),R.layout.item_recyclerview,null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position) {
        //将数据和控件绑定
        DataBean dataBean=(DataBean)list.get(position);
        //Log.d("MainActivity",dataBean.toString());
        MyViewHolder myViewHolder=(MyViewHolder) holder;
//        if(dataBean.getDegreeColor().equals("Green")){
//            myViewHolder.degreeColor.setBackgroundColor(R.color.note_low);
//        }
        //Log.d("MainActivity",dataBean.getDegreeColor());
        if(dataBean.getDegree().equals("MemorialDay")){
            //纪念日
            myViewHolder.degreeColor.setBackground(context.getResources().getDrawable(R.drawable.memorialday_simple));
        }else{
            //记事
            if(dataBean.getDegree().equals("低")){
                myViewHolder.degreeColor.setBackground(context.getResources().getDrawable(R.drawable.degree_low));
            }else if(dataBean.getDegree().equals("中")){
                myViewHolder.degreeColor.setBackground(context.getResources().getDrawable(R.drawable.degree_mid));
            }else{
                myViewHolder.degreeColor.setBackground(context.getResources().getDrawable(R.drawable.degree_high));
            }
        }
        myViewHolder.date.setText(dataBean.getYear()+"年"+
                dataBean.getMonth()+"月"+dataBean.getDay()+"日");
        myViewHolder.title.setText(dataBean.getTitle());
        if(dataBean.getIsAlarm()==0){
            myViewHolder.isAlarm.setVisibility(View.GONE);
        }else{
            myViewHolder.isAlarm.setVisibility(View.VISIBLE);
        }
        //id setText参数如果是int类型，一定要在最后加上 +""，否则报错!!! 找了好久的错误！！
        myViewHolder.id_item.setText(dataBean.getId() +"");

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                if(listener != null){
                    listener.onItemClick(v,position);
                }
            }
        });
        //Log.d("MainActivity",myViewHolder.toString());
    }


    public interface MyItemClickListener {
        public void onItemClick(View view,int position);
    }

    public void setOnMyItemListener(MyItemClickListener listener){
        this.listener = listener;
    }

    //获取屏幕的高度以方便随着日期调整item
    public int getDeviceHeight(){
        //存疑？
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    @Override
    public int getItemCount() {
        //返回Item总条数
        return list.size();
    }

    //内部类，绑定控件
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView degreeColor,title,date,id_item;
        private ImageView isAlarm;
        public MyViewHolder(View itemView) {
            super(itemView);
            degreeColor=(TextView) itemView.findViewById(R.id.degree_color_item);
            title=(TextView) itemView.findViewById(R.id.title_item);
            date=(TextView) itemView.findViewById(R.id.date_item);
            isAlarm=(ImageView) itemView.findViewById(R.id.alarm_ic_item);
            id_item=(TextView) itemView.findViewById(R.id.id_item);
        }
    }


}
