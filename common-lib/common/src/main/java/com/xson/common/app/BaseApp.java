package com.xson.common.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.xson.common.api.AbstractApi;
import com.xson.common.utils.L;
import com.xson.common.utils.SysUtils;

public abstract class BaseApp extends Application implements Thread.UncaughtExceptionHandler {


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @SuppressLint("NewApi")
    public void init() {
        installMonitor();
        if (SysUtils.isDebug(this)) {
            L.debug = true;
            //内存泄漏监控
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            builder.detectAll();
            builder.penaltyLog();
            StrictMode.setVmPolicy(builder.build());
        }
        // 友盟捕获异常
//        MobclickAgent.setCatchUncaughtExceptions(true);
        // 捕捉未知异常
        Thread.setDefaultUncaughtExceptionHandler(this);
        // fix: java.lang.IllegalArgumentException: You must not call setTag() on a view Glide is targeting
//        ViewTarget.setTagId(R.id.tag_first);
        AbstractApi.API_URL = (String) SysUtils.getBuildConfigValue(this, "API_URL");
//        MobclickAgent.setDebugMode( true );
        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        L.e("fangApp", "错误：" + ex.getMessage(), ex);

        Intent dialogIntent = new Intent(this, CrashReportDialogActivity.class);
        dialogIntent.putExtra(CrashReportDialogActivity.KEY_MSG, ex.getMessage());
        dialogIntent.putExtra(CrashReportDialogActivity.KEY_TRACE, Log.getStackTraceString(ex));
        dialogIntent.putExtra(CrashReportDialogActivity.KEY_MEMORY_ERROR, ex instanceof OutOfMemoryError);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    protected abstract void installMonitor();

    public abstract void watch(Object object);
}
