package com.xson.common.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.data.volley.Request;
import com.data.volley.RequestQueue;
import com.data.volley.Response;
import com.data.volley.error.TimeoutError;
import com.data.volley.error.VolleyError;
import com.data.volley.toolbox.Volley;
import com.xson.common.R;
import com.xson.common.api.AbstractApi;
import com.xson.common.bean.BaseBean;
import com.xson.common.utils.L;
import com.xson.common.widget.ProgressHUD;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author Milk <249828165@qq.com>
 */
public class HttpClient implements IHttpClient {
    private static RequestQueue requestQueue;
    private WeakReference<Context> contextWeakReference;
    private ProgressHUD mProgressHUD;
    private boolean useCache = false;
    private ConcurrentLinkedQueue<BeanRequest> mWaitingRequests;
    private OnRequestFinishListener onRequestFinishListener;
    private boolean cancelable = true;
    private AbstractApi.Enctype enctype = AbstractApi.Enctype.TEXT_PLAIN;

    private HttpClient(Context context) {
        this.contextWeakReference = new WeakReference<Context>(context);
    }

    public static HttpClient newInstance(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return new HttpClient(context);
    }

    public static interface ErrorInterceptor {
        public boolean onError(Context context, Exception e);
    }

    public static interface OnRequestFinishListener {
        public void onFinish(BeanRequest request);
    }

    public void clearCache() {
        requestQueue.getCache().clear();
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api) {
        return request(api, null, getErrorListener());
    }

    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api, BeanRequest.SuccessListener<T> successListener) {
        return request(api, successListener, getErrorListener());
    }

    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        if (contextWeakReference.get() == null)
            return null;
        String url = api.getUrl();
        Map<String, Object> params = api.getParams();
//        api.handleParams(contextWeakReference.get(), params);
        if (api.requestMethod() == AbstractApi.Method.POST) {
            enctype = api.requestEnctype();
            return post(url, params, successListener, errorListener);
        } else {
            if (!params.isEmpty()) {
                url += "?" + mapToQueryString(params);
            }
            return get(url, successListener, errorListener);
        }
    }

    /**
     * 同步请求
     *
     * @param api
     * @param <T>
     * @return
     */
    public <T extends BaseBean> T syncRequest(AbstractApi api, BeanRequestFuture<T> future) throws InterruptedException, ExecutionException, TimeoutException {
        if (contextWeakReference.get() == null)
            return null;
        String url = api.getUrl();
        Map<String, Object> params = api.getParams();
//        api.handleParams(contextWeakReference.get(), params);
        BeanRequest<T> request;
        if (api.requestMethod() == AbstractApi.Method.POST) {
            enctype = api.requestEnctype();
            request = post(url, params, future, future);
        } else {
            if (!params.isEmpty()) {
                url += "?" + mapToQueryString(params);
            }
            request = get(url, future, future);
        }

        future.setRequest(request);
        // 默认为 3分钟 超时，超时时间过长，容易导致bug产生
        return future.get(180, TimeUnit.SECONDS);
    }

    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api) {
        return loadingRequest(api, null, getErrorListener(), null);
    }

    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener) {
        return loadingRequest(api, successListener, getErrorListener(), null);
    }

    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        return loadingRequest(api, successListener, errorListener, null);
    }

    public <T extends BaseBean> BeanRequest<T> request(boolean hasLoading, AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        return request(api, successListener, errorListener, null, hasLoading);
    }

    public <T extends BaseBean> BeanRequest<T> request(boolean hasLoading, AbstractApi api, BeanRequest.SuccessListener<T> successListener) {
        return request(api, successListener, null, null, hasLoading);
    }

    public <T extends BaseBean> BeanRequest<T> post(String url, Map<String, Object> postParamMap, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText, boolean hasLoading) {
        BeanRequest<T> request = new BeanRequest<>(contextWeakReference.get(),Request.Method.POST, url, postParamMap, successHandler(successListener), errorHandler(errorListener));
        if (enctype == AbstractApi.Enctype.MULTIPART)
            request.asMultipart();
        addRequest(request, hasLoading, loadingText);
        return request;
    }


    public <T extends BaseBean> BeanRequest<T> get(String url, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText, boolean hasLoading) {
        BeanRequest<T> request = new BeanRequest<>(contextWeakReference.get(),Request.Method.GET, url, null, successHandler(successListener), errorHandler(errorListener));
        addRequest(request, hasLoading, loadingText);
        return request;
    }

    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText, boolean hasLoading) {
        if (contextWeakReference.get() == null)
            return null;
        String url = api.getUrl();
        Map<String, Object> params = api.getParams();
//        api.handleParams(contextWeakReference.get(), params);
        if (api.requestMethod() == AbstractApi.Method.POST) {
            enctype = api.requestEnctype();
            return post(url, params, successListener, errorListener, loadingText, hasLoading);
        } else {
            if (!params.isEmpty()) {
                url += "?" + mapToQueryString(params);
            }
            return get(url, successListener, errorListener, loadingText, hasLoading);
        }
    }

    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText) {
        if (contextWeakReference.get() == null)
            return null;
        String url = api.getUrl();
        Map<String, Object> params = api.getParams();
//        api.handleParams(contextWeakReference.get(), params);
        if (api.requestMethod() == AbstractApi.Method.POST) {
            enctype = api.requestEnctype();
            return loadingPost(url, params, successListener, errorListener, loadingText);
        } else {
            if (!params.isEmpty()) {
                url += "?" + mapToQueryString(params);
            }
            return loadingGet(url, successListener, errorListener, loadingText);
        }
    }

    public <T extends BaseBean> BeanRequest<T> get(String url, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        BeanRequest<T> request = new BeanRequest<T>(contextWeakReference.get(),Request.Method.GET, url, null, successHandler(successListener), errorHandler(errorListener));
        addRequest(request, false, null);
        return request;
    }

    public <T extends BaseBean> BeanRequest<T> post(String url, Map<String, Object> postParamMap, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        BeanRequest<T> request = new BeanRequest<>(contextWeakReference.get(),Request.Method.POST, url, postParamMap, successHandler(successListener), errorHandler(errorListener));
        if (enctype == AbstractApi.Enctype.MULTIPART)
            request.asMultipart();
        addRequest(request, false, null);
        return request;
    }

    public <T extends BaseBean> BeanRequest<T> loadingGet(String url, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText) {
        BeanRequest<T> request = new BeanRequest<>(contextWeakReference.get(),Request.Method.GET, url, null, successHandler(successListener), errorHandler(errorListener));
        addRequest(request, true, loadingText);
        return request;
    }

    public <T extends BaseBean> BeanRequest<T> loadingPost(String url, Map<String, Object> postParamMap, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText) {
        BeanRequest<T> request = new BeanRequest<>(contextWeakReference.get(),Request.Method.POST, url, postParamMap, successHandler(successListener), errorHandler(errorListener));
        if (enctype == AbstractApi.Enctype.MULTIPART)
            request.asMultipart();
        addRequest(request, true, loadingText);
        return request;
    }

    private <T extends BaseBean> void addRequest(BeanRequest<T> request, boolean hasLoading, String loadingText) {
        String tag = getTagAndCount();

        if (hasLoading)
            startLoading(tag, loadingText);

        request.setTag(tag);
        request.setShouldCache(false); //has bug

        addRequestFinishListenerToRequest(request);

        requestQueue.add(request);

        if (mWaitingRequests == null)
            mWaitingRequests = new ConcurrentLinkedQueue<>();
        mWaitingRequests.add(request);
    }

    public void cancelRequest(BeanRequest request) {
        requestQueue.cancelAll(request.getTag());
    }

    private String mapToQueryString(Map<String, Object> params) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null || entry.getValue() instanceof File)
                    continue;
                if (entry.getValue() instanceof String[]) {
                    String[] files = (String[]) entry.getValue();
                    for (String file : files) {
                        encodedParams.append(URLEncoder.encode(entry.getKey() + "[]", "UTF-8"));
                        encodedParams.append('=');
                        encodedParams.append(URLEncoder.encode(String.valueOf(file), "UTF-8"));
                        encodedParams.append('&');
                    }
                } else {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
                    encodedParams.append('&');
                }
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: UTF-8", uee);
        }
    }


    private String getTagAndCount() {
        int num = requestQueue.getSequenceNumber();
        return "HttpRequest-" + num;
    }

    private static class ResolveMemoryLeakSuccessListener<T> extends BeanRequest.SuccessListener<T> {
        private final BeanRequest.SuccessListener<T> listener;
        private final Type type;

        private ResolveMemoryLeakSuccessListener(BeanRequest.SuccessListener<T> listener) {
            this.listener = listener;
            type = listener.getType();
        }

        @Override
        public void onResponse(T response) {
            if (listener != null)
                listener.onResponse(response);
        }

        @Override
        public Type getType() {
            return type;
        }
    }

    private <T extends BaseBean> BeanRequest.SuccessListener<T> successHandler(BeanRequest.SuccessListener<T> listener) {
        return new ResolveMemoryLeakSuccessListener<>(listener);
    }

    private static class ResolveMemoryLeakErrorListener implements Response.ErrorListener {
        private final WeakReference<Context> contextWeakReference;
        private final WeakReference<Response.ErrorListener> listenerWeakReference;

        private ResolveMemoryLeakErrorListener(WeakReference<Context> contextWeakReference, Response.ErrorListener listener) {
            this.contextWeakReference = contextWeakReference;
            this.listenerWeakReference = new WeakReference<Response.ErrorListener>(listener);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Context context = contextWeakReference.get();
            Response.ErrorListener errorListener = listenerWeakReference.get();
            if (context == null || errorListener == null)
                return;

            error = humanError(context, error);

            ApplicationInfo appInfo = null;
            try {
                appInfo = context.getPackageManager().getApplicationInfo(
                        context.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (appInfo != null && appInfo.metaData != null) {
                String interceptor = appInfo.metaData.getString("http_error_interceptor");
                if (!TextUtils.isEmpty(interceptor)) {
                    if (interceptor.startsWith(".")) {
                        interceptor = appInfo.packageName + interceptor;
                    }
                    try {
                        ErrorInterceptor errorInterceptor = (ErrorInterceptor) Class.forName(interceptor).newInstance();
                        if (errorInterceptor.onError(context, error))
                            return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (errorListener != null)
                errorListener.onErrorResponse(error);
        }
    }

    private static VolleyError humanError(Context context, VolleyError error) {

        if (!(error instanceof BeanRequest.UserLevelError)) {
            if (!NetworkHelper.isNetworkAvailable(context)) {
                error = new VolleyError(context.getString(R.string.network_unconnect), error);
            } else if (error instanceof TimeoutError) {
                error = new VolleyError(context.getString(R.string.http_request_timeout), error);
            } else {
                String msg;
                if (error != null && error.getCause() != null && error.getCause() instanceof JSONException) {
                    msg = context.getString(R.string.http_parse_error);
                } else {
                    msg = context.getString(R.string.http_request_error);
                }
                if (error != null && error.networkResponse != null) {
                    msg += " #" + error.networkResponse.statusCode;
                }
                error = new VolleyError(msg, error);
            }
        }
        return error;
    }

    public Response.ErrorListener errorHandler(final Response.ErrorListener errorListener) {
        return new ResolveMemoryLeakErrorListener(contextWeakReference, errorListener);
    }

    public Response.ErrorListener getErrorListener() {
        return new DefaultErrorListener(contextWeakReference.get());
    }

    public static class DefaultErrorListener implements Response.ErrorListener {
        private Context context;

        public DefaultErrorListener(Context context) {
            this.context = context;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            String msg;
            error = humanError(context, error);
            msg = error.getMessage();
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoading(final String tag, String loadingText) {
        try {
            if (contextWeakReference.get() == null)
                return;

            if (mProgressHUD != null && mProgressHUD.isShowing())
                mProgressHUD.dismiss();

            mProgressHUD = ProgressHUD.show(contextWeakReference.get(), loadingText, true, cancelable, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    requestQueue.cancelAll(tag);
                    if (onRequestFinishListener != null)
                        onRequestFinishListener.onFinish(null);
                }
            });
        } catch (Exception e) {
            //java.lang.IllegalArgumentException: View=com.android.internal.policy.impl.PhoneWindow$DecorView{43b81b28 V.E..... R.....ID 0,0-396,341} not attached to window manager
            L.d(e);
        }

    }

    private <T extends BaseBean> void addRequestFinishListenerToRequest(final BeanRequest<T> request) {
        request.setFinishListener(new BeanRequest.OnFinishListener() {
            @Override
            public void onFinish() {

                try {
                    if (mWaitingRequests != null) {
                        mWaitingRequests.remove(request);
                    }
                    if (mProgressHUD != null) {
                        mProgressHUD.dismiss();
                        mProgressHUD = null;
                    }
                } catch (Exception e) {
                    L.d(e);
                }
                if (onRequestFinishListener != null)
                    onRequestFinishListener.onFinish(request);

            }
        });
    }

    public int queueSize() {
        if (mWaitingRequests == null)
            return 0;
        return mWaitingRequests.size();
    }

    public static void cancelAll() {
        if (requestQueue != null)
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
    }

    public void setOnRequestFinishListener(OnRequestFinishListener onRequestFinishListener) {
        this.onRequestFinishListener = onRequestFinishListener;
    }

    public void setCanCancelProgressHUD(boolean cancelable) {
        this.cancelable = cancelable;
    }
}
