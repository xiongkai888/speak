package com.lanmei.speak.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.lanmei.speak.R;
import com.lanmei.speak.api.SpeakApi;
import com.lanmei.speak.event.ChangePwEvent;
import com.lanmei.speak.event.SetUserEvent;
import com.lanmei.speak.event.StopServiceEvent;
import com.lanmei.speak.huawei.HUAWEIPushRevicer;
import com.lanmei.speak.service.SpeakService;
import com.lanmei.speak.ui.login.LoginActivity;
import com.lanmei.speak.ui.login.RegisterActivity;
import com.lanmei.speak.ui.main.AccountDetailsActivity;
import com.lanmei.speak.utils.AKDialog;
import com.lanmei.speak.utils.BadgeUtil;
import com.lanmei.speak.utils.CommonUtils;
import com.xson.common.app.BaseActivity;
import com.xson.common.bean.DataBean;
import com.xson.common.bean.UserBean;
import com.xson.common.helper.BeanRequest;
import com.xson.common.helper.HttpClient;
import com.xson.common.helper.SharedAccount;
import com.xson.common.utils.IntentUtil;
import com.xson.common.utils.L;
import com.xson.common.utils.StringUtils;
import com.xson.common.utils.UserHelper;
import com.xson.common.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST = 1;

    @InjectView(R.id.pic_iv)
    CircleImageView picIv;
    @InjectView(R.id.name_tv)
    TextView nameTv;
    @InjectView(R.id.open_voice_tv)
    TextView openVoiceTv;
    @InjectView(R.id.id_num_tv)
    TextView idNumTv;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        boolean voice = !SharedAccount.getInstance(this).isOpenVoice();
        openVoiceTv.setText(voice ? getString(R.string.open_voice) : getString(R.string.close_voice));
        L.d(HUAWEIPushRevicer.TAG, "initAllMembersView:" + !voice);
        EventBus.getDefault().register(this);
        setUser();
        SpeakApi api = new SpeakApi("app/get_userinfo");
        HttpClient.newInstance(this).request(api, new BeanRequest.SuccessListener<DataBean<UserBean>>() {
            @Override
            public void onResponse(DataBean<UserBean> response) {
                if (isFinishing()) {
                    return;
                }
                UserBean bean = response.body;
                if (bean == null) {
                    return;
                }
                UserHelper.getInstance(getContext()).saveBean(bean);
                EventBus.getDefault().post(new SetUserEvent());

                if (!StringUtils.isEmpty(getIntent().getStringExtra("value"))) {//从登录界面跳转进来
                    SpeakApp.instance.registerPush();
                    if (StringUtils.isSame(getString(R.string.huawei), BadgeUtil.getClientType())) {
                        HMSAgent.connect(MainActivity.this, new ConnectHandler() {
                            @Override
                            public void onConnect(int rst) {
                                L.d(HUAWEIPushRevicer.TAG, "HMSAgent.connect:rst" + rst);
                            }
                        });
                    }
                }
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕长亮
        initTTSPermission(this);
        startService(new Intent(this, SpeakService.class));//开启服务
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    public boolean initTTSPermission(Activity activity) {

        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity.getApplicationContext(), perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 123);
            return true;
        }
        return false;
    }

    @OnClick({R.id.open_voice_tv, R.id.help_tv, R.id.check_running_water_tv, R.id.logout_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.open_voice_tv://开启语音
                boolean voice = SharedAccount.getInstance(this).isOpenVoice();
                openVoiceTv.setText(voice ? getString(R.string.open_voice) : getString(R.string.close_voice));
                SharedAccount.getInstance(this).setOpenVoice(!voice);
                if (!voice) {
                    SpeakApp.instance.getTtsHelper().speak(getString(R.string.have_open_voice),false);
                } else {
                    SpeakApp.instance.getTtsHelper().speak(getString(R.string.have_close_voice),false);
                }
                L.d(HUAWEIPushRevicer.TAG, "onViewClicked:" + !voice);
                break;
            case R.id.help_tv://修改密码
                IntentUtil.startActivity(this, RegisterActivity.class);
                break;
            case R.id.check_running_water_tv://查看流水
                IntentUtil.startActivity(this, AccountDetailsActivity.class);
                break;
            case R.id.logout_tv://退出
                AKDialog.getAlertDialog(this, "确定要退出登录?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                break;
        }
    }

    private void logout() {
        SpeakApp.instance.unregisterPush();//先退出推送
        UserHelper.getInstance(getContext()).cleanLogin();//再清空数据
        SharedAccount.getInstance(getContext()).saveAccessToken("");
        SharedAccount.getInstance(getContext()).saveAccessUid("");
        SharedAccount.getInstance(getContext()).savePushId("");
        SharedAccount.getInstance(getContext()).setOpenVoice(true);//默认开启语音
        IntentUtil.startActivity(getContext(), LoginActivity.class);
        EventBus.getDefault().post(new StopServiceEvent());//停止接收语音
        finish();
    }


    @Subscribe
    public void setUserEvent(SetUserEvent event) {
        setUser();
    }

    //修改密码后调用
    @Subscribe
    public void changePwEvent(ChangePwEvent event) {
        logout();
    }


    private void setUser() {
        UserBean userBean = CommonUtils.getUserBean(this);
        if (StringUtils.isEmpty(userBean)) {
            picIv.setImageResource(R.drawable.default_pic);
            nameTv.setText(R.string.tourist);
            idNumTv.setText("");
            return;
        }
        picIv.setImageResource(R.drawable.default_pic);
        nameTv.setText(userBean.getArena_name());
//        idNumTv.setText(String.format(getString(R.string.id_num), userBean.getArena_code()));
        idNumTv.setText(userBean.getArena_code());
//        idNumTv.setText(String.format(getString(R.string.id_num), SharedAccount.getInstance(getContext()).getPushId() + " , " + BadgeUtil.getClientType()) + "推送");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            boolean granted = false;
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                }
            }
            if (granted) {
                L.d("x-access-token", "Permissions granted:");
            }
            finish();
        }
    }

    public static final String ACTION_SHOW_HOME = "android.intent.action.SHOW_HOME"; // 显示并滑动到首页，并且显示首页TAB

    public static void showHome(Context context, Application application) {
        Intent intent = new Intent(ACTION_SHOW_HOME);
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_HOME.equals(intent.getAction())) {

        }
    }

}
