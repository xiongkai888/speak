package com.lanmei.speak.utils;


import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by xkai on 2018/1/18.
 */

public class BadgeUtil {
    /**
     * Set badge count<br/>
     * 针对 Samsung / xiaomi / sony 手机有效
     *
     * @param context The context of the application package.
     * @param count   Badge count to be set
     */
    public static void setBadgeCount(Context context, int count) {
        if (count <= 0) {
            count = 0;
        } else {
            count = Math.max(0, Math.min(count, 99));
        }
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(context, count);
        } else if (manufacturer.equalsIgnoreCase("sony")) {
            sendToSony(context, count);
        } else if (manufacturer.equalsIgnoreCase("samsung") || manufacturer.toLowerCase().contains("lg")) {
            sendToSamsumg(context, count);
        } else if (manufacturer.equalsIgnoreCase("htc")) {
            setBadgeOfHTC(context, count);
        } else if (manufacturer.equalsIgnoreCase("nova")) {
            setBadgeOfNova(context, count);
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            setBadgeOfVIVO(context, count);
        } else if (manufacturer.equalsIgnoreCase("HUAWEI") || Build.BRAND.equals("Huawei") || Build.BRAND.equals("HONOR")) {//华为
            setHuaweiBadge(context, count-1);
        } else if (manufacturer.equalsIgnoreCase("OPPO")) {//oppo
            setBadgeOfOPPO(context, count);
        } else if (manufacturer.equalsIgnoreCase("meizu")) {//魅族

        } else if (manufacturer.toLowerCase().contains("")) {//金立

        } else if (manufacturer.toLowerCase().contains("")) {//锤子

        } else {
            //Toast.makeText(context, "Not Found Support Launcher", Toast.LENGTH_LONG).show();
        }
    }
    /**
     *
     */
    public static String getClientType() {

        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase("Xiaomi")) {
            return "xiaomi";
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            return "jiguang";
        } else if (manufacturer.equalsIgnoreCase("HUAWEI") || Build.BRAND.equals("Huawei") || Build.BRAND.equals("HONOR")) {//华为
            return "huawei";
        } else if (manufacturer.equalsIgnoreCase("meizu")) {//魅族
            return "meizu";
        } else {
            return "jiguang";//其他手机
        }
    }




    /**
     * 设置oppo的Badge :oppo角标提醒目前只针对内部软件还有微信、QQ开放，其他的暂时无法提供
     */
    private static void setBadgeOfOPPO(Context context, int count) {
        try {
            Bundle extras = new Bundle();
            extras.putInt("app_badge_count", count);
            context.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", String.valueOf(count), extras);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置华为的Badge :mate8 和华为 p7,honor畅玩系列可以,honor6plus 无效果
     */
    public static void setHuaweiBadge(Context context, int count) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("package", context.getPackageName());
            String launchClassName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
            bundle.putString("class", launchClassName);
            bundle.putInt("badgenumber", count);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置vivo的Badge :vivoXplay5 vivo x7无效果
     */
    private static void setBadgeOfVIVO(Context context, int count) {
        try {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            String launchClassName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
            intent.putExtra("className", launchClassName);
            intent.putExtra("notificationNum", count);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Nova的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfNova(Context context, int count) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("tag", context.getPackageName() + "/" + AppInfoUtil.getLauncherClassName(context));
        contentValues.put("count", count);
        context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"),
                contentValues);
    }

    /**
     * 设置HTC的Badge
     *
     * @param context context
     * @param count   count
     */
    private static void setBadgeOfHTC(Context context, int count) {
        Intent intentNotification = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
        ComponentName localComponentName = new ComponentName(context.getPackageName(), AppInfoUtil.getLauncherClassName(context));
        intentNotification.putExtra("com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());
        intentNotification.putExtra("com.htc.launcher.extra.COUNT", count);
        context.sendBroadcast(intentNotification);

        Intent intentShortcut = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
        intentShortcut.putExtra("packagename", context.getPackageName());
        intentShortcut.putExtra("count", count);
        context.sendBroadcast(intentShortcut);
    }

    /**
     * 向小米手机发送未读消息数广播
     * 在小米5上测试通过
     * 但有一个很奇怪的地方，小米手机应用图标上显示的数量是跟通知栏有关的，通知栏有几条，图标上就显示几条
     * 而且如果清除掉通知栏，应用图标上的数字也会相应的减少，并且在打开app以后，数字会自动清空，这里我传入的count貌似并没有作用
     * 这一点我查过是小米自己默认的操作，应该是无法更改的，我在小米手机上测试了今日头条，发现也是这样的情况。
     *
     * @param count
     */
    public static void sendToXiaoMi(Context context, int count) {
        try {


            Notification notification = new Notification();
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, count);

        } catch (Exception e) {
            e.printStackTrace();

            // miui 6之前的版本

            Intent localIntent = new Intent(
                    "android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra(
                    "android.intent.extra.update_application_component_name",
                    context.getPackageName() + "/" + getLauncherClassName(context));
            localIntent.putExtra(
                    "android.intent.extra.update_application_message_text"
                    , String.valueOf(count == 0 ? "" : count));
            context.sendBroadcast(localIntent);
        }

    }

    /**
     * 向索尼手机发送未读消息数广播<br/>
     * 据说：需添加权限：
     * <uses-permission
     * android:name="com.sonyericsson.home.permission.BROADCAST_BADGE" /> [未验证]
     *
     * @param count
     */
    private static void sendToSony(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }

        boolean isShow = true;
        if (count == 0) {
            isShow = false;
        }
        Intent localIntent = new Intent();

        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE"
                , isShow);//是否显示

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME"
                , launcherClassName);//启动页

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE"
                , String.valueOf(count));//数字

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME"
                , context.getPackageName());//包名

        context.sendBroadcast(localIntent);
    }

    /**
     * 向三星手机发送未读消息数广播
     *
     * @param count
     */
    private static void sendToSamsumg(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }


    /**
     * 重置、清除Badge未读显示数<br/>
     *
     * @param context
     */
    public static void resetBadgeCount(Context context) {
        setBadgeCount(context, 0);
    }

    /**
     * Retrieve launcher activity name of the application from the context
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     * "android:name" attribute.
     */
    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }
}

