package com.lanmei.speak.bean;

/**
 * Created by xkai on 2018/7/30.
 */

public class TokenBean {

    /**
     * access_token : 7fcd6c1feac64ffc80a26caffcab5660
     * access_uid : 711806211909002
     * token_expire_time : 2018-08-06 11:27:57
     */

    private String access_token;
    private String access_uid;
    private String push_id;
    private String token_expire_time;

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_uid() {
        return access_uid;
    }

    public void setAccess_uid(String access_uid) {
        this.access_uid = access_uid;
    }

    public String getToken_expire_time() {
        return token_expire_time;
    }

    public void setToken_expire_time(String token_expire_time) {
        this.token_expire_time = token_expire_time;
    }
}
