package com.lanmei.speak.event;

/**
 * Created by xkai on 2018/7/12.
 * 注册或修改密码事件
 */

public class RegisterEvent {

    private String phone;

    public String getPhone() {
        return phone;
    }

    public RegisterEvent(String phone){
        this.phone = phone;
    }
}
