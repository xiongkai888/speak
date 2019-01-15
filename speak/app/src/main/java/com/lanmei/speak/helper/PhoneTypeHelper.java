package com.lanmei.speak.helper;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.push.handler.DeleteTokenHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.lanmei.speak.huawei.HUAWEIPushRevicer;
import com.lanmei.speak.meizu.MeiZuReceiver;
import com.lanmei.speak.utils.BadgeUtil;
import com.lanmei.speak.utils.CommonUtils;
import com.lanmei.speak.xiaomi.XiaoMiReceiver;
import com.meizu.cloud.pushsdk.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xson.common.helper.SharedAccount;
import com.xson.common.utils.L;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by xkai on 2018/7/31.
 */

public class PhoneTypeHelper {

    private Application context;
//    HuaweiApiClient client;

    public PhoneTypeHelper(Application context) {
        this.context = context;
    }

    public void unregisterPush() {
        String type = BadgeUtil.getClientType();
        L.d(HUAWEIPushRevicer.TAG, type + "");
        switch (type) {
            case "xiaomi":
                unregisterXIAOMI();
                break;
            case "huawei":
                unregisterHUAWEI();
                break;
            case "meizu":
                unregisterMEIZU();
                break;
            case "jiguang":
                unregisterJiGuang();
                break;
        }
    }

    private void unregisterHUAWEI() {
        L.d(HUAWEIPushRevicer.TAG, SharedAccount.getInstance(context).getHuaWeiToken());
        /**
         * 注意：该接口只在华为手机并且EMUI版本号不低于5.1的版本上才起作用，即只在EMUI5.1以及更高版本的华为手机上调用该接口后才不会收到PUSH消息。在非华为手机上则必须满足HMS版本不低于2.5.0。
         */
        HMSAgent.Push.deleteToken(SharedAccount.getInstance(context).getHuaWeiToken(), new DeleteTokenHandler() {
            @Override
            public void onResult(int rst) {
                L.d(HUAWEIPushRevicer.TAG, "华为推送反注册成功");
                L.d(HUAWEIPushRevicer.TAG, "deleteToken:end code=" + rst);
            }
        });
//        HuaweiPush.HuaweiPushApi.deleteToken(client, SharedAccount.getInstance(context).getHuaWeiToken());
    }

    private void unregisterJiGuang() {
        JPushInterface.stopPush(context);
    }

    private void unregisterMEIZU() {
        if (MzSystemUtils.isBrandMeizu(context)) {//魅族推送只适用于Flyme系统,因此可以先行判断是否为魅族机型，再进行订阅，避免在其他机型上出现兼容性问题
            L.d(HUAWEIPushRevicer.TAG, "PushManager.getPushId(context):" + PushManager.getPushId(context));
            PushManager.unSubScribeAlias(context, MeiZuReceiver.APP_ID, MeiZuReceiver.APP_KEY, PushManager.getPushId(context), CommonUtils.getPushId(context));
            PushManager.unRegister(context, MeiZuReceiver.APP_ID, MeiZuReceiver.APP_KEY);
        }
    }

    public void registerPush() {
        String type = BadgeUtil.getClientType();
        L.d(HUAWEIPushRevicer.TAG, type + "");
        switch (type) {
            case "xiaomi":
                initXIAOMI();
                break;
            case "huawei":
                initHUAWEI();
                break;
            case "meizu":
                initMEIZU();
                break;
            case "jiguang":
                initJiGuang();
                break;
        }
    }

    private void unregisterXIAOMI() {
        //初始化push推送服务
        if (shouldInit()) {//小米
            MiPushClient.unsetAlias(context, CommonUtils.getPushId(context), null);
            L.d(HUAWEIPushRevicer.TAG, "unregisterXIAOMI");
        }
    }

    private void initXIAOMI() {
        //初始化push推送服务
        if (shouldInit()) {//小米
            MiPushClient.registerPush(context, XiaoMiReceiver.APP_ID, XiaoMiReceiver.APP_KEY);
            MiPushClient.setAlias(context, CommonUtils.getPushId(context), null);
            L.d(HUAWEIPushRevicer.TAG, "registerXIAOMI");
//            MiPushClient.unsetAlias(this, CommonUtils.getUserId(this), null);
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void initHUAWEI() {
        HMSAgent.init(context);
//        HMSAgent.connect(this, new ConnectHandler() {
//            @Override
//            public void onConnect(int rst) {
//                L.d("HMS connect end:" + rst);
//            }
//        });
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rst) {
                L.d(HUAWEIPushRevicer.TAG, "getToken code:" + rst);
            }
        });

//        client = new HuaweiApiClient.Builder(context)
//                .addApi(HuaweiPush.PUSH_API)
//                .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected() {
//                        L.d(HUAWEIPushRevicer.TAG,"HuaweiApiClient:onConnected");
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int cause) {
//                        L.d(HUAWEIPushRevicer.TAG,"HuaweiApiClient:onConnectionSuspended:cause:"+cause);
//                    }
//                })
//                .addOnConnectionFailedListener(new HuaweiApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(ConnectionResult result) {
//                        L.d(HUAWEIPushRevicer.TAG,"HuaweiApiClient:onConnectionFailed");
//                    }
//                })
//                .build();
//        client.connect();
    }

    private void initMEIZU() {
        if (MzSystemUtils.isBrandMeizu(context)) {
            PushManager.register(context, MeiZuReceiver.APP_ID, MeiZuReceiver.APP_KEY);
        }
    }

    private void initJiGuang() {
        JPushInterface.setDebugMode(false);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(context);            // 初始化 JPush
        JPushInterface.resumePush(context);
        JPushInterface.setAlias(context, 0, CommonUtils.getPushId(context));
        L.d(HUAWEIPushRevicer.TAG, "极光推送设置别名:" + CommonUtils.getPushId(context));
    }
}
