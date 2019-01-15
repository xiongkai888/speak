package com.xson.common.helper;

import android.content.Context;

public class SharedAccount {

    private static SharedPreferencesTool sp;

    private SharedAccount() {
    }

    private static SharedAccount instance = null;

    public static SharedAccount getInstance(Context context) {
        if (instance == null) {
            instance = new SharedAccount();
        }
        sp = SharedPreferencesTool.getInstance(context, "account");
        return instance;
    }

    public void saveAccessUid(String access_uid) {
        sp.edit().putString("access_uid", access_uid).commit();
    }

    public String getAccessUid() {
        return sp.getString("access_uid", "");
    }
    public String getPushId() {
        return sp.getString("push_id", "");
    }

    public void saveAccessToken(String access_token) {
        sp.edit().putString("access_token", access_token).commit();
    }

    public void saveHuaWeiToken(String huawei_token) {
        sp.edit().putString("huawei_token", huawei_token).commit();
    }
    public void savePushId(String push_id) {
        sp.edit().putString("push_id", push_id).commit();
    }

    public String getHuaWeiToken() {
        return sp.getString("huawei_token", "");
    }

    public String getAccessToken() {
        return sp.getString("access_token", "");
    }

    public void savePhone(String phone) {
        sp.edit().putString("phone", phone).commit();
    }

    public String getPhone() {
        return sp.getString("phone", "");
    }

    public void setNoFirstLogin(boolean isFirstLogin) {
        sp.edit().putBoolean("isFirstLogin", isFirstLogin).commit();
    }

    public boolean isFirstLogin() {
        return sp.getBoolean("isFirstLogin", false);
    }

    public void setOpenVoice(boolean open_voice) {
        sp.edit().putBoolean("open_voice", open_voice).commit();
    }

    public boolean isOpenVoice() {
        return sp.getBoolean("open_voice", true);
    }

    public void clear() {
        sp.clear();
    }


}
