package com.xson.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xson on 2016/12/6.
 * 带头部的Adapter,用于头部固定，头部不需要请求的情况
 */

public abstract class HeaderAdapter<H,T> extends SwipeRefreshAdapter<T> {
    private SwipeRefreshAdapter<T> mSwipeRefreshAdapter;
    public HeaderAdapter(Context context, SwipeRefreshAdapter<T> swipeRefreshAdapter) {
        super(context);
        if (swipeRefreshAdapter == null) {
            throw new IllegalArgumentException("SwipeRefreshAdapter 不能为空");
        }
        mSwipeRefreshAdapter = swipeRefreshAdapter;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
           return  onCreateHeaderViewHolder(parent, viewType);
        }
        return mSwipeRefreshAdapter.onCreateViewHolder2(parent, viewType);
    }

    @Override
    public void onBindViewHolder2(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_HEADER) {
            onBindHeaderViewHolder(holder, position);
        } else {
            mSwipeRefreshAdapter.onBindViewHolder2(holder, position - getHeaderCount());
        }
    }


    @Override
    public int getItemViewType2(int position) {
        if (0 <= position && position < getHeaderCount()) {
            return TYPE_HEADER;
        }
        return super.getItemViewType2(position);
    }

    public int getHeaderCount() {
        return 1;
    }

    /**
     * 创建头部ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    public abstract RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    /**
     * 绑定头部 ViewHolder
     * @param holder
     * @param position
     */
    public abstract void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position);

    public void clear() {
        mSwipeRefreshAdapter.clear();
    }

    @Override
    public void setData(List<T> data) {
        mSwipeRefreshAdapter.setData(data);
    }

    @Override
    public void addData(List<T> data) {
        mSwipeRefreshAdapter.addData(data);
    }

    @Override
    public int getCount() {
       return mSwipeRefreshAdapter.getCount() + getHeaderCount();
    }

    public List<T> getData() {
        return mSwipeRefreshAdapter.getData();
    }

    public T getItem(int position) {
        return null;
    }
}
