package com.example.minimalistcalendar.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Bean
 * @ClassName: DownloadBean
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/18 16:31
 */
public class DownloadBean {

    /**
     * userID : 1
     * _id : 2
     * title : snbsbsb
     * degree : MemorialDay
     * degreeColor : 无
     * year : 2021
     * month : 2
     * day : 18
     * isAlarm : 1
     * alarmRemind : 2021-02-18-14-46
     * description :
     */

    @SerializedName("userID")
    private Integer userID;
    @SerializedName("_id")
    private Integer id;
    @SerializedName("title")
    private String  title;
    @SerializedName("degree")
    private String  degree;
    @SerializedName("degreeColor")
    private String  degreeColor;
    @SerializedName("year")
    private Integer year;
    @SerializedName("month")
    private Integer month;
    @SerializedName("day")
    private Integer day;
    @SerializedName("isAlarm")
    private Integer isAlarm;
    @SerializedName("alarmRemind")
    private String  alarmRemind;
    @SerializedName("description")
    private String  description;

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(Integer isAlarm) {
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
}
