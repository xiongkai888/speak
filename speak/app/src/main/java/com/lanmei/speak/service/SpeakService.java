package com.lanmei.speak.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.lanmei.speak.event.ArriveEvent;
import com.lanmei.speak.event.StopServiceEvent;
import com.lanmei.speak.ui.SpeakApp;
import com.lanmei.speak.xiaomi.XiaoMiReceiver;
import com.xson.common.utils.L;
import com.xson.common.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by xkai on 2018/8/3.
 */

public class SpeakService extends Service {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://
                    L.d(XiaoMiReceiver.TAG, "SpeakService.HEART_BEAT_RATE == 10000");
//                    UIHelper.ToastMessage(SpeakService.this,"HEART_BEAT_RATE");
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        L.d(XiaoMiReceiver.TAG, "SpeakService:onCreate");
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);// 发送心跳包
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d(XiaoMiReceiver.TAG, "SpeakService:onStartCommand");
        if (intent != null){
            String speak = intent.getStringExtra("speak");
            if (!StringUtils.isEmpty(speak)){
                L.d(XiaoMiReceiver.TAG, "SpeakService:onStartCommand:speak"+speak);
                SpeakApp.instance.getTtsHelper().speak(speak,true);//华为走这里
                EventBus.getDefault().post(new ArriveEvent());//如果界面停留在账户流水界面，通知刷新界面
            }
        }
        return START_STICKY;
    }

    //停止服务
    @Subscribe
    public void stopService(StopServiceEvent event) {
        stopSelf();
    }


    /**
     * 心跳检测时间
     */
    private static final long HEART_BEAT_RATE = 15 * 1000;//一分钟更新位置信息

    private Runnable heartBeatRunnable = new Runnable() {//心跳包请求位置信息
        @Override
        public void run() {
            mHandler.postDelayed(this, HEART_BEAT_RATE);
            Message msg = mHandler.obtainMessage();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d(XiaoMiReceiver.TAG, "SpeakService:onDestroy");
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacks(heartBeatRunnable);
        mHandler = null;
    }

}
