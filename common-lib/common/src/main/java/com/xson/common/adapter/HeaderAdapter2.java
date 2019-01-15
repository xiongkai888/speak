package com.xson.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * Created by xson on 2016/12/6.
 * 带头部的Adapter,头部是动态的，如有数据请求返回的时候来确定是否显示头部
 */

public abstract class HeaderAdapter2<T> extends SwipeRefreshAdapter<T>{
    public HeaderAdapter2(Context context) {
        super(context);

    }

    private View mHeaderView;

    private AdapterView.OnItemClickListener mListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener li) {
        mListener = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void removeHeader() {
        mHeaderView = null;
        notifyItemRemoved(0);
    }


    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return  new HeaderViewHolder(mHeaderView);
        }
        return super.onCreateViewHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_HEADER) {
            return ;
        } else {
            super.onBindViewHolder(holder, position - getHeaderCount());
        }
    }


    @Override
    public int getCount() {
        return super.getCount() + getHeaderCount();
    }


    public int getHeaderCount() {
        return mHeaderView == null ? 0 : 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder{

        HeaderViewHolder(View view) {
            super(view);
        }
    }
}
