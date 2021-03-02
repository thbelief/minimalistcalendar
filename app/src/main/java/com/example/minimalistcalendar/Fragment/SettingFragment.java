package com.example.minimalistcalendar.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.minimalistcalendar.AboutActivity;
import com.example.minimalistcalendar.CalculateDateActivity;
import com.example.minimalistcalendar.EventBus.UpdateLoginEventBus;
import com.example.minimalistcalendar.EventBus.UpdateNoteEventBus;
import com.example.minimalistcalendar.ModifyPasswordActivity;
import com.example.minimalistcalendar.Network.DoPostDownload;
import com.example.minimalistcalendar.Network.DoPostUpLoad;
import com.example.minimalistcalendar.Network.IshaveNetWork;
import com.example.minimalistcalendar.R;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.example.minimalistcalendar.TeachWeekActivity;
import com.example.minimalistcalendar.UserActivity;
import com.example.minimalistcalendar.Util.LoginUtil;
import com.example.minimalistcalendar.WorkDayActivity;
import com.kyleduo.switchbutton.SwitchButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class SettingFragment extends Fragment {

    private TextView teachWeek_tv;
    private TextView workDay_tv;
    private TextView calculateDate_tv;
    private TextView user_tv;
    private TextView about_tv;
    private TextView website_tv;
    private TextView modify_password_tv;
    private SwitchButton display_weather_sb;
    private SwitchButton synchronize_sb;
    //QQ登陆点击
    private ImageView qqImage;
    private TextView qqName;
    //初始化腾讯服务
    private  Tencent tencent;
    //调用SDK已经封装好的接口时，例如：登录、快速支付登录、应用分享、应用邀请等接口，需传入该回调的实例。
    private IUiListener listener;
    //存储的开关key
    private String display_weather_key="isDisplayWeather";
    private String synchronize_key="isSynchronize";
    private String isLogin_key="isLoginStatus";

    //上传 下载
    private TextView up_tv,download_tv;
    //登陆状态
    private TextView login_state;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //注册EventBus 最好加上判断 因为fragment生命周期后面还会在调用这个create 免得重复注册报错
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑EventBUS
        EventBus.getDefault().unregister(this);
    }
    //EventBus响应函数 这里必须使用 ThreadMode.MAIN因为有可能是其它线程发的post 而默认的posting只能在同一线程收发
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateLoginEventBus event) {
        //传递消息过来说明该更新UI了。
        if(event.getMsg().equals("updateLoginState")){
            String isLoginState=MySharedPreferences.getData(isLogin_key,getActivity());
            if(!isLoginState.equals("nothing")&&isLoginState.equals("true")){
                login_state.setTextColor(Color.GREEN);
                login_state.setText("已登录");
                synchronize_sb.setEnabled(true);
            }else{
                login_state.setTextColor(Color.RED);
                login_state.setText("未登陆");
                synchronize_sb.setChecked(false);
                synchronize_sb.setEnabled(false);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        teachWeek_tv=getActivity().findViewById(R.id.teach_week_tv);
        workDay_tv=getActivity().findViewById(R.id.work_day_tv);
        calculateDate_tv=getActivity().findViewById(R.id.calculate_date_tv);
        user_tv=getActivity().findViewById(R.id.user_tv);
        display_weather_sb=getActivity().findViewById(R.id.display_weather_sb);
        synchronize_sb=getActivity().findViewById(R.id.synchronize_sb);
        login_state=getActivity().findViewById(R.id.settings_fragement_login_state);
        about_tv=getActivity().findViewById(R.id.aboutme_tv);
        website_tv=getActivity().findViewById(R.id.website_tv);
        modify_password_tv=getActivity().findViewById(R.id.modify_password_tv);

        //QQ控件绑定事件
        qqImage=getActivity().findViewById(R.id.login);
        qqName=getActivity().findViewById(R.id.login_name);

        modify_password_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开修改密码页面
                Intent intent = new Intent(getActivity(), ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });
        website_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.thbelief.xyz");    //设置跳转的网站
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        about_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开关于页面
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        teachWeek_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开教学周设置的页面
                Intent intent = new Intent(getActivity(), TeachWeekActivity.class);
                startActivity(intent);
            }
        });
        workDay_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开工作日界面
                Intent intent = new Intent(getActivity(), WorkDayActivity.class);
                startActivity(intent);
            }
        });
        calculateDate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开日期计算页面
                Intent intent = new Intent(getActivity(), CalculateDateActivity.class);
                startActivity(intent);
            }
        });
        //账号界面
        user_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开账号界面
                Intent intent = new Intent(getActivity(), UserActivity.class);
                startActivity(intent);
            }
        });
        //展示天气的按钮点击事件
        display_weather_sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IshaveNetWork.getIsNetWork(getActivity())!=0){
                    //随着选中状态修改存储的设置
                    if(display_weather_sb.isChecked()){
                        Toasty.info(getActivity(),"显示天气已打开",Toasty.LENGTH_SHORT).show();
                        MySharedPreferences.saveData(display_weather_key,"true",getActivity());
                    }else{
                        Toasty.info(getActivity(),"显示天气已关闭",Toasty.LENGTH_SHORT).show();
                        MySharedPreferences.saveData(display_weather_key,"false",getActivity());
                    }
                }else{
                    Toasty.warning(getActivity(),"当前无网络 无法打开天气",Toasty.LENGTH_SHORT).show();
                }
            }
        });
        //自动同步按钮的点击事件
        synchronize_sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginUtil.isLogin(getActivity())){
                    if(IshaveNetWork.getIsNetWork(getActivity())!=0){
                        //随着选中状态修改存储的设置
                        if(synchronize_sb.isChecked()){
                            Toasty.info(getActivity(),"自动同步已打开",Toasty.LENGTH_SHORT).show();
                            MySharedPreferences.saveData(synchronize_key,"true",getActivity());
                        }else{
                            Toasty.info(getActivity(),"自动同步已关闭",Toasty.LENGTH_SHORT).show();
                            MySharedPreferences.saveData(synchronize_key,"false",getActivity());
                        }
                    }else{
                        Toasty.warning(getActivity(),"当前无网络 无法打开自动同步",Toasty.LENGTH_SHORT).show();
                    }
                }else{
                    Toasty.warning(getActivity(),"当前未登陆 无法打开自动同步",Toasty.LENGTH_SHORT).show();
                }

            }
        });
        //QQ登陆相关
        tencent=Tencent.createInstance("101933219",getActivity().getApplicationContext());

        qqImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IshaveNetWork.getIsNetWork(getActivity())!=0){
                    //Toasty.info(getActivity(),"审核还没过，暂停使用",Toasty.LENGTH_SHORT).show();
                    //如果session无效，就开始做登录操作
                    if (!tencent.isSessionValid()) {
                        loginQQ();
                    }
                }else{
                    Toasty.warning(getActivity(),"当前无网络 无法登陆",Toasty.LENGTH_SHORT).show();
                }
            }
        });
        //上传 下载
        up_tv=getActivity().findViewById(R.id.up_tv);
        download_tv=getActivity().findViewById(R.id.download_tv);
        up_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginUtil.isLogin(getActivity())){
                    if(IshaveNetWork.getIsNetWork(getActivity())!=0){
                        // 上传
                        DoPostUpLoad.uploadAllData(getActivity(),LoginUtil.getUserID(getActivity()));
                    }else{
                        Toasty.warning(getActivity(),"当前无网络 无法上传",Toasty.LENGTH_SHORT).show();
                    }
                }else{
                    Toasty.warning(getActivity(),"当前未登陆 无法上传",Toasty.LENGTH_SHORT).show();
                }
            }
        });
        download_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginUtil.isLogin(getActivity())){
                    if(IshaveNetWork.getIsNetWork(getActivity())!=0){
                        if(synchronize_sb.isEnabled()&&synchronize_sb.isChecked()){
                            //如果打开了自动同步 直接
                            //下载数据
                            DoPostDownload.downloadAllData(getActivity(),LoginUtil.getUserID(getActivity()));
                            //通知UI更新
                            EventBus.getDefault().post(new UpdateNoteEventBus("update"));
                        }else{
                            String tips="当前未打开自动同步 请确认先上传本地已修改数据 再下载 否则本地未同步数据将丢失";
                            //如果没有打开自动同步的话 必须确认
                            new XPopup.Builder(getActivity()).asConfirm("提醒",tips,
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            //下载数据
                                            DoPostDownload.downloadAllData(getActivity(),LoginUtil.getUserID(getActivity()));
                                            //通知UI更新
                                            EventBus.getDefault().post(new UpdateNoteEventBus("update"));
                                        }
                                    })
                                    .show();
                        }
                    }else{
                        Toasty.warning(getActivity(),"当前无网络 无法下载",Toasty.LENGTH_SHORT).show();
                    }
                }else{
                    Toasty.warning(getActivity(),"当前未登陆 无法下载",Toasty.LENGTH_SHORT).show();
                }

            }
        });
        //加载设置
        loadSettings();

    }
    public void loginQQ(){
        listener = new IUiListener() {
            @Override
            public void onComplete(Object object) {

                Log.d("MainActivity", "登录成功: " + object.toString() );

                JSONObject jsonObject = (JSONObject) object;
                try {
                    //得到token、expires、openId等参数
                    String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                    String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                    String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);

                    tencent.setAccessToken(token, expires);
                    tencent.setOpenId(openId);


                    //获取个人信息
                    getQQInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                //登录失败
                Log.d("MainActivity", "登录失败 ");

            }

            @Override
            public void onCancel() {
                //登录取消
                Log.d("MainActivity", "登录取消" );

            }

            @Override
            public void onWarning(int i) {
                Log.d("MainActivity", String.valueOf(i));
            }
        };
        //context上下文、第二个参数SCOPO 是一个String类型的字符串，表示一些权限
        //应用需要获得权限，由“,”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
        //第三个参数事件监听器
        tencent.login(getActivity(), "all", listener);
        //注销登录
        //mTencent.logout(this);
    }
    /**
     * 获取QQ个人信息
     */
    private void getQQInfo() {
        //获取基本信息
        QQToken qqToken = tencent.getQQToken();
        UserInfo info = new UserInfo(getActivity(), qqToken);
        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                Log.d("MainActivity", "个人信息: " + object.toString() );
                //infoText.setText(object.toString());
                //头像
                //String avatar = ((JSONObject) object).getString("figureurl_2");
                //GlideUtils.showGlide(TestActivity.this, avatar, infoIcon);
                // String nickName = ((JSONObject) object).getString("nickname");
                //infoName.setText(nickName);
            }

            @Override
            public void onError(UiError uiError) {
                Log.d("MainActivity", String.valueOf(uiError));
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onWarning(int i) {

            }
        });
    }

    /**
     * 回调必不可少
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, listener);
            }
        }
    }

    public void loadSettings(){
        //进入该页面的时候先加载设置 如果设置有的话 比如展示天气按钮
        //加载设置进来
        String isTurnOnFunction= MySharedPreferences.getData(display_weather_key,getActivity());
        String isTurnOnSynchronize=MySharedPreferences.getData(synchronize_key,getActivity());
        String isLoginState=MySharedPreferences.getData(isLogin_key,getActivity());
        //如果返回为nothing说明没有设置过 不用管 (当查询的数据没有的时候默认返回nothing，自己设置的 )
        if (!isTurnOnFunction.equals("nothing")&&isTurnOnFunction.equals("true")){
            //如果是已经开启了的话 直接设置为true显示选中
            display_weather_sb.setChecked(true);
        }
        if (!isTurnOnSynchronize.equals("nothing")&&isTurnOnSynchronize.equals("true")){
            //如果是已经开启了的话 直接设置为true显示选中
            synchronize_sb.setChecked(true);
        }
        if(!isLoginState.equals("nothing")&&isLoginState.equals("true")){
            login_state.setText("已登录");
            login_state.setTextColor(Color.GREEN);
        }else{
            login_state.setText("未登陆");
            login_state.setTextColor(Color.RED);
        }
        if(!LoginUtil.isLogin(getActivity())){
            //如果没登录的话 不允许选中自动同步
            synchronize_sb.setEnabled(false);
        }
        if(IshaveNetWork.getIsNetWork(getActivity())==0){
            //为0代表的是无网络
            display_weather_sb.setEnabled(false);
        }

    }

}
