package com.example.minimalistcalendar.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minimalistcalendar.Adapter.ItemNullRecyclerViewAdapter;
import com.example.minimalistcalendar.Adapter.RecycleAdapter;
import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.MainActivity;
import com.example.minimalistcalendar.Manager.TopLayoutManager;
import com.example.minimalistcalendar.NoteDetailsActivity;
import com.example.minimalistcalendar.R;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.example.minimalistcalendar.Util.DateUtil;
import com.necer.calendar.BaseCalendar;
import com.necer.calendar.Miui9Calendar;
import com.necer.enumeration.CalendarState;
import com.necer.enumeration.CheckModel;
import com.necer.enumeration.DateChangeBehavior;
import com.necer.listener.OnCalendarChangedListener;
import com.necer.painter.InnerPainter;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateFragment extends Fragment {
    private Miui9Calendar miui9Calendar;
    private   TextView   textView;
    private   ImageView  imageView;

    //recyclerView
    private RecyclerView   recyclerView;
    private RecycleAdapter recycleAdapter;
    private Context      context;
    private List<String> list;
    //是否获取数据更新
    private boolean isUpdateData = false;

    //按照日期排好序的databean
    private List<DataBean> listsortbydatabean;
    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //进入Fragment的时候
        if(enter&&!isUpdateData){
            isUpdateData=true;
        }else{
            isUpdateData=false;
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onPause() {
        super.onPause();
        isUpdateData=false;
    }

    @Override
    public void onResume() {
        //数据更新的时候
        if(!isUpdateData){
            loadNotes();
            isUpdateData=true;
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_date, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        //参考fragment中点击事件的写法 https://blog.csdn.net/u010940300/article/details/43170477
        textView= getActivity().findViewById(R.id.title_month);
        miui9Calendar=getActivity().findViewById(R.id.miui9Calendar);
        imageView=getActivity().findViewById(R.id.title_packup);
        //recyclerView加载
         /*
        与ListView效果对应的可以通过LinearLayoutManager来设置
        与GridView效果对应的可以通过GridLayoutManager来设置
        与瀑布流对应的可以通过StaggeredGridLayoutManager来设置
        */
        recyclerView = getActivity().findViewById(R.id.recyclerView);
        //日历选中模式checkModel
        //checkModel = (CheckModel) getActivity().getIntent().getSerializableExtra("selectedModel");
        miui9Calendar.setCheckMode(CheckModel.SINGLE_DEFAULT_CHECKED);
        
        //InnerPainter innerPainter = (InnerPainter) miui9Calendar.getCalendarPainter();

        loadNotes();
        //日历里面的点击事件
        miui9Calendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarChange(BaseCalendar baseCalendar, int year, int month, LocalDate localDate, DateChangeBehavior dateChangeBehavior) {
                textView.setText(year + "年" + month + "月" );
                //Toast.makeText(getActivity(),localDate.toString(),Toast.LENGTH_SHORT).show();
                //点击之后提示
                getNoteByDate(localDate.toDate());
            }
        });
        //收起日历
        getActivity().findViewById(R.id.title_packup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fold(view);
            }
        });
        //回到今天
        getActivity().findViewById(R.id.back_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                today(view);
            }
        });

    }
    //点击了日历上面的日期之后显示记事 如果没有提示没有记事
    public void getNoteByDate(Date date){
        DatabaseFunctions databaseFunctions=new DatabaseFunctions(getActivity().getApplicationContext());
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        //Log.d("MainActivity", "getNoteByDate: "+date.toString());
        List<DataBean> d = databaseFunctions.getDataByDay(calendar);
        if(d.size()==0){
            int month=calendar.get(Calendar.MONTH)+1;
            //Log.d("MainActivity", "getNoteByDate: "+calendar.get(Calendar.MONTH));
//            Toast.makeText(getActivity(),calendar.get(Calendar.YEAR)+"年"+month+
//                    "月"+calendar.get(Calendar.DAY_OF_MONTH)+"日 暂未创建记事！",Toast.LENGTH_SHORT).show();
        }else{
            int position = 0;
            int id_item=d.get(0).getId();
            for(int i=0;i<listsortbydatabean.size();i++){
                if(id_item==listsortbydatabean.get(i).getId()){
                    position=i;
                    //Log.d("MainActivity", "getNoteByDate: position "+position);
                }
            }
            //如果点击的时候有的话 先收起 再滑动
            if(miui9Calendar.getCalendarState() == CalendarState.MONTH){
                miui9Calendar.toWeek();
            }
            recyclerView.smoothScrollToPosition(position);
        }
    }
    //收起/放下日历
    public void fold(View view){
        if (miui9Calendar.getCalendarState() == CalendarState.WEEK) {
            miui9Calendar.toMonth();
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
        } else {
            miui9Calendar.toWeek();
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
        }
    }
    //回到今天
    public void today(View view){
        miui9Calendar.toToday();
    }
    //首页记事recyclerView加载方法
    public void loadNotes(){
        //下面是加载数据
        DatabaseFunctions databaseFunctions=new DatabaseFunctions(getActivity().getApplicationContext());
        //从数据库添加数据到界面
        List<DataBean> list1=new ArrayList<>();
        list1=databaseFunctions.getAllDataBean();
        //在日历上面标记出有记事的日期
        List<String> pointList = new ArrayList<String>();
        for(int i=0;i<list1.size();i++){
            pointList.add(list1.get(i).getDate());
        }
        InnerPainter innerPainter = (InnerPainter) miui9Calendar.getCalendarPainter();
        innerPainter.setPointList(pointList);
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
        //Toast.makeText(getActivity(),manager.findFirstVisibleItemPosition()+"",Toast.LENGTH_SHORT).show();

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
