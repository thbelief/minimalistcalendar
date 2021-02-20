package com.example.minimalistcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class WorkDayActivity extends AppCompatActivity {

    private TextView close_tv,save_tv;
    private SwitchButton is_open_function;
    private LinearLayout linearLayout;
    private SwitchButton sb_1,sb_2,sb_3,sb_4,sb_5,sb_6,sb_7;
    private String turnOnFunction="isOpenWorkDayFunction";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_day);
        //绑定控件
        close_tv=findViewById(R.id.work_day_activity_close);
        save_tv=findViewById(R.id.work_day_activity_save);
        is_open_function=findViewById(R.id.is_work_day_sb);
        linearLayout=findViewById(R.id.select_work_day_l);
        sb_1=findViewById(R.id.Mon_sb);
        sb_2=findViewById(R.id.Tue_sb);
        sb_3=findViewById(R.id.Wed_sb);
        sb_4=findViewById(R.id.Thur_sb);
        sb_5=findViewById(R.id.Fri_sb);
        sb_6=findViewById(R.id.Sat_sb);
        sb_7=findViewById(R.id.Sun_sb);

        //这里需要加载设置 （如果之前已经设置过的话 比如已经开启过功能设置过的）
        loadSettings();
        //关闭
        close_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //保存
        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOk()){
                    //两种情况 一种开启功能并且至少选择一个按钮
                    //第二种是关闭按钮 关闭功能 开启按钮并且没有选择一个也算是错的 无法保存
                    if(is_open_function.isChecked()){
                        //开启功能并且保存按钮
                        MySharedPreferences.saveData(turnOnFunction,"true",WorkDayActivity.this);
                        MySharedPreferences.saveData("Mon",isTrueOrFalse(1),WorkDayActivity.this);
                        MySharedPreferences.saveData("Tue",isTrueOrFalse(2),WorkDayActivity.this);
                        MySharedPreferences.saveData("Wed",isTrueOrFalse(3),WorkDayActivity.this);
                        MySharedPreferences.saveData("Thur",isTrueOrFalse(4),WorkDayActivity.this);
                        MySharedPreferences.saveData("Fri",isTrueOrFalse(5),WorkDayActivity.this);
                        MySharedPreferences.saveData("Sat",isTrueOrFalse(6),WorkDayActivity.this);
                        MySharedPreferences.saveData("Sun",isTrueOrFalse(7),WorkDayActivity.this);
                    }else{
                        //关闭此功能
                        MySharedPreferences.saveData(turnOnFunction,"false",WorkDayActivity.this);
                        linearLayout.setVisibility(View.GONE);
                    }
                    Toasty.success(WorkDayActivity.this,"保存成功",Toasty.LENGTH_SHORT).show();
                    //Toast.makeText(WorkDayActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toasty.error(WorkDayActivity.this,"保存失败",Toasty.LENGTH_SHORT).show();
                    //Toast.makeText(WorkDayActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        is_open_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_open_function.isChecked()){
                    linearLayout.setVisibility(View.VISIBLE);
                }else{
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
    }
    //存储的时候看是true还是false 返回字符串
    public String isTrueOrFalse(int i){
        List<SwitchButton> lists=new ArrayList<SwitchButton>();
        lists.add(sb_1);
        lists.add(sb_2);
        lists.add(sb_3);
        lists.add(sb_4);
        lists.add(sb_5);
        lists.add(sb_6);
        lists.add(sb_7);
        //因为i是1~7 而数组的小标是从0开始的
        if(lists.get(i-1).isChecked()){
            return "true";
        }
        return "false";
    }
    //取出来的时候看字符串 返回boolean
    public Boolean isTrue(int i){
        List<String> lists=new ArrayList<String>();
        lists.add("Mon");
        lists.add("Tue");
        lists.add("Wed");
        lists.add("Thur");
        lists.add("Fri");
        lists.add("Sat");
        lists.add("Sun");
        //因为i是1~7 而数组的小标是从0开始的
        if(MySharedPreferences.getData(lists.get(i-1),WorkDayActivity.this).equals("true")){
            return true;
        }
        return false;
    }
    //判断是否具有保存成功条件
    public boolean isOk(){
        if(is_open_function.isChecked()){
            //必须选择一个 如果一个都不选就直接提示无法保存
            if(sb_1.isChecked()){
                return true;
            }
            if(sb_2.isChecked()){
                return true;
            }
            if(sb_3.isChecked()){
                return true;
            }
            if(sb_4.isChecked()){
                return true;
            }
            if(sb_5.isChecked()){
                return true;
            }
            if(sb_6.isChecked()){
                return true;
            }
            if(sb_7.isChecked()){
                return true;
            }
            return false;
        }
        return true;
    }
    public void loadSettings(){
        //加载设置进来
        String isTurnOnFunction= MySharedPreferences.getData(turnOnFunction,WorkDayActivity.this);
        //如果返回为nothing说明没有设置过 不用管 (当查询的数据没有的时候默认返回nothing，自己设置的 )
        if (!isTurnOnFunction.equals("nothing")&&isTurnOnFunction.equals("true")){
            is_open_function.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);
            sb_1.setChecked(isTrue(1));
            sb_2.setChecked(isTrue(2));
            sb_3.setChecked(isTrue(3));
            sb_4.setChecked(isTrue(4));
            sb_5.setChecked(isTrue(5));
            sb_6.setChecked(isTrue(6));
            sb_7.setChecked(isTrue(7));
        }
    }
}