package com.xson.common.helper;

import android.content.Context;
import android.content.DialogInterface;

import com.alibaba.fastjson.JSON;
import com.data.volley.Response;
import com.data.volley.misc.AsyncTask;
import com.xson.common.api.AbstractApi;
import com.xson.common.bean.BaseBean;
import com.xson.common.utils.L;
import com.xson.common.widget.ProgressHUD;

import java.lang.ref.WeakReference;

/**
 * Created by xson on 2016/11/14.
 *
 * 本地获取json数据，在开发的前期阶段，当接口文档写好之后就可以投入开发，不用等待接口都写好后才开发
 */

public class MockHttpClient implements IHttpClient {
    private static final String TAG = "MockHttpClient";
    private static String mPageName = "com.xson.online";
    private ProgressHUD mProgressHUD;
    private final WeakReference<Context> contextWeakReference;
    private MockHttpClient(Context context) {
        this.contextWeakReference = new WeakReference<Context>(context);
    }

    public  static MockHttpClient newInstance(Context content) {
        return new MockHttpClient(content);
    }

    @Override
    public void setUseCache(boolean useCache) {

    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return null;
    }

    @Override
    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api) {
        return request(api,null,null);
    }

    @Override
    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api, BeanRequest.SuccessListener<T> successListener) {
        return request(api,successListener, null);
    }

    @Override
    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        return null;
    }

    @Override
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api) {
        throw new RuntimeException("不支持该方法");
    }

    @Override
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener) {
        return loadingRequest(api, successListener,null, null );
    }

    @Override
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        return loadingRequest(api, successListener,errorListener, null );
    }

    @Override
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText) {
        new LocalAsyncTask(api,successListener, loadingText).execute();
        return null;
    }


    // 加载本地数据，每次都是成功的
    private class LocalAsyncTask<T extends BaseBean> extends AsyncTask<Object,Void,T> {
        AbstractApi mApi;
        BeanRequest.SuccessListener<T> mSuccessListener;
        private String mLoadingText;
        public  LocalAsyncTask(AbstractApi api, BeanRequest.SuccessListener<T> successListener, String loadingText) {
            mApi = api;
            mSuccessListener = successListener;
            mLoadingText = loadingText;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startLoading(mLoadingText);
        }

        @Override
        protected T doInBackground(Object... params) {
            AbstractApi api = mApi;
            String url = api.getUrl();
            String name = url.substring(url.lastIndexOf("/") + 1);
            L.d(TAG, "url=" + url+"name=" + name);

            // 获取json.xml中lastPath 字段的json值
            Context context = contextWeakReference.get();
            String json = "";
            if (context != null) {
               int resId =  context.getResources().getIdentifier(name,"string",context.getPackageName());
                json = context.getResources().getString(resId);
            }
            L.d(TAG, "json=" + json);
            T bean = null;

            try {
                bean = JSON.parseObject(json, mSuccessListener.getType());
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bean;
        }

        @Override
        protected void onPostExecute(T baseBean) {
            super.onPostExecute(baseBean);
            if (mSuccessListener != null) {
                mSuccessListener.onResponse(baseBean);
            }
            stopLoading();
        }
    }

    @Override
    public void cancelRequest(BeanRequest request) {

    }


    private void startLoading(String loadingText) {
        try {
            if (contextWeakReference.get() == null)
                return;

            if (mProgressHUD != null && mProgressHUD.isShowing())
                mProgressHUD.dismiss();

            mProgressHUD = ProgressHUD.show(contextWeakReference.get(), loadingText, true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
        } catch (Exception e) {
            //java.lang.IllegalArgumentException: View=com.android.internal.policy.impl.PhoneWindow$DecorView{43b81b28 V.E..... R.....ID 0,0-396,341} not attached to window manager
            L.d(e);
        }

    }


    private void stopLoading() {
        if(mProgressHUD != null) {
            mProgressHUD.dismiss();
            mProgressHUD = null;
        }
    }
}
