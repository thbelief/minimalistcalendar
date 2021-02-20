package com.example.minimalistcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.Util.AlarmManagerUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.dmoral.toasty.Toasty;


public class NoteDetailsActivity extends AppCompatActivity {

    //item的id
    private int item_id;
    private DatabaseFunctions databaseFunctions;
    private DataBean          dataBean;
    //控件
    private TextView          degree,title,date,alarm,content;
    ImageButton ib_back,ib_delete;
    FloatingActionButton fab_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_details);
        //获取从MainActivity跳转时传入的id
        item_id=Integer.parseInt(getIntent().getStringExtra("id"));
        databaseFunctions=new DatabaseFunctions(getApplicationContext());
        dataBean=databaseFunctions.getDataById(item_id);

        title=findViewById(R.id.note_details_title);
        degree=findViewById(R.id.note_details_degree);
        date=findViewById(R.id.note_details_date);
        alarm=findViewById(R.id.note_details_alarm);
        content=findViewById(R.id.note_details_content);
        ib_back=findViewById(R.id.note_details_back);
        ib_delete=findViewById(R.id.note_details_delete);
        fab_edit=findViewById(R.id.note_details_edit);
        //Log.d("NoteDetailsActivity",dataBean.toString());
        loadData();
        //返回
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回到主页面
                finish();
            }
        });
        //删除
        ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除该id的闹钟(不起作用 因为context无法一样 )
                AlarmManagerUtil.cancelAlarm(NoteDetailsActivity.this,dataBean.getId());
                Toasty.success(NoteDetailsActivity.this,"删除成功",Toasty.LENGTH_SHORT).show();
                //删除该id的数据
                databaseFunctions.deleteDataById(dataBean.getId());
                finish();
            }
        });
        //编辑
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataBean.getDegree().equals("MemorialDay")){
                    Intent intent=new Intent(NoteDetailsActivity.this,AddMemorialDayActivity.class);
                    intent.putExtra("DataBean",dataBean);
                    intent.putExtra("type","MemorialDayEdit");
                    startActivity(intent);
                    finish();
                }else{
                    //进入记事编辑页面
                    Intent intent=new Intent(NoteDetailsActivity.this,AddNoteActivity.class);
                    intent.putExtra("DataBean",dataBean);
                    intent.putExtra("type","NoteEdit");
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    //将databean的数据加载到页面上
    public void loadData(){
        title.setText(dataBean.getTitle());
        date.setText(dataBean.getYear()+"年"+
                dataBean.getMonth()+"月"+dataBean.getDay()+"日");
        if(dataBean.getDegree().equals("MemorialDay")){
            degree.setText("特殊日期");
        }else{
            degree.setText(dataBean.getDegree());
        }
        if(dataBean.getIsAlarm()==0){
            alarm.setText("不提醒");
        }else{
            alarm.setText(dataBean.getAlarmRemind());
        }
        content.setText(dataBean.getDescription());
    }
}