package com.example.minimalistcalendar.EventBus;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.EventBus
 * @ClassName: UpdateLoginEventBus
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/19 16:02
 */
public class UpdateLoginEventBus {
    //更新登陆状态
    private String msg;
    //用来通知fragment 数据更新了 需要更新UI了。
    public UpdateLoginEventBus(String msg){
        this.msg=msg;
    }
    public String getMsg(){
        return msg;
    }
}
