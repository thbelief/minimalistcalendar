package com.example.minimalistcalendar.Bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar
 * @ClassName: DataBean
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/1/31 14:09
 */
public class DataBean implements Serializable {
    private int id;
    private String title;
    private String degree;
    private String degreeColor;
    private int year;
    private int month;
    private int day;
    private int isAlarm;
    private String alarmRemind;//提前多久提醒
    private String description;

    public DataBean(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getDegreeColor() {
        return degreeColor;
    }

    public void setDegreeColor(String degreeColor) {
        this.degreeColor = degreeColor;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(int isAlarm) {
        this.isAlarm = isAlarm;
    }

    public String getAlarmRemind() {
        return alarmRemind;
    }

    public void setAlarmRemind(String alarmRemind) {
        this.alarmRemind = alarmRemind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //测试用例
    public String getDate(){
        return year+"-"+month+"-"+day;
    }

    public  Date getDateIsDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date= null;
        try {
            date = simpleDateFormat.parse(year+"-"+month+"-"+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    @Override
    public String toString(){
        return "DataBean{"+
                "id="+id+
                ", title='" + title + '\'' +
                ", degree=" + degree +
                ", degreeColor=" + degreeColor +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", isAlarm=" + isAlarm +
                ", alarmRemind=" + alarmRemind +
                ", description='" + description+ '\'' +
                '}';
    }
}
