package com.lanmei.speak.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.lanmei.speak.R;
import com.lanmei.speak.service.SpeakService;
import com.lanmei.speak.ui.login.LoginActivity;
import com.xson.common.bean.UserBean;
import com.xson.common.helper.SharedAccount;
import com.xson.common.utils.IntentUtil;
import com.xson.common.utils.StringUtils;
import com.xson.common.utils.UserHelper;

public class CommonUtils {

    public final static String isOne = "1";
    public final static String isZero = "0";
    public final static String isTwo = "2";

    public static int quantity = 3;


    /**
     * 获取TextView 字符串
     *
     * @param textView
     * @return
     */
    public static String getStringByTextView(TextView textView) {
        return textView.getText().toString().trim();
    }

    /**
     * 获取EditText 字符串
     *
     * @param editText
     * @return
     */
    public static String getStringByEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isLogin(Context context) {
        if (!UserHelper.getInstance(context).hasLogin()) {
            IntentUtil.startActivity(context, LoginActivity.class);
            return false;
        }
        return true;
    }

    public static String getUserId(Context context) {
        UserBean bean = getUserBean(context);
        if (StringUtils.isEmpty(bean)) {
            return "";
        }
        return bean.getId() + "";
    }

    /**
     * @param context
     * @return 登录后返回的push_id
     */
    public static String getPushId(Context context) {
        return SharedAccount.getInstance(context).getPushId();
    }

    public static UserBean getUserBean(Context context) {
        return UserHelper.getInstance(context).getUserBean();
    }

    public static boolean isHuaWei(Context context){
       return StringUtils.isSame(context.getString(R.string.huawei),BadgeUtil.getClientType());
    }

    public static void startServiceToSpeak(Context context, String speak){
        if (!SharedAccount.getInstance(context).isOpenVoice()){
            return;
        }
        Intent intent = new Intent(context, SpeakService.class);
        intent.putExtra("speak",speak);
        context.startService(intent);
    }

}
