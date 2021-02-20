package com.example.minimalistcalendar.DateBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.DateBase
 * @ClassName: DatabaseHelper
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/1/31 13:28
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //自增长字段定义为INTEGER PRIMARY KEY AUTOINCREMENT
        db.execSQL("create table alldatalist(_id integer primary key autoincrement," +
                "title char(20),degree char(20),degreeColor char(20)," +
                "year int(20),month int(20),day int(20),"+
                "isAlarm int(20),alarmRemind char(50),"+
                "description char(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //必须重写 没用到就直接空着了
    }
}
