package com.example.minimalistcalendar.DateBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.minimalistcalendar.Bean.DataBean;
import com.example.minimalistcalendar.MainActivity;
import com.example.minimalistcalendar.Network.DoPostUpLoad;
import com.example.minimalistcalendar.SharePreferences.MySharedPreferences;
import com.example.minimalistcalendar.Util.AlarmManagerUtil;
import com.example.minimalistcalendar.Util.LoginUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.DateBase
 * @ClassName: DatabaseFunctions
 * @Description: DB操作的函数都写到这个里面来
 * @Author: 作者名
 * @CreateDate: 2021/1/31 13:46
 */
public class DatabaseFunctions{
    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private DatabaseFunctions instance = null;

    //创建数据库
    public DatabaseFunctions(Context context){
        this.context=context;
        databaseHelper=new DatabaseHelper(context,"alldatalist.db",null,1);
        db=databaseHelper.getWritableDatabase();

    }
    //关闭数据库
    public void closeDataBase() {
        if (null != db && db.isOpen()) {
            db.close();
        }
        db = null;
    }
    //插入数据
    public void insertData(DataBean dataBean){
        db.execSQL("insert into alldatalist(title,degree,degreeColor,year,month,day,isAlarm,alarmRemind,description) "+
                "values(?,?,?,?,?,?,?,?,?)",new Object[]{dataBean.getTitle(),dataBean.getDegree(),dataBean.getDegreeColor(),
                dataBean.getYear(),dataBean.getMonth(),dataBean.getDay(),dataBean.getIsAlarm(),dataBean.getAlarmRemind(),
                dataBean.getDescription()});
        //如果打开了自动同步只要操作一次 就上传一次
        if(MySharedPreferences.getData("isSynchronize", context).equals("true")&& LoginUtil.isLogin(context)){
            DoPostUpLoad.uploadAllData(context,LoginUtil.getUserID(context));
        }
    }
    //插入包含id的数据 (下载时使用)
    public void insertContainID(DataBean dataBean){
        db.execSQL("insert into alldatalist(_id,title,degree,degreeColor,year,month,day,isAlarm,alarmRemind,description) "+
                "values(?,?,?,?,?,?,?,?,?,?)",new Object[]{dataBean.getId(),dataBean.getTitle(),dataBean.getDegree(),dataBean.getDegreeColor(),
                dataBean.getYear(),dataBean.getMonth(),dataBean.getDay(),dataBean.getIsAlarm(),dataBean.getAlarmRemind(),
                dataBean.getDescription()});
    }
    //查询所有的数据返回DataBean类型
    public List<DataBean> getAllDataBean(){
        List<DataBean> beanList=new ArrayList<DataBean>();
        Cursor cursor=getCursor();
        //指向查询结果的第一个位置。
        if(cursor.moveToFirst()){
            do{

                DataBean bean=new DataBean();
                bean.setId(cursor.getInt(0));
                bean.setTitle(cursor.getString(1));
                bean.setDegree(cursor.getString(2));
                bean.setDegreeColor(cursor.getString(3));
                bean.setYear(cursor.getInt(4));
                bean.setMonth(cursor.getInt(5));
                bean.setDay(cursor.getInt(6));
                bean.setIsAlarm(cursor.getInt(7));
                bean.setAlarmRemind(cursor.getString(8));
                bean.setDescription(cursor.getString(9));

                beanList.add(bean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return beanList;
    }
    //查询所有数据返回String类型
    public List<String> getAllDataString(){
        List<String> beanList=new ArrayList<String>();
        Cursor cursor=getCursor();
        //指向查询结果的第一个位置。
        if(cursor.moveToFirst()){
            do{
                DataBean bean=new DataBean();
                bean.setId(cursor.getInt(0));
                bean.setTitle(cursor.getString(1));
                bean.setDegree(cursor.getString(2));
                bean.setDegreeColor(cursor.getString(3));
                bean.setYear(cursor.getInt(4));
                bean.setMonth(cursor.getInt(5));
                bean.setDay(cursor.getInt(6));
                bean.setIsAlarm(cursor.getInt(7));
                bean.setAlarmRemind(cursor.getString(8));
                bean.setDescription(cursor.getString(9));
                beanList.add(bean.toString());
            }while (cursor.moveToNext());
        }
        cursor.close();
        return beanList;
    }
    //返回Object类型
    public List<Object> getAllDataObject(){
        List<Object> beanList=new ArrayList<Object>();
        Cursor cursor=getCursor();
        //指向查询结果的第一个位置。
        if(cursor.moveToFirst()){
            do{
                DataBean bean=new DataBean();
                bean.setId(cursor.getInt(0));
                bean.setTitle(cursor.getString(1));
                bean.setDegree(cursor.getString(2));
                bean.setDegreeColor(cursor.getString(3));
                bean.setYear(cursor.getInt(4));
                bean.setMonth(cursor.getInt(5));
                bean.setDay(cursor.getInt(6));
                bean.setIsAlarm(cursor.getInt(7));
                bean.setAlarmRemind(cursor.getString(8));
                bean.setDescription(cursor.getString(9));
                beanList.add(bean.toString());
            }while (cursor.moveToNext());
        }
        cursor.close();
        return beanList;
    }

    //按日期查找(月份加一)
    public List<DataBean> getDataByDay(Calendar calendar){
        List<DataBean> beanList = new ArrayList<>();
        int year =calendar.get(Calendar.YEAR);
        //Calendar.MONTH ，这是一个特殊于日历的值。
        //在格里高利历和罗马儒略历中一年中的第一个月是 JANUARY，它为 0；最后一个月取决于一年中的月份数。
        //
        //所以这个值的初始值为0，所以我们用它来表示日历月份时需要加1
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        Cursor cursor = db.rawQuery("select * from alldatalist where year=? and month=? and day=?",
                new String[]{year+"",month+"",day+""});
        if(cursor.moveToFirst()){
            do{
                DataBean bean=new DataBean();
                bean.setId(cursor.getInt(0));
                bean.setTitle(cursor.getString(1));
                bean.setDegree(cursor.getString(2));
                bean.setDegreeColor(cursor.getString(3));
                bean.setYear(cursor.getInt(4));
                bean.setMonth(cursor.getInt(5));
                bean.setDay(cursor.getInt(6));
                bean.setIsAlarm(cursor.getInt(7));
                bean.setAlarmRemind(cursor.getString(8));
                bean.setDescription(cursor.getString(9));
                beanList.add(bean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return beanList;
    }
    //按日期查找 (月份不加一)
    public List<DataBean> getDataByDayPlus(Calendar calendar){
        List<DataBean> beanList = new ArrayList<>();
        int year =calendar.get(Calendar.YEAR);
        //Calendar.MONTH ，这是一个特殊于日历的值。
        //在格里高利历和罗马儒略历中一年中的第一个月是 JANUARY，它为 0；最后一个月取决于一年中的月份数。
        //
        //所以这个值的初始值为0，所以我们用它来表示日历月份时需要加1
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        Cursor cursor = db.rawQuery("select * from alldatalist where year=? and month=? and day=?",
                new String[]{year+"",month+"",day+""});
        if(cursor.moveToFirst()){
            do{
                DataBean bean=new DataBean();
                bean.setId(cursor.getInt(0));
                bean.setTitle(cursor.getString(1));
                bean.setDegree(cursor.getString(2));
                bean.setDegreeColor(cursor.getString(3));
                bean.setYear(cursor.getInt(4));
                bean.setMonth(cursor.getInt(5));
                bean.setDay(cursor.getInt(6));
                bean.setIsAlarm(cursor.getInt(7));
                bean.setAlarmRemind(cursor.getString(8));
                bean.setDescription(cursor.getString(9));
                beanList.add(bean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return beanList;
    }
    //按照id查找返回
    public DataBean getDataById(int id){
        Cursor cursor = db.rawQuery("select * from alldatalist where _id=?", new String[]{id + ""});
        cursor.moveToNext();
        DataBean bean=new DataBean();
        bean.setId(cursor.getInt(0));
        bean.setTitle(cursor.getString(1));
        bean.setDegree(cursor.getString(2));
        bean.setDegreeColor(cursor.getString(3));
        bean.setYear(cursor.getInt(4));
        bean.setMonth(cursor.getInt(5));
        bean.setDay(cursor.getInt(6));
        bean.setIsAlarm(cursor.getInt(7));
        bean.setAlarmRemind(cursor.getString(8));
        bean.setDescription(cursor.getString(9));
        return bean;
    }
    //查找自增的_id的最后一个id+1返回出来
    public int getLastIDAdd1(){
        Cursor cursor = db.rawQuery("SELECT * from alldatalist ORDER BY _id DESC LIMIT 1;", new String[]{});
        cursor.moveToNext();
        return cursor.getInt(0)+1;
    }
    //按照标题查找返回
    public DataBean getDataByTitle(String title,String degree,String degreeColor,String alarmRemind,String description){
        Cursor cursor = db.rawQuery("select * from alldatalist where title=? and degree=? and degreeColor=? and alarmRemind=? and description=?",
                new String[]{title,degree,degreeColor,alarmRemind,description});
        cursor.moveToNext();
        DataBean bean=new DataBean();
        bean.setId(cursor.getInt(0));
        bean.setTitle(cursor.getString(1));
        bean.setDegree(cursor.getString(2));
        bean.setDegreeColor(cursor.getString(3));
        bean.setYear(cursor.getInt(4));
        bean.setMonth(cursor.getInt(5));
        bean.setDay(cursor.getInt(6));
        bean.setIsAlarm(cursor.getInt(7));
        bean.setAlarmRemind(cursor.getString(8));
        bean.setDescription(cursor.getString(9));
        return bean;
    }
    //删除所有数据 下载时使用
    public void deleteAllData(){
        //删除数据的时候先取消绑定的闹钟
        List<DataBean> lists=this.getAllDataBean();
        for(int i=0;i<lists.size();i++){
            AlarmManagerUtil.cancelAlarm(context,lists.get(i).getId());
        }
        //取消完之后再删除
        db.delete("alldatalist",null,null);
    }
    //通过id删除数据
    public void deleteDataById(int id) {
        db.delete("alldatalist", "_id=?", new String[]{id+""});
        //如果打开了自动同步只要操作一次 就上传一次
        if(MySharedPreferences.getData("isSynchronize", context).equals("true")&&LoginUtil.isLogin(context)){
            DoPostUpLoad.uploadAllData(context,LoginUtil.getUserID(context));
        }
    }
    //通过ID更新数据
    public void updateDataById(int id,DataBean dataBean){
        //ContentValues 和HashTable类似都是一种存储的机制 但是两者最大的区别就在于，
        // contenvalues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。
        ContentValues values=new ContentValues();
        values.put("title",dataBean.getTitle());
        values.put("degree",dataBean.getDegree());
        values.put("degreeColor",dataBean.getDegreeColor());
        values.put("year",dataBean.getYear());
        values.put("month",dataBean.getMonth());
        values.put("day",dataBean.getDay());
        values.put("isAlarm",dataBean.getIsAlarm());
        values.put("alarmRemind",dataBean.getAlarmRemind());
        values.put("description",dataBean.getDescription());
        db.update("alldatalist",values,"_id=?",new String[]{id+""});

        //如果打开了自动同步只要操作一次 就上传一次
        if(MySharedPreferences.getData("isSynchronize", context).equals("true")&&LoginUtil.isLogin(context)){
            DoPostUpLoad.uploadAllData(context,LoginUtil.getUserID(context));
        }
    }

    //查询每一条（一行一行的查）
    public Cursor getCursor() {
        String[] columns = new String[] {
                "_id",
                "title",
                "degree",
                "degreeColor",
                "year",
                "month",
                "day",
                "isAlarm",
                "alarmRemind",
                "description"
        };
        return db.query("alldatalist", columns, null, null, null, null,
                null);
    }
}
