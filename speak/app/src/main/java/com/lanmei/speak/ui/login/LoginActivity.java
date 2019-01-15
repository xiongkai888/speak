package com.lanmei.speak.ui.login;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lanmei.speak.R;
import com.lanmei.speak.api.SpeakApi;
import com.lanmei.speak.bean.TokenBean;
import com.lanmei.speak.event.RegisterEvent;
import com.lanmei.speak.huawei.HUAWEIPushRevicer;
import com.lanmei.speak.ui.MainActivity;
import com.lanmei.speak.ui.SpeakApp;
import com.lanmei.speak.utils.BadgeUtil;
import com.lanmei.speak.utils.CommonUtils;
import com.xson.common.app.BaseActivity;
import com.xson.common.bean.DataBean;
import com.xson.common.helper.BeanRequest;
import com.xson.common.helper.HttpClient;
import com.xson.common.helper.SharedAccount;
import com.xson.common.utils.IntentUtil;
import com.xson.common.utils.L;
import com.xson.common.utils.StringUtils;
import com.xson.common.utils.UIHelper;
import com.xson.common.widget.CenterTitleToolbar;
import com.xson.common.widget.DrawClickableEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {


    @InjectView(R.id.toolbar)
    CenterTitleToolbar toolbar;
    @InjectView(R.id.phone_et)
    DrawClickableEditText phoneEt;
    @InjectView(R.id.pwd_et)
    DrawClickableEditText pwdEt;
    @InjectView(R.id.showPwd_iv)
    ImageView showPwdIv;

    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }


    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.login);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);
        String mobile = SharedAccount.getInstance(this).getPhone();
        phoneEt.setText(mobile);
//        phoneEt.setText("711806211909002");
//        pwdEt.setText("123456");
        EventBus.getDefault().register(this);
    }


    @OnClick({R.id.showPwd_iv, R.id.forgotPwd_tv, R.id.login_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.showPwd_iv://显示密码
                showPwd();
                break;
            case R.id.forgotPwd_tv://忘记密码
                UIHelper.ToastMessage(this, R.string.developing);
//                IntentUtil.startActivity(this, RegisterActivity.class, CommonUtils.isTwo);
                break;
            case R.id.login_bt://登录
                login();
                break;
        }
    }

    String phone;

    private void login() {

        phone = CommonUtils.getStringByEditText(phoneEt);
        if (StringUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.input_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }
//        if (!StringUtils.isMobile(phone)) {
//            Toast.makeText(this, R.string.not_mobile_format, Toast.LENGTH_SHORT).show();
//            return;
//        }
        String pwd = CommonUtils.getStringByEditText(pwdEt);
        if (StringUtils.isEmpty(pwd) || pwd.length() < 6) {
            Toast.makeText(this, R.string.input_password_count, Toast.LENGTH_SHORT).show();
            return;
        }
        String clientType = BadgeUtil.getClientType();
        SpeakApi api = new SpeakApi("app/login");
        api.addParams("username", phone)
                .addParams("password", pwd)
                .addParams("client_type", clientType);
        if (StringUtils.isSame(clientType, getString(R.string.huawei))) {
            String token = SharedAccount.getInstance(this).getHuaWeiToken();
            if (StringUtils.isEmpty(token)) {
                SpeakApp.instance.registerPush();//获取华为token
                L.d(HUAWEIPushRevicer.TAG, "登录token：null");
                return;
            }
            L.d(HUAWEIPushRevicer.TAG, "登录token：" + token);
            api.addParams("client_huawei_id", token);
        }
        HttpClient.newInstance(this).loadingRequest(api, new BeanRequest.SuccessListener<DataBean<TokenBean>>() {
            @Override
            public void onResponse(DataBean<TokenBean> response) {
                if (isFinishing()) {
                    return;
                }
                TokenBean bean = response.body;
                if (bean == null) {
                    UIHelper.ToastMessage(getContext(), "数据为空");
                    return;
                }
                SharedAccount.getInstance(getContext()).savePhone(phone);
                SharedAccount.getInstance(getContext()).saveAccessToken(bean.getAccess_token());
                SharedAccount.getInstance(getContext()).saveAccessUid(bean.getAccess_uid());
                SharedAccount.getInstance(getContext()).savePushId(bean.getPush_id());
//                UIHelper.ToastMessage(getContext(),SharedAccount.getInstance(getContext()).getPushId());
                IntentUtil.startActivity(getContext(), MainActivity.class, CommonUtils.isOne);
                finish();
            }
        });
    }

    private boolean isShowPwd = false;//是否显示密码

    private void showPwd() {
        if (!isShowPwd) {//显示密码
            pwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            showPwdIv.setImageResource(R.drawable.pwd_on);
        } else {//隐藏密码
            pwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showPwdIv.setImageResource(R.drawable.pwd_off);
        }
        isShowPwd = !isShowPwd;
    }

    //注册后调用
    @Subscribe
    public void respondRegisterEvent(RegisterEvent event) {
        phoneEt.setText(event.getPhone());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}