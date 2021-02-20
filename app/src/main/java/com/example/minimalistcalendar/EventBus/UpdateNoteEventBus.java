package com.example.minimalistcalendar.EventBus;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.EventBus
 * @ClassName: UpdateNoteEventBus
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/18 19:24
 */
public class UpdateNoteEventBus {
    private String msg;
    //用来通知fragment 数据更新了 需要更新UI了。
    public UpdateNoteEventBus(String msg){
        this.msg=msg;
    }
    public String getMsg(){
        return msg;
    }
}
