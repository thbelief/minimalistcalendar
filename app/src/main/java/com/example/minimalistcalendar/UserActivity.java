package com.example.minimalistcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.minimalistcalendar.EventBus.UpdateLoginEventBus;
import com.example.minimalistcalendar.Network.DoPostLogin;
import com.example.minimalistcalendar.Network.DoPostRegister;
import com.example.minimalistcalendar.Network.IshaveNetWork;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;

import org.greenrobot.eventbus.EventBus;

import es.dmoral.toasty.Toasty;

public class UserActivity extends AppCompatActivity {

    private TextView login_state;
    private EditText account_number_edit,account_password_edit;
    private Button login_bt,register_bt;
    private LinearLayout have_login,not_login;
    private TextView login_success_number,login_success_password;
    private Button exit_login_bt;
    private CheckBox remeber_password_cb;
    private TextView close;
    //查询登陆状态
    private String loginKey="isLoginStatus";
    private String numberKey="account_number";
    private String passwordKey="account_password";
    private String remeber_passwordKey="isRemeberPassword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        login_state=findViewById(R.id.login_state);
        account_number_edit=findViewById(R.id.account_number_edit);
        account_password_edit=findViewById(R.id.account_password_edit);
        login_bt=findViewById(R.id.login_bt);
        register_bt=findViewById(R.id.register_bt);

        have_login=findViewById(R.id.have_login);
        not_login=findViewById(R.id.not_login);
        login_success_number=findViewById(R.id.login_success_number);
        login_success_password=findViewById(R.id.login_success_password);
        exit_login_bt=findViewById(R.id.exit_login_bt);
        remeber_password_cb=findViewById(R.id.remeber_password_cb);
        close=findViewById(R.id.user_activity_close);

        loadSetting();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exit_login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySharedPreferences.saveData(loginKey,"false",UserActivity.this);
                //MySharedPreferences.saveData(numberKey,"nothing",UserActivity.this);
                //MySharedPreferences.saveData(passwordKey,"nothing",UserActivity.this);

                //退出登录则登录状态改变
                EventBus.getDefault().post(new UpdateLoginEventBus("updateLoginState"));

                finish();
            }
        });
        remeber_password_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    MySharedPreferences.saveData(remeber_passwordKey,"true",UserActivity.this);
                }else{
                    MySharedPreferences.saveData(remeber_passwordKey,"false",UserActivity.this);
                }
            }
        });
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOk()){
                    if(IshaveNetWork.getIsNetWork(UserActivity.this)!=0){
                        //登陆 在登陆里面如果成功了 就直接保存了Myshared...
                        DoPostLogin.login(UserActivity.this,account_number_edit.getText().toString(),
                                account_password_edit.getText().toString());
                        finish();
                    }else{
                        Toasty.warning(UserActivity.this,"当前无网络 无法登陆",Toasty.LENGTH_SHORT).show();
                    }
                }else{
                    Toasty.warning(UserActivity.this,"账号密码不完整！",Toasty.LENGTH_SHORT).show();
                }


            }
        });
        register_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOk()){
                    if(IshaveNetWork.getIsNetWork(UserActivity.this)!=0){
                        //注册
                        DoPostRegister.register(UserActivity.this,account_number_edit.getText().toString(),
                                account_password_edit.getText().toString());
                    }else{
                        Toasty.warning(UserActivity.this,"当前无网络 无法注册",Toasty.LENGTH_SHORT).show();
                    }
                }else{
                    Toasty.warning(UserActivity.this,"账号密码不完整！",Toasty.LENGTH_SHORT).show();
                }
            }
        });


    }
    //判断是否两个输入框都输入了数据
    public boolean isOk(){
        if(!account_number_edit.getText().toString().isEmpty()&&!account_password_edit.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }
    public void loadSetting(){
        //加载界面数据
        if(MySharedPreferences.getData(loginKey,UserActivity.this).equals("true")){
            login_state.setText("已登录");
            login_state.setTextColor(Color.GREEN);
            have_login.setVisibility(View.VISIBLE);
            not_login.setVisibility(View.GONE);
            login_success_number.setText(MySharedPreferences.getData(numberKey,UserActivity.this));
            login_success_password.setText(MySharedPreferences.getData(passwordKey,UserActivity.this));
        }else{
            //如果没有登陆的话
            login_state.setText("未登陆");
            login_state.setTextColor(Color.RED);
            have_login.setVisibility(View.GONE);
            not_login.setVisibility(View.VISIBLE);
            if(MySharedPreferences.getData(remeber_passwordKey,UserActivity.this).equals("true")&&!MySharedPreferences.getData(numberKey,UserActivity.this).equals("nothing")){
                remeber_password_cb.setChecked(true);
                //如果记住密码的话 直接填上去
                account_number_edit.setText(MySharedPreferences.getData(numberKey,UserActivity.this));
                account_password_edit.setText(MySharedPreferences.getData(passwordKey,UserActivity.this));
            }
        }
    }
}