package com.lanmei.speak.ui.login;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;

import com.lanmei.speak.R;
import com.lanmei.speak.api.SpeakApi;
import com.lanmei.speak.event.ChangePwEvent;
import com.lanmei.speak.utils.CommonUtils;
import com.xson.common.app.BaseActivity;
import com.xson.common.bean.BaseBean;
import com.xson.common.helper.BeanRequest;
import com.xson.common.helper.HttpClient;
import com.xson.common.utils.StringUtils;
import com.xson.common.utils.UIHelper;
import com.xson.common.widget.CenterTitleToolbar;
import com.xson.common.widget.DrawClickableEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 重设密码（修改密码）
 */
public class RegisterActivity extends BaseActivity{

    @InjectView(R.id.toolbar)
    CenterTitleToolbar toolbar;
    @InjectView(R.id.pwd_et)
    DrawClickableEditText pwdEt;
    @InjectView(R.id.pwd_again_et)
    DrawClickableEditText pwdAgainEt;
    @InjectView(R.id.showPwd_iv)
    ImageView showPwdIv;
//    @InjectView(R.id.showPwd_again_iv)
//    ImageView showPwdAgainIv;



    @Override
    public int getContentViewId() {
        return R.layout.activity_register;
    }


    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);
        actionbar.setTitle(R.string.change_pw);
    }


    private boolean isShowPwd = false;//是否显示密码


    @OnClick({R.id.showPwd_iv, R.id.sure_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.showPwd_iv:
                if (!isShowPwd) {//显示密码
                    pwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPwdIv.setImageResource(R.drawable.pwd_on);
                } else {//隐藏密码
                    pwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPwdIv.setImageResource(R.drawable.pwd_off);
                }
                isShowPwd = !isShowPwd;
                break;
            case R.id.sure_bt://
                loadChangePw();
                break;
        }
    }


    //修改密码
    private void loadChangePw() {
        String pwd = CommonUtils.getStringByEditText(pwdEt);//
        if (StringUtils.isEmpty(pwd) || pwd.length() < 6) {
            UIHelper.ToastMessage(this, R.string.input_password_count);
            return;
        }
        String pwdAgain = CommonUtils.getStringByEditText(pwdAgainEt);//
        if (StringUtils.isEmpty(pwdAgain)) {
            UIHelper.ToastMessage(this, R.string.input_pwd_again);
            return;
        }
        if (!StringUtils.isSame(pwd, pwdAgain)) {
            UIHelper.ToastMessage(this, R.string.password_inconformity);
            return;
        }
        SpeakApi api = new SpeakApi("app/update_password");
        api.addParams("password", pwd);
        HttpClient.newInstance(this).loadingRequest(api, new BeanRequest.SuccessListener<BaseBean>() {
            @Override
            public void onResponse(BaseBean response) {
                if (isFinishing()) {
                    return;
                }
                UIHelper.ToastMessage(getContext(),response.getMsg());
                EventBus.getDefault().post(new ChangePwEvent());
                finish();
            }
        });
    }

}
