package com.lanmei.speak.ui.main;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanmei.speak.R;
import com.lanmei.speak.adapter.AccountDetailsAdapter;
import com.lanmei.speak.api.SpeakApi;
import com.lanmei.speak.bean.AccountDetailsBean;
import com.lanmei.speak.bean.CBean;
import com.lanmei.speak.bean.DrawTotalBean;
import com.lanmei.speak.event.ArriveEvent;
import com.lanmei.speak.utils.FormatTime;
import com.xson.common.app.BaseActivity;
import com.xson.common.bean.DataBean;
import com.xson.common.bean.DataListBean;
import com.xson.common.helper.BeanRequest;
import com.xson.common.helper.HttpClient;
import com.xson.common.helper.SwipeRefreshController;
import com.xson.common.utils.IntentUtil;
import com.xson.common.utils.StringUtils;
import com.xson.common.utils.UIHelper;
import com.xson.common.widget.CenterTitleToolbar;
import com.xson.common.widget.SmartSwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DateTimePicker;

/**
 * 账户流水(订单列表)
 */
public class AccountDetailsActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    CenterTitleToolbar mToolbar;

    @InjectView(R.id.pull_refresh_rv)
    SmartSwipeRefreshLayout smartSwipeRefreshLayout;
    AccountDetailsAdapter mAdapter;
    @InjectView(R.id.today_tv)
    TextView todayTv;
    @InjectView(R.id.yesterday_tv)
    TextView yesterdayTv;
    @InjectView(R.id.this_month_tv)
    TextView thisMonthTv;
    @InjectView(R.id.last_month_tv)
    TextView lastMonthTv;
    @InjectView(R.id.filtrate_tv)
    TextView filtrateTv;//筛选

    @InjectView(R.id.c2_tv)
    TextView c2Tv;
    @InjectView(R.id.c5_tv)
    TextView c5Tv;
    @InjectView(R.id.draw_total_tv)
    TextView drawTotalTv;

    private Button sdateBt;
    private Button edateBt;
    private AlertDialog dialog;

    private SwipeRefreshController<DataListBean<AccountDetailsBean>> controller;
    private SpeakApi api;
    private SpeakApi apiC;
    private SpeakApi apiT;
    private FormatTime formatTime;
    private int type;
    private DateTimePicker picker;
    private FormatTime time;

    @Override
    public int getContentViewId() {
        return R.layout.activity_account_details;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        formatTime = new FormatTime();

        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(R.string.account_details);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        EventBus.getDefault().register(this);
        initTimeFiltrateDialog();
        initDatePicker();//开始和结束时间

        apiT = new SpeakApi("app/statistics_op");//参与抽奖的商品汇总金额
        apiT.addParams("arena_code", apiT.getArenaCode(this));
        setT("", "");



        apiC = new SpeakApi("app/statistics_oc");//消费2次以下和5次及以上的人数汇总
        apiC.addParams("arena_code", apiC.getArenaCode(this));
        setC("", "");


        api = new SpeakApi("app/order_list");//列表
        api.addParams("arena_code", api.getArenaCode(this))
                .addParams("pageSize", 20);

        smartSwipeRefreshLayout.initWithLinearLayout();
        mAdapter = new AccountDetailsAdapter(this);
        smartSwipeRefreshLayout.setAdapter(mAdapter);
        controller = new SwipeRefreshController<DataListBean<AccountDetailsBean>>(this, smartSwipeRefreshLayout, api, mAdapter) {
//            @Override
//            public boolean onSuccessResponse(DataListBean<AccountDetailsBean> response) {
//                AbsListBean.SummaryBean summaryBean = response.getSummary();
//                if (!StringUtils.isEmpty(summaryBean)) {
//                    L.d("BaseAppCompatActivity", summaryBean.getCoupon_fee_no_cash());
//                }
//                return super.onSuccessResponse(response);
//            }
        };
        controller.loadFirstPage();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕长亮
    }

    private void initDatePicker() {
        picker = new DateTimePicker(this, DateTimePicker.YEAR_MONTH_DAY, DateTimePicker.NONE);
        time = new FormatTime();
        int year = time.getYear();
        int month = time.getMonth();
        int day = time.getDay();
        picker.setDateRangeStart(2008, 1, 1);
        picker.setDateRangeEnd(year, month, day);
        picker.setSelectedItem(year, month, day, 0, 0);
//        picker.setLabel("年","月","日",":","");
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                String timeStr = year + "-" + month + "-" + day;
                if (timeType == 0) {
                    sdateBt.setText(timeStr);
                } else {
                    edateBt.setText(timeStr);
                }
            }
        });
    }

    @OnClick({R.id.today_tv, R.id.yesterday_tv, R.id.this_month_tv, R.id.last_month_tv, R.id.filtrate_tv, R.id.draw_detail_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.today_tv://今日
                setBg(todayTv, 0, true);
                break;
            case R.id.yesterday_tv://昨日
                setBg(yesterdayTv, 1, true);
                break;
            case R.id.this_month_tv://本月
                setBg(thisMonthTv, 2, true);
                break;
            case R.id.last_month_tv://上月
                setBg(lastMonthTv, 3, true);
                break;
            case R.id.filtrate_tv://筛选
                dialog.show();
                break;
            case R.id.draw_detail_tv://查看抽奖商品金额明细
                IntentUtil.startActivity(this,DrawDetailsActivity.class);
                break;
        }
    }

    /**
     * @param textView
     * @param type
     * @param isClick  是否是点击选择
     */
    private void setBg(TextView textView, int type, boolean isClick) {
        if (this.type == type && isClick) {
            return;
        }
        this.type = type;

        filtrateTv.setTextColor(getResources().getColor(R.color.black));

        textViewDefault();
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setBackgroundResource(R.drawable.order_time_button_on);
        refreshByType();
    }

    private void textViewDefault() {
        todayTv.setBackgroundResource(R.drawable.order_time_button_off);
        yesterdayTv.setBackgroundResource(R.drawable.order_time_button_off);
        thisMonthTv.setBackgroundResource(R.drawable.order_time_button_off);
        lastMonthTv.setBackgroundResource(R.drawable.order_time_button_off);

        todayTv.setTextColor(getResources().getColor(R.color.black));
        yesterdayTv.setTextColor(getResources().getColor(R.color.black));
        thisMonthTv.setTextColor(getResources().getColor(R.color.black));
        lastMonthTv.setTextColor(getResources().getColor(R.color.black));
    }

    private void refreshByType() {
        String startTime = "";
        String endTime = "";
        switch (type) {
            case 0:
                break;
            case 1:
                startTime = endTime = formatTime.getYesterdayDate();
                break;
            case 2:
                startTime = formatTime.getThisMonthDateS();
                endTime = formatTime.getThisMonthDateE();
                break;
            case 3:
                startTime = formatTime.getLastMonthDateS();
                endTime = formatTime.getLastMonthDateE();
                break;
        }
        api.addParams("sdate", startTime);
        api.addParams("edate", endTime);
        controller.loadFirstPage();
        setC(startTime, endTime);
        setT(startTime, endTime);
    }

    //顾客支付到帐播报语音时候调用
    @Subscribe
    public void arriveEvent(ArriveEvent event) {
//        setBg(todayTv, 0,false);
        refreshByType();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_filtrate, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_filtrate) {
//            dialog.show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private int timeType;

    public void initTimeFiltrateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_time_filtrate, null);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        sdateBt = (Button) view.findViewById(R.id.sdate_bt);//开始时间
        edateBt = (Button) view.findViewById(R.id.edate_bt);//结束时间
        Button sureBt = (Button) view.findViewById(R.id.sure_bt);//确定
        ImageView crossIv = (ImageView) view.findViewById(R.id.cross_iv);//

        sdateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = 0;
                picker.show();
            }
        });
        edateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = 1;
                picker.show();
            }
        });
        sureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureTime();
            }
        });
        crossIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void sureTime() {
        String sTime = sdateBt.getText() + "";
        if (StringUtils.isEmpty(sTime)) {
            UIHelper.ToastMessage(this, "请选择开始日期");
            return;
        }
        String eTime = edateBt.getText() + "";
        if (StringUtils.isEmpty(eTime)) {
            UIHelper.ToastMessage(this, "请选择结束日期");
            return;
        }

        try {
            if (time.dateToStampLong(sTime) > time.dateToStampLong(eTime)) {
                UIHelper.ToastMessage(getContext(), "开始时间不能大于结束时间");
                return;
            }
            api.addParams("sdate", sTime);
            api.addParams("edate", eTime);
            controller.loadFirstPage();
            dialog.dismiss();
            textViewDefault();
            setC(sTime, eTime);
            setT(sTime, eTime);
            filtrateTv.setTextColor(getResources().getColor(R.color.color33cea6));
            type = 4;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void setC(String sTime, String eTime) {
        apiC.addParams("sdate", sTime);
        apiC.addParams("edate", eTime);
        HttpClient.newInstance(this).request(apiC, new BeanRequest.SuccessListener<DataBean<CBean>>() {
            @Override
            public void onResponse(DataBean<CBean> response) {
                if (isFinishing()){
                    return;
                }
                CBean bean = response.body;
                if (bean == null){
                    return;
                }
                c2Tv.setText(String.format(getString(R.string.c2),String.valueOf(bean.getC2())));
                c5Tv.setText(String.format(getString(R.string.c5),String.valueOf(bean.getC5())));
            }
        });
    }

    private void setT(String sTime, String eTime) {
        apiT.addParams("sdate", sTime);
        apiT.addParams("edate", eTime);
        HttpClient.newInstance(this).request(apiT, new BeanRequest.SuccessListener<DataBean<DrawTotalBean>>() {
            @Override
            public void onResponse(DataBean<DrawTotalBean> response) {
                if (isFinishing()){
                    return;
                }
                DrawTotalBean bean = response.body;
                if (StringUtils.isEmpty(bean)){
                    return;
                }
                drawTotalTv.setText(String.format(getString(R.string.draw_total),bean.getTotal_price()));
            }
        });
    }

}
