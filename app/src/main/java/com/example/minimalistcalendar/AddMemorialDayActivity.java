package com.example.minimalistcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.DateBase.DatabaseFunctions;
import com.example.minimalistcalendar.Util.AlarmManagerUtil;
import com.example.minimalistcalendar.Util.DateUtil;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.kyleduo.switchbutton.SwitchButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AddMemorialDayActivity extends AppCompatActivity {
    private TextView textView;
    private SwitchButton switchButton;
    //已删除
//    private MaterialCheckBox materialCheckBox1;
//    private MaterialCheckBox materialCheckBox2;
    private TextView textView_sp_priority;
    private TextView textView_close;
    private TextView textView_save;
    private EditText editText;
    private EditText editText_content;
    private TextView select_alarmt_time;

    //需要保存的数据~
    private String title="无";
    //通过这个来区别记事或者纪念日
    private String degree="MemorialDay";
    private String degreeColor="无";
    private int year=0;
    private int month=0;
    private int day=0;
    private int isAlarm=0;
    private String alarmRemind="选择提醒时间";//提前多久提醒 (默认设置 防止非空判断)
    private String description="无";
    //用来判断是保存还是更新
    private int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memorial_day);

        //选择时间的控件
        textView=(TextView) findViewById(R.id.sp_dateselect);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickDialog(DateType.TYPE_YMD,false);
            }
        });

        //是否提醒 提醒的话直接打开需要选择提前多久提醒的界面
        switchButton=(SwitchButton) findViewById(R.id.sp_alarm_swicthbt);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout=(LinearLayout) findViewById(R.id.sp_alarm_time);
                if(switchButton.isChecked()){
                    Toasty.warning(AddMemorialDayActivity.this,"确认开启自启动\n否则可能失效",Toasty.LENGTH_SHORT).show();
                    linearLayout.setVisibility(View.VISIBLE);
                    isAlarm=1;
                }else{
                    linearLayout.setVisibility(View.GONE);
                    isAlarm=0;
                }

            }
        });

        //如果要提醒的话 展开选择提醒时间的界面
        select_alarmt_time=findViewById(R.id.select_alarmtime_memorial);
        select_alarmt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择时间然后存到alarm的remind中去
                showDatePickDialog(DateType.TYPE_YMDHM,true);
            }
        });

        textView_close=(TextView)findViewById(R.id.sp_close);
        textView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                //name相当于一个key,content是返回的内容
                data.putExtra("data","从纪念日页返回了主页面");
                //resultCode是返回码,用来确定是哪个页面传来的数据，这里设置返回码是2
                //这个页面传来数据,要用到下面这个方法setResult(int resultCode,Intent data)
                setResult(3,data);
                //结束掉这个activity
                finish();
            }
        });
        //保存
        textView_save=(TextView)findViewById(R.id.sp_save);
        editText=(EditText)findViewById(R.id.sp_name);
        editText_content=(EditText) findViewById(R.id.sp_content_edit);
        textView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title=editText.getText().toString();
                description=editText_content.getText().toString();
                if(isFillData()){
                    DataBean dataBean=new DataBean();
                    dataBean=saveDataToBean();
                    //Log.d("AddNoteActivity",dataBean.toString());
                    //Toast.makeText(AddNoteActivity.this,title,Toast.LENGTH_SHORT).show();
                    //将数据写入数据库
                    DatabaseFunctions databaseFunction=new DatabaseFunctions(getApplicationContext());
                    if(id==0){
                        databaseFunction.insertData(dataBean);
                        Toasty.success(AddMemorialDayActivity.this,"新建纪念日成功！",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(AddMemorialDayActivity.this,"新建纪念日成功！",Toast.LENGTH_SHORT).show();
                        Intent data = new Intent();
                        //name相当于一个key,content是返回的内容
                        data.putExtra("data","从纪念日页返回了主页面");
                        //resultCode是返回码,用来确定是哪个页面传来的数据，这里设置返回码是2
                        //这个页面传来数据,要用到下面这个方法setResult(int resultCode,Intent data)
                        setResult(3,data);
                        //设置闹钟 必须放后面 因为只有存了之后才有id
                        if(dataBean.getIsAlarm()==1){
                            //Log.d("MainActivity",databaseFunction.getDataByTitle(title).getId()+""+databaseFunction.getDataByTitle(title).getTitle());
                            AlarmManagerUtil.setAlarmInCreate(databaseFunction.getDataByTitle(title,degree,degreeColor,alarmRemind,description),AddMemorialDayActivity.this,1);
                        }
                    }else{
                        //更新
                        databaseFunction.updateDataById(id,dataBean);
                        Toasty.success(AddMemorialDayActivity.this,"更新纪念日成功！",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(AddMemorialDayActivity.this,"更新纪念日成功！",Toast.LENGTH_SHORT).show();
                        //设置闹钟
                        if(dataBean.getIsAlarm()==1){
                            //要先取消上一次的闹钟
                            AlarmManagerUtil.cancelAlarm(AddMemorialDayActivity.this,id);
                            AlarmManagerUtil.setAlarmInCreate(databaseFunction.getDataById(id),AddMemorialDayActivity.this,1);
                        }
                    }
                    //结束掉这个activity
                    finish();
                }
            }
        });

        //从详情页编辑进来的时候要记载数据
        //直接使用getIntent().getStringExtra("type").equals("MemorialDayEdit")会报错 原因未知 Alt+enter解决
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(Objects.equals(getIntent().getStringExtra("type"), "MemorialDayEdit")){
                DataBean DB = (DataBean) getIntent().getSerializableExtra("DataBean");
                //Toast.makeText(AddMemorialDayActivity.this,"已进入",Toast.LENGTH_SHORT).show();
                id=DB.getId();
                TextView m_title_bar=(TextView)findViewById(R.id.m_title_bar);
                m_title_bar.setText("修改纪念日");
                textView_save.setText("更新");
                editText.setText(DB.getTitle());
                //year判断 防止判断还未选择日期
                year=DB.getYear();
                month=DB.getMonth();
                day=DB.getDay();
                textView.setText(DB.getYear()+"-"+DB.getMonth()+"-"+DB.getDay());
                if(DB.getIsAlarm()==1){
                    isAlarm=1;
                    switchButton.setChecked(true);
                    LinearLayout linearLayout=(LinearLayout) findViewById(R.id.sp_alarm_time);
                    linearLayout.setVisibility(View.VISIBLE);
                    //加载闹钟提醒的详情
                    select_alarmt_time.setText(DB.getAlarmRemind());
//                    if(DB.getAlarmRemind().equals("提前一周")){
//                        materialCheckBox1.setChecked(true);
//                        materialCheckBox2.setChecked(false);
//                    }else{
//                        materialCheckBox1.setChecked(false);
//                        materialCheckBox2.setChecked(true);
//                    }
                }
                editText_content.setText(DB.getDescription());
            }
        }

    }
    //日期选择控件展示 isAlarm用来判断是不是闹钟选择时间控件
    private void showDatePickDialog(DateType type,boolean isAlarm) {
        DatePickDialog dialog = new DatePickDialog(this);
        //设置上下年分限制
        dialog.setYearLimt(5);
        //设置标题
        dialog.setTitle("选择时间");
        //设置类型
        dialog.setType(type);
        //设置消息体的显示格式，日期格式
        if(!isAlarm){
            dialog.setMessageFormat("yyyy-MM-dd");
        }else{
            //24小时制
            dialog.setMessageFormat("yyyy-MM-dd-HH-mm");
        }
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                if(!isAlarm){
                    //获取年月日
                    year=Integer.parseInt(new SimpleDateFormat("yyyy").format(date)) ;
                    month=Integer.parseInt(new SimpleDateFormat("MM").format(date));
                    day=Integer.parseInt(new SimpleDateFormat("dd").format(date));
                    //确定修改日期之后修改button的显示效果
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String time = sdf.format(date);
                    textView.setText(time);
                }else{
                    //确定修改日期之后修改button的显示效果
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                    String time = sdf.format(date);
                    select_alarmt_time.setText(time);
                    //同时存储时间
                    alarmRemind=time;
                }

            }
        });
        dialog.show();
    }
    //保存的数据存到DateBean中
    public DataBean saveDataToBean(){
        DataBean dataBean=new DataBean();
        dataBean.setTitle(title);
        dataBean.setDegree(degree);
        dataBean.setDegreeColor(degreeColor);
        dataBean.setYear(year);
        dataBean.setMonth(month);
        dataBean.setDay(day);
        dataBean.setIsAlarm(isAlarm);
        dataBean.setAlarmRemind(alarmRemind);
        dataBean.setDescription(description);
        return dataBean;
    }
    //判断所有的数据都填完了。
    public boolean isFillData(){
        //只要一个没写都不能保存
        if(title.isEmpty()){
            Toasty.warning(AddMemorialDayActivity.this,"标题未填，无法保存！",Toast.LENGTH_SHORT).show();
            //Toast.makeText(AddMemorialDayActivity.this,"标题未填，无法保存！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(year==0){
            Toasty.warning(AddMemorialDayActivity.this,"日期未填，无法保存！",Toast.LENGTH_SHORT).show();
            //Toast.makeText(AddMemorialDayActivity.this,"日期未填，无法保存！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(isAlarm==1&&alarmRemind.equals("选择提醒时间")){
            Toasty.warning(AddMemorialDayActivity.this,"提醒时间未选择，无法保存！",Toast.LENGTH_SHORT).show();
            //Toast.makeText(AddMemorialDayActivity.this,"提醒时间未选择，无法保存！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(isAlarm==1&&!alarmRemind.equals("选择提醒时间")){
            //alarmRemind默认值就是选择提醒时间 如果还是这个 说明没有选择提醒时间
            //如果选择提醒时间错误 比如选择的时间是在现在的时间之前的就无法提醒 所以直接报错
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            Date now=new Date();
            Date alarmTime=null;
            try {
                alarmTime=sdf.parse(alarmRemind);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(!alarmTime.after(now)){
                Toasty.warning(AddMemorialDayActivity.this,"提醒时间无法选择在目前时间之前，无法保存！",Toast.LENGTH_SHORT).show();
                //Toast.makeText(AddMemorialDayActivity.this,"提醒时间无法选择在目前时间之前，无法保存！",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}