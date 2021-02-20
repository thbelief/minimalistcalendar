package com.example.minimalistcalendar.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minimalistcalendar.Adapter.ItemNullRecyclerViewAdapter;
import com.example.minimalistcalendar.Adapter.RecycleAdapter;
import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.EventBus.UpdateNoteEventBus;
import com.example.minimalistcalendar.Manager.TopLayoutManager;
import com.example.minimalistcalendar.NoteDetailsActivity;
import com.example.minimalistcalendar.R;
import com.example.minimalistcalendar.Util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NoteFragment extends Fragment {
    //recyclerView
    private RecyclerView   recyclerView;
    private RecycleAdapter recycleAdapter;
    //是否获取数据更新
    private boolean isUpdateData = false;
    //按照日期排好序的databean
    private List<DataBean> listsortbydatabean;
    //本周 本月 全部 的筛选
    private TextView week_tv,month_tv,all_tv;
    private View week_v,month_v,all_v;
    //选择的事本周还是本月还是全部 依次为 0 1 2
    private int isWho=0;//默认是0 选择本周的展示
    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //进入Fragment的时候
//        if(enter&&!isUpdateData){
//            isUpdateData=true;
//        }else{
//            isUpdateData=false;
//        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onPause() {
        super.onPause();
//        isUpdateData=false;
    }

    @Override
    public void onResume() {
        //数据更新的时候
        if(isUpdateData){
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
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }
    //EventBus响应函数
    @Subscribe
    public void onEventMainThread(UpdateNoteEventBus event) {
        //传递消息过来说明该更新UI了。 和DateFragment都必须更新
        if(event.getMsg().equals("update")){
            loadNotes();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑EventBUS
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        //recyclerView加载
         /*
        与ListView效果对应的可以通过LinearLayoutManager来设置
        与GridView效果对应的可以通过GridLayoutManager来设置
        与瀑布流对应的可以通过StaggeredGridLayoutManager来设置
        */
        recyclerView = getActivity().findViewById(R.id.note_fragment_recyclerview);

        week_tv=getActivity().findViewById(R.id.note_fragment_week);
        month_tv=getActivity().findViewById(R.id.note_fragment_month);
        all_tv=getActivity().findViewById(R.id.note_fragment_all);
        week_v=getActivity().findViewById(R.id.note_fragment_week_line);
        month_v=getActivity().findViewById(R.id.note_fragment_month_line);
        all_v=getActivity().findViewById(R.id.note_fragment_all_line);
        //点击事件
        week_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlLine(0);
                isWho=0;
                loadNotes();
            }
        });
        month_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlLine(1);
                isWho=1;
                loadNotes();
            }
        });
        all_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlLine(2);
                isWho=2;
                loadNotes();
            }
        });

        isUpdateData=true;
        loadNotes();
    }
    //传入0 1 2 控制滑动条的显示 week month all
    public void controlLine(int temp){
        switch(temp){
            case 0:
                week_v.setVisibility(View.VISIBLE);
                month_v.setVisibility(View.INVISIBLE);
                all_v.setVisibility(View.INVISIBLE);
                break;
            case 1:
                week_v.setVisibility(View.INVISIBLE);
                month_v.setVisibility(View.VISIBLE);
                all_v.setVisibility(View.INVISIBLE);
                break;
            case 2:
                week_v.setVisibility(View.INVISIBLE);
                month_v.setVisibility(View.INVISIBLE);
                all_v.setVisibility(View.VISIBLE);
                break;
        }
    }
    // 筛选出合适的集合
    public void filterData(){
        //通过listsortbydatabean 以及isWho来筛选
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //今天
        try {
            Date todayBegin = simpleDateFormat.parse(simpleDateFormat.format(DateUtil.getDayBegin()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //本周开始时间以及结束时间
        Date weekStart= null;
        try {
            weekStart = simpleDateFormat.parse(simpleDateFormat.format(DateUtil.getBeginDayOfWeek()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date weekEnd= null;
        try {
            weekEnd = simpleDateFormat.parse(simpleDateFormat.format(DateUtil.getEndDayOfWeek()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //本月开始时间以及结束时间
        Date monthStart= null;
        try {
            monthStart = simpleDateFormat.parse(simpleDateFormat.format(DateUtil.getBeginDayOfMonth()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date monthEnd= null;
        try {
            monthEnd = simpleDateFormat.parse(simpleDateFormat.format(DateUtil.getEndDayOfMonth()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<DataBean> dataBeanList = new ArrayList<DataBean>();
        for(int i=0;i<listsortbydatabean.size();i++){
            dataBeanList.add(listsortbydatabean.get(i));
        }
        listsortbydatabean.clear();

        //Log.d("MainActivity","jinlaile "+listsortbydatabean.toString());
        if(isWho==0){
            //筛选出本周的
            for(int i=0;i<dataBeanList.size();i++){
                //Log.d("MainActivity"," 测试用例"+monthEnd);
                if(weekStart.compareTo(dataBeanList.get(i).getDateIsDate())<=0&&weekEnd.compareTo(dataBeanList.get(i).getDateIsDate())>=0){
                    //本周的
                    listsortbydatabean.add(dataBeanList.get(i));
                }
            }
        }else if(isWho==1){
            //筛选出本月的
            for(int i=0;i<dataBeanList.size();i++){
                if(monthStart.compareTo(dataBeanList.get(i).getDateIsDate())<=0&&monthEnd.compareTo(dataBeanList.get(i).getDateIsDate())>=0){
                    //本周的
                    listsortbydatabean.add(dataBeanList.get(i));
                }
            }
        }else{
            //全部 不需要筛选
            for(int i=0;i<dataBeanList.size();i++){
                listsortbydatabean.add(dataBeanList.get(i));
            }
        }


    }
    //recyclerView加载方法
    public void loadNotes(){
        //默认是本周的

        DatabaseFunctions databaseFunctions=new DatabaseFunctions(getActivity().getApplicationContext());
        //从数据库添加数据到界面
        List<DataBean> list1=new ArrayList<>();
        list1=databaseFunctions.getAllDataBean();
        //在日历上面标记出有记事的日期
        List<String> pointList = new ArrayList<String>();
        for(int i=0;i<list1.size();i++){
            pointList.add(list1.get(i).getDate());
        }
        //对于日期排序
        DateUtil.sortListByDate(list1);
        //将日期排序之后的数据保存下来
        listsortbydatabean=list1;
        //排完序之后筛选
        filterData();
        LinearLayoutManager manager=new TopLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        //判断是否记事列表为空 如果为空的话 切换为null adapter
        if(list1.isEmpty()){
            //Toast.makeText(getActivity(),"为空",Toast.LENGTH_SHORT).show();
            ItemNullRecyclerViewAdapter itemNullRecyclerViewAdapter=new ItemNullRecyclerViewAdapter(getActivity());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(itemNullRecyclerViewAdapter);
        }else{
            recycleAdapter = new RecycleAdapter(listsortbydatabean,getActivity());
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
