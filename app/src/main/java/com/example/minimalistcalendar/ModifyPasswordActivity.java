package com.example.minimalistcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.minimalistcalendar.Network.DoPostLogin;
import com.example.minimalistcalendar.Network.DoPostModifyPassword;
import com.example.minimalistcalendar.Network.IshaveNetWork;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;

import es.dmoral.toasty.Toasty;

public class ModifyPasswordActivity extends AppCompatActivity {

    private EditText modify_account_number_edit;
    private EditText user_mark_edit;
    private EditText new_account_password_edit;
    private EditText confirm_new_account_password_edit;
    private Button modify_button;
    private TextView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        modify_account_number_edit=findViewById(R.id.modify_account_number_edit);
        user_mark_edit=findViewById(R.id.user_mark_edit);
        new_account_password_edit=findViewById(R.id.new_account_password_edit);
        confirm_new_account_password_edit=findViewById(R.id.confirm_new_account_password_edit);
        modify_button=findViewById(R.id.modify_button);
        back=findViewById(R.id.modify_activity_close);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOk()){
                    if(IshaveNetWork.getIsNetWork(ModifyPasswordActivity.this)!=0){
                        if(MySharedPreferences.getData("isLoginStatus",ModifyPasswordActivity.this).equals("true")&&
                                MySharedPreferences.getData("account_number",ModifyPasswordActivity.this).
                                        equals(modify_account_number_edit.getText().toString())){
                            //如果是登陆并且修改的账号就是登陆的账号的话
                            Toasty.warning(ModifyPasswordActivity.this,"该账号正在本机登陆，请先退出再修改密码",Toasty.LENGTH_SHORT).show();
                        }else{
                            Toasty.info(ModifyPasswordActivity.this,"请稍后...",Toasty.LENGTH_SHORT).show();
                            //当准备好的时候 开始网络请求
                            DoPostModifyPassword.modify(ModifyPasswordActivity.this,modify_account_number_edit.getText().toString(),
                                    new_account_password_edit.getText().toString(),user_mark_edit.getText().toString());
                        }
                    }else{
                        Toasty.warning(ModifyPasswordActivity.this,"当前无网络 无法登陆",Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public boolean isOk(){
        //返回是否准备好了
        if(modify_account_number_edit.getText().toString().isEmpty()){
            Toasty.warning(ModifyPasswordActivity.this,"未输入账号",Toasty.LENGTH_SHORT).show();
            return  false;
        }
        if(user_mark_edit.getText().toString().isEmpty()){
            Toasty.warning(ModifyPasswordActivity.this,"未输入标识",Toasty.LENGTH_SHORT).show();
            return  false;
        }
        if(new_account_password_edit.getText().toString().isEmpty()){
            Toasty.warning(ModifyPasswordActivity.this,"未输入新密码",Toasty.LENGTH_SHORT).show();
            return  false;
        }
        if(confirm_new_account_password_edit.getText().toString().isEmpty()){
            Toasty.warning(ModifyPasswordActivity.this,"未再次输入新密码",Toasty.LENGTH_SHORT).show();
            return  false;
        }
        if(!new_account_password_edit.getText().toString().equals(confirm_new_account_password_edit.getText().toString())){
            Toasty.warning(ModifyPasswordActivity.this,"两次输入新密码不一致",Toasty.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }
}