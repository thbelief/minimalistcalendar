package com.example.minimalistcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.minimalistcalendar.Adapter.RecycleAdapter;
import com.example.minimalistcalendar.Bean.WeatherResponseBean;
import com.example.minimalistcalendar.Bean.WeatherTodayBean;
import com.example.minimalistcalendar.Fragment.DateFragmentPlus;
import com.example.minimalistcalendar.Fragment.HolidayFragment;
import com.example.minimalistcalendar.Fragment.NoteFragment;
import com.example.minimalistcalendar.Fragment.SettingFragment;
import com.example.minimalistcalendar.Network.DoPostDownload;
import com.example.minimalistcalendar.Network.IshaveNetWork;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.example.minimalistcalendar.Util.DialogUtil;
import com.example.minimalistcalendar.Util.LoginUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.necer.calendar.Miui9Calendar;
import com.necer.enumeration.CheckModel;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private Miui9Calendar miui9Calendar;
    private TextView textView;
    private ImageView imageView;
    protected CheckModel checkModel;

    //recyclerView
    private RecyclerView recyclerView;
    private RecycleAdapter recycleAdapter;
    private Context context;
    private List<Fragment> list;
    private List<String> list_loadnotes;
    private FloatingActionsMenu float_fab_menu;
    //底部导航栏的四个按钮
    private RadioGroup radioGroup;
    private RadioButton date_rb;
    private RadioButton note_rb;
    private RadioButton holiday_rb;
    private RadioButton setting_rb;
    //tab num
    private int tabnum=0;

    //权限获取框架 通过依赖导入的 用于快速动态权限获取
    private RxPermissions      rxPermissions;
    //定位器 百度地图
    public  LocationClient     mLocationClient = null;
    private MyLocationListener myListener      = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //如果是第一次打开的话 弹出提示框
        if(MySharedPreferences.getData("isFirstOpenApp",MainActivity.this).equals("nothing")){
            MySharedPreferences.saveData("isFirstOpenApp","false",MainActivity.this);
            DialogUtil.messageDialog(MainActivity.this,"Tips", getString(R.string.precautions));
        }

        //Android App中的方法数超过65535时，如果往下兼容到低版本设备时，就会报编译错误，尤其在引入一些jar包或者搞了一个modle进来之后容易出现这个错误
        MultiDex.install(this);

        //悬浮按钮的点击事件
        FloatingActionButton actionA=(FloatingActionButton)findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNote();
            }
        });
        FloatingActionButton actionB=(FloatingActionButton)findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMemorialDay();
            }
        });
        //返回主页面的时候收拢floatFab 不然还是展开的
        float_fab_menu=(FloatingActionsMenu)findViewById(R.id.float_fab);
        //fragment切换
        list=new ArrayList<>(4);
        //添加几个fragment
        //list.add(new DateFragment());
        list.add(new DateFragmentPlus());
        list.add(new NoteFragment());
        list.add(new HolidayFragment());
        list.add(new SettingFragment());
        MyFragmentPagerAdapter adapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),list);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(tabnum);
        //底部导航栏的第几个
        date_rb=findViewById(R.id.date_tab);
        note_rb=findViewById(R.id.note_tab);
        holiday_rb=findViewById(R.id.holiday_tab);
        setting_rb=findViewById(R.id.settings_tab);
        date_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabnum=0;
                viewPager.setCurrentItem(tabnum);
            }
        });
        note_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"点击了note——rb",Toast.LENGTH_SHORT).show();
                tabnum=1;
                viewPager.setCurrentItem(tabnum);
            }
        });
        holiday_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabnum=2;
                viewPager.setCurrentItem(tabnum);
            }
        });
        setting_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabnum=3;
                viewPager.setCurrentItem(tabnum);
            }
        });

        //Log.d("MainActivity", Lunar.fromDate(new Date()).toFullString());
        //导航栏和页面的联动
        radioGroup=findViewById(R.id.tab_radioGroup);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        //必须先判断是否有网络 如果没有网络的话 不能进行网络请求
        if(IshaveNetWork.getIsNetWork(MainActivity.this)!=0){
            //定位权限判断并获取
            rxPermissions=new RxPermissions(this);//必须实例化否则报错
            permissionVersion();
            if(MySharedPreferences.getData("isSynchronize",MainActivity.this).equals("true")&&
                    LoginUtil.isLogin(MainActivity.this)){
                //如果已经开启了自动同步的话
                //开启了同步的话 数据自动下载更新 测试
                DoPostDownload.downloadAllData(MainActivity.this, LoginUtil.getUserID(MainActivity.this));
            }
        }else{
            //无网络的话 自动关闭天气以及自动同步 自动退出登陆
            MySharedPreferences.saveData("isLoginStatus","false",MainActivity.this);
            MySharedPreferences.saveData("isSynchronize","false",MainActivity.this);
            MySharedPreferences.saveData("isDisplayWeather","false",MainActivity.this);
            DialogUtil.messageDialog(MainActivity.this,"提示","当前无网络\n"+
                    "已自动关闭天气展示\n"+
                    "已自动关闭自动同步\n"+
                    "已自动退出登陆\n"+
                    "如有需要请自行打开");
            //Toast.makeText(MainActivity.this, "当前无网络,已自动关闭天气以及自动同步", Toast.LENGTH_SHORT).show();
        }

    }
    //定位权限判断
    private void permissionVersion(){
        if(Build.VERSION.SDK_INT >= 23){//6.0或6.0以上
            //动态权限申请
            permissionsRequest();
        }else {//6.0以下
            //发现只要权限在AndroidManifest.xml中注册过，均会认为该权限granted  提示一下即可
            Toast.makeText(MainActivity.this,"版本在6.0以下，不需要申请权限！", Toast.LENGTH_SHORT).show();
        }
    }
    //动态权限申请
    private void permissionsRequest() {
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {//申请成功
                        //得到权限之后开始定位
                        startLocation();
                    } else {//申请失败
                        Toast.makeText(MainActivity.this,"权限未开启！", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //开始定位
    private void startLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(this);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);
        //可选，设置是否需要最新版本的地址信息。默认不需要，即参数为false
        option.setNeedNewVersionRgc(true);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(option);
        //启动定位
        mLocationClient.start();
    }
    /**
     * 定位结果返回
     */
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();//161  表示网络定位结果
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String locationDescribe = location.getLocationDescribe();
            //Log.d("MainActivity","当前的城市为"+district);
            //获取当前城市的id 和风天气查询天气的时候需要用到的城市id
           getCityID(district);
        }
    }

    //下面使用异步请求get来获取天气状态
    private void getCityID(String district) {
        //使用Get异步请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //拼接访问地址
                .url("https://geoapi.qweather.com/v2/city/lookup?key=00962ab93927469eb12b50607c737379&location="+district)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //如果请求失败的话
                Toast.makeText(MainActivity.this,"网络请求失败",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
//                    Log.d("MainActivity","获取数据成功了");
//                    Log.d("MainActivity","response.code()=="+response.code());
//                    Log.d("MainActivity","response.body().string()=="+ response.body().string());
                    Gson gson=new GsonBuilder().create();
                    //将json数据传入实体类bean中 主要就是拿到城市id 然后通过城市id来查询城市天气
                    WeatherResponseBean weatherResponseBean=gson.fromJson(response.body().string(),WeatherResponseBean.class);
                    //Log.d("MainActivity","测试"+ weatherResponseBean.getLocation().get(0).getCountry());
                    getWeather(weatherResponseBean.getLocation().get(0).getId());
                }
            }
        });
    }
    private void getWeather(String cityId) {
        //使用Get异步请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //拼接访问地址
                .url("https://devapi.qweather.com/v7/weather/now?key=00962ab93927469eb12b50607c737379&location="+cityId)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //如果请求失败的话
                Toast.makeText(MainActivity.this,"网络请求失败",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
//                    Log.d("MainActivity","获取数据成功了");
//                    Log.d("MainActivity","response.code()=="+response.code());
//                    Log.d("MainActivity","response.body().string()=="+ response.body().string());
                    Gson gson=new GsonBuilder().create();
                    //将json数据传入实体类bean中 主要就是拿到城市id 然后通过城市id来查询城市天气
                    WeatherTodayBean weatherTodayBean=gson.fromJson(response.body().string(),WeatherTodayBean.class);
                    //存储数据
                    MySharedPreferences.saveData("weatherIcon",weatherTodayBean.getNow().getIcon(),MainActivity.this);
                    MySharedPreferences.saveData("weatherState",weatherTodayBean.getNow().getText(),MainActivity.this);
                    MySharedPreferences.saveData("weatherTemp",weatherTodayBean.getNow().getTemp(),MainActivity.this);
                    //Log.d("MainActivity","测试"+ MySharedPreferences.getData("weatherIcon",MainActivity.this));
                }
            }
        });
    }
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //随着页面滑动 改变下方tab按钮的选中状态
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return this.mList == null ? null : this.mList.get(position);
        }

        @Override
        public int getCount() {
            return this.mList == null ? 0 : this.mList.size();
        }
    }

    //点击了 创建记事的点击事件
    public void createNote(){

        //Toast.makeText(MainActivity.this,"click A",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this, AddNoteActivity.class);
        //第一个参数 intent对象 第二个参数 请求的标识
        startActivityForResult(intent,1);
    }
    //创建纪念日的点击事件
    public void createMemorialDay(){
        //Toast.makeText(MainActivity.this,"click B",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this, AddMemorialDayActivity.class);
        //第一个参数 intent对象 第二个参数 请求的标识
        startActivityForResult(intent,1);
    }
    /*
    通过startActivityForResult的方式接受返回数据的方法
    requestCode：请求的标志,给每个页面发出请求的标志不一样，这样以后通过这个标志接受不同的数据
    resultCode：这个参数是setResult(int resultCode,Intent data)方法传来的,这个方法用在传来数据的那个页面
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode ,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //当请求码是1&&返回码是2进行下面操作
        if(requestCode==1&&resultCode==2){
            String note_back_mess=data.getStringExtra("data");
            //Toast.makeText(MainActivity.this,note_back_mess,Toast.LENGTH_SHORT).show();
            //返回首页的时候从数据库重新加载数据刷新记事显示
//            loadNotes();

        }
        else if(requestCode==1&&resultCode==3){
            String note_back_mess=data.getStringExtra("data");
            //Toast.makeText(MainActivity.this,note_back_mess,Toast.LENGTH_SHORT).show();

            //返回首页的时候从数据库重新加载数据刷新记事显示
//            loadNotes();
        }
    }

    @Override
    protected void onPostResume() {
        //重新唤醒Activity的时候需要将按钮默认不展开
        super.onPostResume();
        //默认是展开的  必须切换状态折叠回去
        //float_fab_menu.toggle();
        if(float_fab_menu.isExpanded()){
            float_fab_menu.toggle();
        }
    }

}
