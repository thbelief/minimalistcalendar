package com.example.minimalistcalendar.Bean;

import com.nlf.calendar.Solar;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Bean
 * @ClassName: HolidayBean
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/5 17:28
 */
public class HolidayBean implements Serializable {
    //日期
    private Solar        solar;
    //节假日的集合
    private List<String> holidays;

    public void setSolar(Solar solar) {
        this.solar = solar;
    }
    public Solar getSolar() {
        return solar;
    }

    public void setHolidays(List<String> holidays) {
        this.holidays = holidays;
    }
    public String getHolidays() {
        StringBuilder sb = new StringBuilder();
        String resultString = "";
        for(int i=0;i<holidays.size();i++){
            if(i<holidays.size()-1){
                sb.append(holidays.get(i));
                sb.append(" ");
            }else{
                sb.append(holidays.get(i));
            }
        }
        resultString = sb.toString();
        return resultString;
    }
    public void addHoliday(String holiday){
        holidays.add(holiday);
    }
}
