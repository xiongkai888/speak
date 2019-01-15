package com.lanmei.speak.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.lanmei.speak.R;
import com.lanmei.speak.utils.CommonUtils;
import com.xson.common.app.BaseActivity;
import com.xson.common.utils.IntentUtil;

/**
 * 引导页、启动也
 */
public class SplashActivity extends BaseActivity {

    @Override
    public int getContentViewId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        return R.layout.activity_splash;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                enterHomeActivity();
            }
        }, 1000);
    }

    private void enterHomeActivity() {
        if (CommonUtils.isLogin(this)) {
            IntentUtil.startActivity(this, MainActivity.class);
        }
        finish();
    }
}
