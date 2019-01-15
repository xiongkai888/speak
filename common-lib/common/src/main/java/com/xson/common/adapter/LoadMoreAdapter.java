package com.xson.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xson.common.R;
import com.xson.common.widget.OnTryLoadListener;

import java.util.List;


/**
 * @author Milk <249828165@qq.com>
 */
public abstract class LoadMoreAdapter<T> extends RecyclerView.Adapter {
    final public static int TYPE_HEADER = 100;
    final public static int TYPE_ITEM = 101;
//    final public static int TYPE_EMPTY = Integer.MAX_VALUE - 2;
    final public static int TYPE_FOOTER = 102;
    final public static int TYPE_LOADER = 103;

    private OnTryLoadListener onTryLoadListener;
    private boolean loading = false;
    private LoadMoreViewHolder loadMoreViewHolder;
    private View emptyView;
//    private boolean showEmpty = false;
    private boolean showError = false;

    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;
        public final TextView errorTextView;
        public final TextView tryAgainButton;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.ssrl___progress);
            errorTextView = (TextView) itemView.findViewById(R.id.ssrl___error_tv);
            tryAgainButton = (TextView) itemView.findViewById(R.id.ssrl___try_btn);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void showLoading(boolean loading) {
        if(loading == this.loading)
            return;
        this.loading = loading;
        //notifyDataSetChanged();
        if(loading) {
            notifyItemInserted(getItemCount());
        }else{
            notifyItemRemoved(getItemCount()+1);
        }
    }

    public void setOnTryLoadListener(OnTryLoadListener onTryLoadListener) {
        this.onTryLoadListener = onTryLoadListener;
    }

//    public void hideEmptyView() {
//        showEmpty = false;
//    }
//
//    public void showEmptyView() {
//        showEmpty = true;
//    }

    public void setError(String error) {
        showError = true;
        if(loadMoreViewHolder == null)
            return;
        loadMoreViewHolder.progressBar.setVisibility(View.GONE);
        loadMoreViewHolder.errorTextView.setText(error);
        loadMoreViewHolder.errorTextView.setVisibility(View.VISIBLE);
        loadMoreViewHolder.tryAgainButton.setVisibility(View.VISIBLE);
    }

    public void hideError() {
        showError = false;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

//    public View getEmptyView() {
//        return emptyView;
//    }

    /**
     * 使用时注意判断loading的情况，比如自定义data时，要判断 positions < data.size()
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if(loading && position >= getItemCount()-1)
            return TYPE_LOADER;
/*        else if (position == 0 && getCount() == 0 && showEmpty)
            return TYPE_EMPTY;*/

        return getItemViewType2(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_LOADER) {
            return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ssrl_load_more, parent, false));
        } /*else if(viewType == TYPE_EMPTY) {
            if (emptyView == null) {
                emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ssrl_empty, parent, false);
            }
            return new EmptyViewHolder(emptyView);
        }*/
        return onCreateViewHolder2(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LoadMoreViewHolder) {
            loadMoreViewHolder = (LoadMoreViewHolder) holder;
            if(onTryLoadListener != null) {
                loadMoreViewHolder.tryAgainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
                        loadMoreViewHolder.errorTextView.setVisibility(View.GONE);
                        loadMoreViewHolder.tryAgainButton.setVisibility(View.GONE);
                        onTryLoadListener.onTryLoadMore();
                    }
                });
            }
            return;
        } else if (holder instanceof EmptyViewHolder) {
            return;
        }
        onBindViewHolder2(holder, position);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder instanceof LoadMoreViewHolder) {
            loadMoreViewHolder = null;
        }
    }


    public int getItemViewType2(int position) {
        return TYPE_ITEM;
    }

    @Override
    public final int getItemCount() {
//        return getCount() + (loading ? 1 : 0);
        int dataCount = getCount();

        if (loading) {
            //加载更多滚动
            return dataCount + 1;
        } /*else if (dataCount == 0) {
            //为空时的View
            return 1;
        }*/
        return dataCount;
    }

    abstract public RecyclerView.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType);
    abstract public void onBindViewHolder2(RecyclerView.ViewHolder holder, int position);

    /**
     * 仅列表数据部分个数，不包括头，尾这些非列表数据类型个数
     * @return
     */
    abstract public int getCount() ;

    abstract public void setData(List<T> data);

    abstract public void addData(List<T> data);
}
