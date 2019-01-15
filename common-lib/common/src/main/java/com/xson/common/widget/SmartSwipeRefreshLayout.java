package com.xson.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mugen.Mugen;
import com.mugen.MugenCallbacks;
import com.mugen.attachers.BaseAttacher;
import com.xson.common.R;
import com.xson.common.adapter.LoadMoreAdapter;
import com.xson.common.utils.L;

/**
 * @author Milk <249828165@qq.com>
 */
public class SmartSwipeRefreshLayout extends FrameLayout {
    private RecyclerView recyclerView;
    private View loadingLayout;
    private View errorLayout;
    private View emptyLayout;
    private OnLoadingListener onLoadingListener;
    private TextView errorTextView;
    private OnTryLoadListener onTryLoadListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean loadingMore = false;
    private Mode mode = Mode.REFRESH;
    private Drawable divider;
    private int dividerHeight;
    private BaseAttacher attacher;
    private HorizontalDividerItemDecoration.Builder itemDecoration;

    public static enum Mode {
        DISABLED,
        //pull from start
        REFRESH,
        //pull from end
        LOAD_MORE,
        //refresh and load more
        BOTH
    }

    public SmartSwipeRefreshLayout(Context context) {
        super(context);
    }

    public SmartSwipeRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartSwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, new int[]{
                android.R.attr.divider,
                android.R.attr.dividerHeight,
                R.attr.colorControlActivated
        }, defStyleAttr, defStyleRes);

        divider = a.getDrawable(0);
        dividerHeight = a.getDimensionPixelOffset(1, 0);
        int colorControlActivated = a.getColor(2, 0);
        a.recycle();
        L.d("colorControlActivated="+colorControlActivated);
        if (dividerHeight == 0 && divider != null) {
            dividerHeight = divider.getIntrinsicHeight();
        }

        a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SmartSwipeRefreshLayout, defStyleAttr, defStyleRes);
        int leftDividerMargin = a.getDimensionPixelOffset(R.styleable.SmartSwipeRefreshLayout_dividerLeftMargin, 0);
        int rightDividerMargin = a.getDimensionPixelOffset(R.styleable.SmartSwipeRefreshLayout_dividerRightMargin, 0);
        int topDividerMargin = a.getDimensionPixelOffset(R.styleable.SmartSwipeRefreshLayout_dividerTopMargin, 0);
        int bottomDividerMargin = a.getDimensionPixelOffset(R.styleable.SmartSwipeRefreshLayout_dividerBottomMargin, 0);
        a.recycle();

        inflate(context, R.layout.ssrl_swipe_refresh_layout, this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ssrl___swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.ssrl___recyclerView);

        itemDecoration = new HorizontalDividerItemDecoration.Builder(getContext());
        itemDecoration.drawable(divider)
                .size(dividerHeight)
                .margin(leftDividerMargin, rightDividerMargin);

        if (colorControlActivated != 0) {
            swipeRefreshLayout.setColorSchemeColors(colorControlActivated);
        }

        showLoadingLayout();
    }

    private void initRecyclerView() {
//        if(recyclerView != null)return;
        //mCollectionView can be a ListView, GridView, RecyclerView or any instance of AbsListView!
        attacher = Mugen.with(recyclerView, new MugenCallbacks() {
            @Override
            public void onLoadMore() {
                setLoadingMore(true);
                onLoadingListener.onLoadMore();
            }

            @Override
            public boolean isLoading() {
                /* Return true if a load operation is ongoing. This will
                * be used as an optimization to prevent further triggers
                * if the user scrolls up and scrolls back down before
                * the load operation finished.
                *
                * If there is no load operation ongoing, return false
                */
                return isLoadingMore();
            }

            @Override
            public boolean hasLoadedAllItems() {
                /*
                * If every item has been loaded from the data store, i.e., no more items are
                * left to fetched, you can start returning true here to prevent any more
                * triggers of the load more method as a form of optimization.
                *
                * This is useful when say, the data is being fetched from the network
                */
                //return false;
                return onLoadingListener == null || (mode != Mode.BOTH && mode != Mode.LOAD_MORE);
            }
        }).start();

        /* Use this to dynamically turn infinite scroll on or off. It is enabled by default */
        attacher.setLoadMoreEnabled(true);

        /* Use this to change when the onLoadMore() function is called.
        * By default, it is called when the scroll reaches 2 items from the bottom */
        attacher.setLoadMoreOffset(1);
    }

    public HorizontalDividerItemDecoration.Builder getItemDecoration() {
        return itemDecoration;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        if(attacher == null)
            return;
        /*
        * mugen uses an internal OnScrollListener to detect and trigger load events.
        * If you need to listen to scroll events yourself, you can set this and
        * mugen will automatically forward all scroll events to the listener.
        */
        recyclerView.addOnScrollListener(onScrollListener);
    }

    public void initWithLinearLayout() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
    }

    public void initGridLinearLayout(int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),spanCount);
        setLayoutManager(layoutManager);
    }

    /**
     *
     * @param spanCount
     * @param location  位置location 跨度 spanCount
     */
    public void initGridLinearLayout(final int spanCount,final int location) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),spanCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == location){
                    return spanCount;
                }
                return 1;
            }
        });
        setLayoutManager(layoutManager);
    }
    /**
     *
     * @param spanCount
     */
    public void initGridLinearLayout2(int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),spanCount);
        setLayoutManager(layoutManager);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        initRecyclerView();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            setLoadingMore(false);
            int totalItem = getAdapter().getCount();
            if(totalItem == 0) {
                showEmptyLayout();
            } else {
                showSwipeRefreshLayout();
            }
            //fix: 如果当前有数据，然后又变成没数据时，之前的list view会残留和empty view重叠
            recyclerView.requestLayout();
        }
    };

    public void setAdapter(LoadMoreAdapter adapter) {
        if(divider != null && dividerHeight > 0) {
            recyclerView.addItemDecoration(itemDecoration.build());
        }

        if(adapter == null && recyclerView.getAdapter() != null) {
            try {
                recyclerView.getAdapter().unregisterAdapterDataObserver(adapterDataObserver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        recyclerView.setAdapter(adapter);
        if(adapter != null) {
            adapter.registerAdapterDataObserver(adapterDataObserver);
//            adapterDataObserver.onChanged();
        }
    }

    public LoadMoreAdapter getAdapter() {
        return (LoadMoreAdapter)recyclerView.getAdapter();
    }

    private void ensureLoadingLayout() {
        if(loadingLayout == null) {
            loadingLayout = ((ViewStub)findViewById(R.id.ssrl___loading_vs)).inflate();
        }
    }

    private void hideLoadingLayout() {
        if(loadingLayout != null && loadingLayout.getVisibility() != GONE)
            loadingLayout.setVisibility(GONE);
    }

    private void showLoadingLayout() {
        hideErrorLayout();
        hideSwipeRefreshLayout();
        hideEmptyLayout();
        ensureLoadingLayout();

        if(loadingLayout.getVisibility() != VISIBLE)
            loadingLayout.setVisibility(VISIBLE);
    }

    private void hideEmptyLayout() {
//        if (getAdapter() != null)
//            getAdapter().hideEmptyView();
        if (emptyLayout != null) {
            emptyLayout.setVisibility(GONE);
        }
    }

    private void showEmptyLayout() {
        hideLoadingLayout();
        hideErrorLayout();
        showSwipeRefreshLayout();
        ensureEmptyLayout();
        emptyLayout.setVisibility(VISIBLE);
//        if (getAdapter() != null)
//            getAdapter().showEmptyView();
    }

    private void ensureErrorLayout() {
        if(errorLayout == null) {
            errorLayout = ((ViewStub)findViewById(R.id.ssrl___error_vs)).inflate();
            errorTextView = (TextView)errorLayout.findViewById(R.id.ssrl___error_tv);

            if(onTryLoadListener != null) {
                View tryAgainButton = errorLayout.findViewById(R.id.ssrl___try_btn);
                tryAgainButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingLayout();
                        onTryLoadListener.onTryRefresh();
                    }
                });
            }
        }
    }

    private void ensureEmptyLayout() {
        if(emptyLayout == null) {
            emptyLayout = ((ViewStub)findViewById(R.id.ssrl___empty)).inflate();
        }
    }

    private void hideErrorLayout() {
        if(errorLayout != null && errorLayout.getVisibility() != GONE)
            errorLayout.setVisibility(GONE);

        if (getAdapter() != null)
            getAdapter().hideError();
    }

    public boolean setError(String errorMessage) {
        if(!TextUtils.isEmpty(errorMessage) && getAdapter() != null && getAdapter().getCount() > 0) {
            TypedValue tv = new TypedValue();
            int actionBarHeight;
            if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            } else {
                actionBarHeight = 180;
            }
            //防止一些Rom下Toast为白色背景时字体颜色为黑色
            SpannableStringBuilder spannable = new SpannableStringBuilder(errorMessage);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, errorMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Toast toast = Toast.makeText(getContext(), spannable, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, actionBarHeight + 6);
            toast.getView().setBackgroundColor(Color.parseColor("#f0000000"));
            toast.show();
            return true;
        }
        if(isRefreshing() || isLoading() || isEmpty()) {
            hideLoading();
            hideLoadingLayout();
            hideEmptyLayout();
            hideSwipeRefreshLayout();

            ensureErrorLayout();
            errorLayout.setVisibility(VISIBLE);
            //通知Adapter不要显示EmptyView
            getAdapter().setError(errorMessage);
            if (!TextUtils.isEmpty(errorMessage)) {
                errorTextView.setText(errorMessage);
            }
            return true;
        } else if(isLoadingMore()) {
            getAdapter().setError(errorMessage);
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return getAdapter().getCount() == 0;
    }

    /**
     * 是否初始化加载状态
     * @return
     */
    public boolean isLoading() {
        return loadingLayout != null && loadingLayout.getVisibility() == VISIBLE;
    }

    /**
     * 是否下拉刷新状态
     * @return
     */
    public boolean isRefreshing() {
        return swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing();
    }

    /**
     * 是否加载更多状态
     * @return
     */
    public boolean isLoadingMore() {
        return loadingMore;
    }

    private void hideSwipeRefreshLayout() {
        if(swipeRefreshLayout.getVisibility() == GONE)
            return;
        swipeRefreshLayout.setVisibility(GONE);
        recyclerView.setVisibility(GONE);
    }

    private void showSwipeRefreshLayout() {
        hideLoadingLayout();
        hideErrorLayout();
        hideEmptyLayout();
        if(swipeRefreshLayout.getVisibility() == VISIBLE)
            return;
        recyclerView.setVisibility(VISIBLE);
        swipeRefreshLayout.setVisibility(VISIBLE);
    }

    public void setMode(Mode mode) {
        this.mode = mode;

        switch (mode) {
            case DISABLED:
                swipeRefreshLayout.setEnabled(false);
                break;
            case REFRESH:
                swipeRefreshLayout.setEnabled(true);
                break;
            case LOAD_MORE:
                swipeRefreshLayout.setEnabled(false);
                //避免当前位置在最底下没有更多数据，更新后有更多数据时不能上拉更新，这里自动帮Ta加载下一页
//                onScrollListener.onScrolled(recyclerView, 0, 0);
                break;
            case BOTH:
                swipeRefreshLayout.setEnabled(true);
                //避免当前位置在最底下没有更多数据，更新后有更多数据时不能上拉更新，这里自动帮Ta加载下一页
//                onScrollListener.onScrolled(recyclerView, 0, 0);
                break;
        }

    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SmartSwipeRefreshLayout.this.onLoadingListener.onRefresh();
            }
        });
    }

    public void setOnTryLoadListener(OnTryLoadListener onTryLoadListener) {
        this.onTryLoadListener = onTryLoadListener;
        if(getAdapter() != null)
            getAdapter().setOnTryLoadListener(onTryLoadListener);
    }

    public void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        getAdapter().showLoading(loadingMore);
    }

    public void showLoading() {
        if(isLoadingMore() || isLoading() || isRefreshing())
            return;

        /* 不管当前是否有数据，非手动操作的动作都当前下拉刷新处理 */
        setRefreshing(true);
    }

    public void hideLoading() {
        if(isLoadingMore())
            setLoadingMore(false);
        if(isRefreshing())
            setRefreshing(false);
        if(isLoading())
            hideLoadingLayout();
    }

    // use: getAdapter().setEmptyView();
//    public void setEmptyView(View view) {
//        emptyParentLayout.removeAllViews();
//        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp.gravity = Gravity.CENTER;
//        emptyParentLayout.addView(view, lp);
//    }

    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }
}
