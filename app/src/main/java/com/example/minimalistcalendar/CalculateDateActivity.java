package com.example.minimalistcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.nlf.calendar.Solar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculateDateActivity extends AppCompatActivity {

    private TextView back,solar_sd,lunar_sd,first_sd,last_sd,distance_days,basic_sd,finally_date;
    private EditText input_distance_days_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_date);

        //绑定控件
        back=findViewById(R.id.calculate_date_activity_close);
        solar_sd=findViewById(R.id.solar_select_date);
        lunar_sd=findViewById(R.id.lunar_select_date);
        first_sd=findViewById(R.id.first_day_select_date);
        last_sd=findViewById(R.id.last_day_select_date);
        distance_days=findViewById(R.id.distance_date);
        basic_sd=findViewById(R.id.basic_select_date);
        finally_date=findViewById(R.id.finally_date);
        input_distance_days_num=findViewById(R.id.input_day_num);

        //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //阳历选择时间的点击事件
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        solar_sd.setText(simpleDateFormat.format(new Date()));
        lunar_sd.setText(Solar.fromDate(new Date()).getLunar().toString());
        solar_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(CalculateDateActivity.this,"ceshi",Toast.LENGTH_SHORT).show();
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(CalculateDateActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        solar_sd.setText(simpleDateFormat.format(date));
                        //同时修改阴历显示
                        lunar_sd.setText(Solar.fromDate(date).getLunar().toString());
                    }
                }).setOutSideCancelable(true).setTitleText("选择时间").setTextColorCenter(getResources().
                        getColor(R.color.colorPrimary)).build();
                pvTime.show();
            }
        });
        //阴历的选择时间点击事件
        lunar_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(CalculateDateActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        solar_sd.setText(simpleDateFormat.format(date));
                        //同时修改阴历显示
                        lunar_sd.setText(Solar.fromDate(date).getLunar().toString());
                    }
                }).setOutSideCancelable(true).setTitleText("选择时间").setTextColorCenter(getResources().
                        getColor(R.color.colorPrimary)).setLunarCalendar(true).build();
                pvTime.show();
            }
        });
        //日期间隔的计算
        Date today_date=new Date();
        //默认都是今天
        first_sd.setText(simpleDateFormat.format(today_date));
        last_sd.setText(simpleDateFormat.format(today_date));
        distance_days.setText("0天");
        first_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(CalculateDateActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        first_sd.setText(simpleDateFormat.format(date));
                        distance_days.setText(getDistanceDays()+"天");
                    }
                }).setOutSideCancelable(true).setTitleText("选择时间").setTextColorCenter(getResources().
                        getColor(R.color.colorPrimary)).build();
                pvTime.show();
            }
        });
        last_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(CalculateDateActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        last_sd.setText(simpleDateFormat.format(date));
                        distance_days.setText(getDistanceDays()+"天");
                    }
                }).setOutSideCancelable(true).setTitleText("选择时间").setTextColorCenter(getResources().
                        getColor(R.color.colorPrimary)).build();
                pvTime.show();
            }
        });
        //日期推算
        basic_sd.setText(simpleDateFormat.format(today_date));
        basic_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerView pvTime = new TimePickerBuilder(CalculateDateActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        basic_sd.setText(simpleDateFormat.format(date));
                    }
                }).setOutSideCancelable(true).setTitleText("选择时间").setTextColorCenter(getResources().
                        getColor(R.color.colorPrimary)).build();
                pvTime.show();
            }
        });
        input_distance_days_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){
                    Solar s=null;
                    try {
                        s=Solar.fromDate(simpleDateFormat.parse(basic_sd.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    finally_date.setText(s.next(Integer.parseInt(editable.toString())).toYmd());
                }else{
                    finally_date.setText("");
                }

            }
        });

    }
    public int getDistanceDays(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date fisrt=null;
        try {
            fisrt=simpleDateFormat.parse(first_sd.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date last=null;
        try {
            last=simpleDateFormat.parse(last_sd.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getDistanceNum(fisrt,last);
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
}