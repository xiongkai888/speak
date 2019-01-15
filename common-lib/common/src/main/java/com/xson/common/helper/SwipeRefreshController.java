package com.xson.common.helper;

import android.content.Context;

import com.data.volley.Response;
import com.data.volley.error.VolleyError;
import com.xson.common.adapter.LoadMoreAdapter;
import com.xson.common.api.AbstractApi;
import com.xson.common.bean.AbsListBean;
import com.xson.common.utils.L;
import com.xson.common.widget.OnLoadingListener;
import com.xson.common.widget.OnTryLoadListener;
import com.xson.common.widget.SmartSwipeRefreshLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Milk <249828165@qq.com>
 */
public abstract class SwipeRefreshController<T2 extends AbsListBean> implements Response.ErrorListener, OnLoadingListener, OnTryLoadListener {
    private final WeakReference<SmartSwipeRefreshLayout> pullToRefresh;
    private final LoadMoreAdapter adapter;
    private final AbstractApi api;
    private final IHttpClient httpClient;
    private int page;

    private boolean mHasMore = false; // 是否还有数据
    private BeanRequest<T2> request;
    private final Type type;
    private CacheEnum cache = CacheEnum.CACHE_FIRST_PAGE;
    private boolean mIsCacheResult = false;

    public static enum CacheEnum {
        CACHE_FIRST_PAGE,
        CACHE_ALL,
        NO_CACHE
    }

    public void setHasMore(boolean mHasMore) {
        this.mHasMore = mHasMore;
    }

    public SwipeRefreshController(Context context, SmartSwipeRefreshLayout smartSwipeRefreshLayout, AbstractApi api, LoadMoreAdapter adapter) {
        if (smartSwipeRefreshLayout.getAdapter() == null) {
            L.e("Please set the " + adapter.getClass().getSimpleName() + " to SmartSwipeRefreshLayout.");
        }
        this.pullToRefresh = new WeakReference<>(smartSwipeRefreshLayout);
        this.adapter = adapter;
        this.api = api;
        Type superClass = getClass().getGenericSuperclass();
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        httpClient = HttpClient.newInstance(context); /*HttpClientFactory.newInstance(context);*/
        smartSwipeRefreshLayout.setOnLoadingListener(this);
        smartSwipeRefreshLayout.setOnTryLoadListener(this);
    }

    @Override
    public void onRefresh() {
        loadFirstPage();
    }

    @Override
    public void onLoadMore() {
        loadNextPage();
    }

    @Override
    public void onTryRefresh() {
        onRefresh();
    }

    @Override
    public void onTryLoadMore() {
        onLoadMore();
    }

    public void loadFirstPage() {
        loadPage(1);
    }

    public void loadNextPage() {
        if (!mHasMore) {
            if (pullToRefresh.get() == null)
                return;
            pullToRefresh.get().setLoadingMore(false);
            return;
        }
        loadPage(page + 1);
    }

    private void loadPage(final int p) {
        api.setPage(p);
        if (cache == CacheEnum.CACHE_FIRST_PAGE) {
            if (p == 1) {
                httpClient.setUseCache(true);
            } else {
                httpClient.setUseCache(false);
            }
        } else if (cache == CacheEnum.CACHE_ALL) {
            httpClient.setUseCache(true);
        } else {
            httpClient.setUseCache(false);
        }

        SmartSwipeRefreshLayout view = pullToRefresh.get();
        if (view == null)
            return;
        view.showLoading();

        request = httpClient.request(api, new BeanRequest.SuccessListener<T2>() {
            @Override
            public Type getType() {
                return type;
            }

            @Override
            public void onResponse(T2 response) {
                boolean isCacheResult = this.isCacheResult;
                mIsCacheResult = isCacheResult;
                page = p;
                SmartSwipeRefreshLayout view = pullToRefresh.get();
                if (view == null) //被回收或离开了当前页面
                    return;
                //是否自己处理结果
                if (!onSuccessResponse(response)) {
                    List data = response.getDataList();
                    AbsListBean.PageBean pageBean = response.getPage();
                    mHasMore = (pageBean != null && (pageBean.getPageNum() > pageBean.getCurPage()));
                    L.d("mHasMore", "mHasMore:" + mHasMore + ", pageBean.getPageNum():" + pageBean.getPageNum() + ", pageBean.getCurPage():" + pageBean.getCurPage());
                    if (page > 1) {
                        adapter.addData(data);
                        if (mIsFirstPageListener != null) {
                            mIsFirstPageListener.isMore();
                        }
                    } else {
                        adapter.setData(data);
                        if (mIsFirstPageListener != null) {
                            mIsFirstPageListener.isFirst();
                        }
                    }
                }
                /**
                 * {@link android.widget.ListView#layoutChildren}
                 * 数据修改后，要马上通知Adapter数据已经改变，期间若做其它操作会导致异常
                 */
                adapter.notifyDataSetChanged();
                if (!mHasMore) {
                    view.setMode(SmartSwipeRefreshLayout.Mode.REFRESH);
                } else {
                    view.setMode(SmartSwipeRefreshLayout.Mode.BOTH);
                }
                if (!isCacheResult) {
                    view.hideLoading();
                } else {
                    //数据变动后，可能就没有加载状态显示了，再显示一下
                    view.showLoading();
                }
            }
        }, this);
    }

    public void close() {
        httpClient.cancelRequest(request);
        request = null;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        SmartSwipeRefreshLayout view = pullToRefresh.get();
        if (view == null)
            return;

        String errMsg = error.getMessage();

        //SmartSwipeRefreshLayout不显示错误时，调用默认规则显示错误
        if (!view.setError(errMsg))
            httpClient.getErrorListener().onErrorResponse(error);
        //注意setError需要正确地判断是否正在loading
        view.hideLoading();
    }

    /**
     * 返回true表示手动处理返回数据，false表示由本控制器处理返回数据到Adapter
     *
     * @param response
     * @return
     */
    public boolean onSuccessResponse(T2 response) {
        return false;
    }

    public void setCacheType(CacheEnum cache) {
        this.cache = cache;
    }

    public boolean isCacheResult() {
        return mIsCacheResult;
    }

    public int getPage() {
        return page;
    }

    IsFirstPageListener mIsFirstPageListener;//第一页监听

    public interface IsFirstPageListener {
        void isFirst();//获取第一页数据的时候

        void isMore();//获取前n页数据的时候
    }

    public void setIsFirstPageListener(IsFirstPageListener isFirstPageListener) {
        mIsFirstPageListener = isFirstPageListener;
    }

}
