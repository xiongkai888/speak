package com.lanmei.speak.xiaomi;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.lanmei.speak.ui.MainActivity;
import com.lanmei.speak.ui.SpeakApp;
import com.lanmei.speak.utils.CommonUtils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.xson.common.utils.L;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 DemoMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */
public class XiaoMiReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    public static String TAG = "XiaoMiReceiver";
    public static String APP_ID = "2882303761517843099";
    public static String APP_KEY = "5101784379099";//

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
        L.d(TAG, "onReceivePassThroughMessage:mTopic：" + mTopic);
        L.d(TAG, ":onReceivePassThroughMessage:mAlias：" + mAlias);
    }

    //点击推送通知时调用这里
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
        L.d(TAG, "onNotificationMessageClicked:mTopic：" + mTopic);
        L.d(TAG, "onNotificationMessageClicked:mAlias：" + mAlias);
        L.d(TAG, "点击了小米通知栏");
        MainActivity.showHome(context, SpeakApp.instance);
    }

    //用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
    @Override
    public void onNotificationMessageArrived(final Context context, MiPushMessage message) {

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
        L.d(TAG, "onNotificationMessageArrived:mTopic：" + mTopic);
        L.d(TAG, ":onNotificationMessageArrived:mAlias：" + mAlias);
        L.d(TAG, ":onNotificationMessageArrived:mAlias：" + message.toString());
//        EventBus.getDefault().post(new SpeakEvent(message.getDescription()));
        CommonUtils.startServiceToSpeak(context,message.getDescription());
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                L.d(TAG, "onCommandResult:COMMAND_REGISTER：mRegId:" + mRegId);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_REGISTER：mRegId:" + null);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                L.d(TAG, "onCommandResult:COMMAND_SET_ALIAS：mRegId:" + mRegId);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_SET_ALIAS：mRegId:" + null);
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                L.d(TAG, "onCommandResult:COMMAND_UNSET_ALIAS：mRegId:" + mRegId);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_UNSET_ALIAS：mRegId:" + null);
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                L.d(TAG, "onCommandResult:COMMAND_SET_ACCOUNT：mAccount:" + mAccount);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_SET_ACCOUNT：mAccount:" + null);
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                L.d(TAG, "onCommandResult:COMMAND_UNSET_ACCOUNT：mAccount:" + mAccount);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_UNSET_ACCOUNT：mAccount:" + null);
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                L.d(TAG, "onCommandResult:COMMAND_SUBSCRIBE_TOPIC：mTopic:" + mTopic);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_SUBSCRIBE_TOPIC：mTopic:" + null);
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                L.d(TAG, "onCommandResult:COMMAND_UNSUBSCRIBE_TOPIC：mTopic:" + mTopic);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_UNSUBSCRIBE_TOPIC：mTopic:" + null);
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                L.d(TAG, "onCommandResult:COMMAND_SET_ACCEPT_TIME：mStartTime:" + mStartTime + "  mEndTime:" + mEndTime);
            } else {
                L.d(TAG, "onCommandResult:COMMAND_SET_ACCEPT_TIME：mStartTime和:mEndTime" + null);
            }
        } else {
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                L.d(TAG, "onReceiveRegisterResult:mRegId:" + mRegId);
            } else {
                L.d(TAG, "onReceiveRegisterResult:mRegId:" + null);
            }
        } else {
        }
    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            Intent intent = new Intent();
            intent.putExtra("permissions", permissions);
            intent.setComponent(new ComponentName(context.getPackageName(), MainActivity.class.getCanonicalName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }
}
