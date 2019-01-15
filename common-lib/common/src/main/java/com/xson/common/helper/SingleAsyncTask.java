package com.xson.common.helper;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 异步处理简单线程
 * Created by xson on 2016/7/22.
 */
public class SingleAsyncTask {
    private static HandlerThread sHandlerThread = new HandlerThread("SingleAsyncTask");
    private static Handler sHandler;

    static {
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper());
    }
    public static void post(Runnable r) {
        sHandler.post(r);
    }
}
