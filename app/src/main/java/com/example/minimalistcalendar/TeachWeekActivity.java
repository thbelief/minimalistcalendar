package com.example.minimalistcalendar;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.kyleduo.switchbutton.SwitchButton;
import com.nlf.calendar.Solar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class TeachWeekActivity extends AppCompatActivity {

    private TextView close_tv,save_tv;
    private SwitchButton is_teachWeek_sb;
    private LinearLayout select_linearLayout;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach_week);

        //绑定
        close_tv=findViewById(R.id.teach_week_activity_close);
        save_tv=findViewById(R.id.teach_week_activity_save);
        is_teachWeek_sb=findViewById(R.id.is_teach_week_sb);
        select_linearLayout=findViewById(R.id.select_teach_week_l);
        editText=findViewById(R.id.current_teach_week);

        //这里需要加载设置 （如果之前已经设置过的话 比如已经开启过功能设置过的）
        loadSettings();
        //关闭当前页面
        close_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Toast.makeText(TeachWeekActivity.this,MySharedPreferences.getData("weekStartDate",TeachWeekActivity.this),Toast.LENGTH_SHORT).show();
        //保存
        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOk()){
                    //有两种情况 第一是 开启功能 则必须是选中按钮 并且输入框填入数字
                    //第二种是 关闭按钮 关闭功能 如果开启了按钮没有填数字也是错误的
                    if(is_teachWeek_sb.isChecked()){
                        //意味着必须填入edittext的数字
                        MySharedPreferences.saveData("isTurnOnFunction","true",TeachWeekActivity.this);
                        String editTextContent=editText.getText().toString();
                        MySharedPreferences.saveData("weekStartDate",calculateStartDate(Integer.parseInt(editTextContent)),TeachWeekActivity.this);
                    }else{
                        //意味着关闭此功能
                        MySharedPreferences.saveData("isTurnOnFunction","false",TeachWeekActivity.this);
                        select_linearLayout.setVisibility(View.GONE);
                    }
                    Toasty.success(TeachWeekActivity.this,"保存成功",Toasty.LENGTH_SHORT).show();
                    //Toast.makeText(TeachWeekActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toasty.error(TeachWeekActivity.this,"保存失败",Toasty.LENGTH_SHORT).show();
                    //Toast.makeText(TeachWeekActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击了开启跟功能的switchbutton
        is_teachWeek_sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_teachWeek_sb.isChecked()){
                    select_linearLayout.setVisibility(View.VISIBLE);
                }else{
                    select_linearLayout.setVisibility(View.GONE);
                }
            }
        });

    }
    public void loadSettings(){
        //加载设置进来
        String isTurnOnFunction=MySharedPreferences.getData("isTurnOnFunction",TeachWeekActivity.this);
        //如果返回为nothing说明没有设置过 不用管 (当查询的数据没有的时候默认返回nothing，自己设置的 )
        if (!isTurnOnFunction.equals("nothing")&&isTurnOnFunction.equals("true")){
            is_teachWeek_sb.setChecked(true);
            select_linearLayout.setVisibility(View.VISIBLE);
            //int类型的参数必须“”
            editText.setText(weekNum()+"");
        }
    }
    //计算起始周次的日期
    public String calculateStartDate(int t){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date today=null;
        String startDate = null;
        Calendar calendar=Calendar.getInstance();
        try {
            today=simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            //startDate=simpleDateFormat.format(new Date(today.getTime()-24*60*60*1000*t*7));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(today);
        calendar.add(Calendar.DATE,-(t*7));
        Date date1=calendar.getTime();
        return simpleDateFormat.format(date1);
    }
    //根据存储的教学周起始日期 计算当前具体的教学周周次
    public int weekNum(){
        String startDate=MySharedPreferences.getData("weekStartDate",TeachWeekActivity.this);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Toast.makeText(TeachWeekActivity.this,"测试"+startDate,Toast.LENGTH_SHORT).show();
        Date today=null;
        Date startDateDay=null;
        try {
            today=simpleDateFormat.parse(simpleDateFormat.format(new Date()));
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
    //判断是否具有保存成功条件
    public boolean isOk(){
        if(is_teachWeek_sb.isChecked()){
            //当sbcheck的时候必须填写数字 没有的话 无法保存
            //如果没开的时候则是直接看作是关闭功能
            if(!(editText.getText().length()==0)){
                return true;
            }
            return false;
        }
        return true;
    }



}