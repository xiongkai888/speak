package com.lanmei.speak.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lanmei.speak.R;
import com.lanmei.speak.bean.DrawTotalDetailsBean;
import com.xson.common.adapter.SwipeRefreshAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 参与抽奖的商品金额明细
 */
public class DrawDetailsAdapter extends SwipeRefreshAdapter<DrawTotalDetailsBean> {


    public DrawDetailsAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_account_details, parent, false));
    }

    @Override
    public void onBindViewHolder2(RecyclerView.ViewHolder holder, int position) {
        DrawTotalDetailsBean bean = getItem(position);
        if (bean == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.paymentTypeTv.setText(bean.getGoods_name());
        viewHolder.orderSnTv.setText(String.format(context.getString(R.string.order_sn), bean.getOrder_sn()));
        viewHolder.timeTv.setText(String.format(context.getString(R.string.time), bean.getCreate_time()));
        viewHolder.arenaNameTv.setText(String.format(context.getString(R.string.price_sub), bean.getTotal_price_format()));
        viewHolder.cashierNameTv.setText(String.format(context.getString(R.string.num), String.valueOf(bean.getQuantity())));

        viewHolder.priceTv.setText(String.format(context.getString(R.string.total_price), String.valueOf(bean.getTotal_price())));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @InjectView(R.id.payment_type_tv)
        TextView paymentTypeTv;
        @InjectView(R.id.order_sn_tv)
        TextView orderSnTv;
        @InjectView(R.id.time_tv)
        TextView timeTv;
        @InjectView(R.id.arena_name_tv)
        TextView arenaNameTv;
        @InjectView(R.id.cashier_name_tv)
        TextView cashierNameTv;
        @InjectView(R.id.price_tv)
        TextView priceTv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

}
