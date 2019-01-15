package com.lanmei.speak.ui;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.huawei.android.hms.agent.HMSAgent;
import com.lanmei.speak.helper.PhoneTypeHelper;
import com.lanmei.speak.helper.TTSHelper;
import com.lanmei.speak.huawei.HUAWEIPushRevicer;
import com.lanmei.speak.utils.CommonUtils;
import com.xson.common.app.BaseApp;
import com.xson.common.helper.SharedAccount;
import com.xson.common.utils.L;
import com.xson.common.utils.StringUtils;
import com.xson.common.utils.UserHelper;

/**
 * Created by xkai on 2018/4/13.
 */

public class SpeakApp extends BaseApp {

    public static SpeakApp instance = null;
    PhoneTypeHelper phoneTypeHelper;
    public TTSHelper ttsHelper;

    @Override
    protected void installMonitor() {
        instance = this;
        if (UserHelper.getInstance(this).hasLogin()
                || (CommonUtils.isHuaWei(this))
                && StringUtils.isEmpty(SharedAccount.getInstance(this).getHuaWeiToken())) {//如果是华为，先初始化获取token，用于登录
            registerPush();
        }
//        LeakCanary.install(this);//LeakCanary内存泄漏监控
    }


    public TTSHelper getTtsHelper() {
        if (ttsHelper == null) {
            ttsHelper = new TTSHelper(this);
        }
        return ttsHelper;
    }

    public void registerPush() {
        L.d(HUAWEIPushRevicer.TAG, "registerPush");
        phoneTypeHelper = new PhoneTypeHelper(this);
        phoneTypeHelper.registerPush();
        if (ttsHelper == null) {//不是华为手机初始化百度语音
            ttsHelper = new TTSHelper(this);
        }
    }

    public void unregisterPush() {
        if (phoneTypeHelper != null) {
            phoneTypeHelper.unregisterPush();
            phoneTypeHelper = null;
            L.d(HUAWEIPushRevicer.TAG, "unregisterPush");
        }
        if (ttsHelper != null) {
            ttsHelper.onDestroy();
            ttsHelper = null;
        }
    }

    @Override
    public void watch(Object object) {

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        HMSAgent.destroy();
        if (phoneTypeHelper != null) {
            phoneTypeHelper = null;
        }
        if (ttsHelper != null) {
            ttsHelper.onDestroy();
            ttsHelper = null;
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

}
