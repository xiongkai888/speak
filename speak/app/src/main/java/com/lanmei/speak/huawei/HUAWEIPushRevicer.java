package com.lanmei.speak.huawei;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.lanmei.speak.utils.CommonUtils;
import com.xson.common.helper.SharedAccount;
import com.xson.common.utils.L;
import com.xson.common.utils.UserHelper;

/**
 * Created by xkai on 2018/7/31.
 */

public class HUAWEIPushRevicer extends PushReceiver {

    public static String TAG = "HUAWEIPushRevicer";

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        SharedAccount.getInstance(context).saveHuaWeiToken(token);
        L.d(TAG, "获取到华为token:" + token);
    }

    /**
     * 通过广播接收Push连接状态
     *
     * @param context
     * @param pushState
     */
    @Override
    public void onPushState(Context context, boolean pushState) {
        L.d(TAG, "通过广播接收Push连接状态:pushState" + pushState);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msgBytes, Bundle extras) {
        if (!UserHelper.getInstance(context).hasLogin()) {
            return false;
        }
        try {
            //CP可以自己解析消息内容，然后做相应的处理
            String content = new String(msgBytes, "UTF-8");
            L.d(TAG, "收到PUSH透传消息,消息内容为:" + content);
//            EventBus.getDefault().post(new SpeakEvent(content));
            CommonUtils.startServiceToSpeak(context, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        super.onEvent(context, event, extras);
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            L.d(HUAWEIPushRevicer.TAG, "收到通知栏消息点击事件,notifyId:" + notifyId);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
        }
        String message = extras.getString(BOUND_KEY.pushMsgKey);
        L.d(HUAWEIPushRevicer.TAG, "HUAWEIPushRevicer.onEvent:" + message);
    }

}
