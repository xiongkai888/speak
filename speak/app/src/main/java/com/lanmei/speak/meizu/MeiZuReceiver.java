package com.lanmei.speak.meizu;

import android.content.Context;
import android.content.Intent;

import com.lanmei.speak.huawei.HUAWEIPushRevicer;
import com.lanmei.speak.ui.MainActivity;
import com.lanmei.speak.ui.SpeakApp;
import com.lanmei.speak.utils.CommonUtils;
import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.PushManager;
import com.meizu.cloud.pushsdk.handler.MzPushMessage;
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.xson.common.utils.L;

/**
 * Created by xkai on 2018/7/31.
 */

public class MeiZuReceiver extends MzPushMessageReceiver {

    public static String APP_ID = "114899";
    public static String APP_KEY = "f72a6ab8a6694b0198cabeffbe6c2ce5";//   6daef687ecd7422b9bfc12d33fd95ffc

    @Override
    @Deprecated
    public void onRegister(Context context, String pushid) {
//调用PushManager.register(context）方法后，会在此回调注册状态
//应用在接受返回的pushid
        L.d(HUAWEIPushRevicer.TAG, "onRegister：pushid" + pushid);
    }

    @Override
    public void onMessage(Context context, String s) {
        super.onMessage(context, s);
        //接收服务器推送的透传消息
        L.d(HUAWEIPushRevicer.TAG, "onMessage：s" + s);
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        String content = intent.getStringExtra("content");
        L.d(HUAWEIPushRevicer.TAG, "onMessage：content" + content);
    }

    @Override
    @Deprecated
    public void onUnRegister(Context context, boolean b) {
//调用PushManager.unRegister(context）方法后，会在此回调反注册状态
        L.d(HUAWEIPushRevicer.TAG, "onUnRegister：b：" + b);
    }

    //设置通知栏小图标
    @Override
    public void onUpdateNotificationBuilder(PushNotificationBuilder pushNotificationBuilder) {
//重要详情参考应用小图标自定设置pushNotificationBuilder.setmStatusbarIcon(R.drawable.mz_push_notification_small_icon);
        L.d(HUAWEIPushRevicer.TAG, "onUpdateNotificationBuilder：重要详情参考应用小图标自定设置pushNotificationBuilder");
    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {
//检查通知栏和透传消息开关状态回调
        L.d(HUAWEIPushRevicer.TAG, "onPushStatus：检查通知栏和透传消息开关状态回调");
    }

    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
//调用新版订阅PushManager.register(context, appId, appKey)回调
        L.d(HUAWEIPushRevicer.TAG, "onRegisterStatus：PushId:" + registerStatus.getPushId() + ",expireTime:" + registerStatus.getExpireTime());
//        EventBus.getDefault().post(new MeiZuEvent());
        PushManager.subScribeAlias(context, MeiZuReceiver.APP_ID, MeiZuReceiver.APP_KEY, registerStatus.getPushId(), CommonUtils.getPushId(context));
    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {
//新版反订阅回调
        L.d(HUAWEIPushRevicer.TAG, "onUnRegisterStatus：新版反订阅回调:getMessage:" + unRegisterStatus.getMessage() + "," + PushManager.getPushId(context));
    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {
//标签回调
        L.d(HUAWEIPushRevicer.TAG, "onSubTagsStatus：标签回调:");
    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
//别名回调
        L.d(HUAWEIPushRevicer.TAG, "onSubAliasStatus：别名回调:" + subAliasStatus.getAlias());
    }

    @Override
    public void onNotificationArrived(Context context, MzPushMessage mzPushMessage) {
//通知栏消息到达回调，flyme6基于android6 .0以上不再回调
        L.d(HUAWEIPushRevicer.TAG, "onNotificationArrived：通知栏消息到达回调，flyme6基于android6 .0以上不再回调:toString" + mzPushMessage.toString());
//        EventBus.getDefault().post(new SpeakEvent(context.getString(R.string.order_come)));
//        MainActivity.showHome(context, SpeakApp.instance,context.getString(R.string.order_come));
//        EventBus.getDefault().post(new SpeakEvent(mzPushMessage.getContent()));
        CommonUtils.startServiceToSpeak(context, mzPushMessage.getContent());
    }

    @Override
    public void onNotificationClicked(Context context, MzPushMessage mzPushMessage) {
//通知栏消息点击回调
        L.d(HUAWEIPushRevicer.TAG, "onNotificationClicked：通知栏消息点击回调");
        MainActivity.showHome(context, SpeakApp.instance);
    }

    @Override
    public void onNotificationDeleted(Context context, MzPushMessage mzPushMessage) {
//通知栏消息删除回调；flyme6基于android6 .0以上不再回调
        L.d(HUAWEIPushRevicer.TAG, "onNotificationDeleted：通知栏消息删除回调；flyme6基于android6 .0以上不再回调");
    }
}
