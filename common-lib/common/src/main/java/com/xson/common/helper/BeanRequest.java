package com.xson.common.helper;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.data.volley.Cache;
import com.data.volley.NetworkResponse;
import com.data.volley.Response;
import com.data.volley.error.AuthFailureError;
import com.data.volley.error.ParseError;
import com.data.volley.error.VolleyError;
import com.data.volley.request.MultiPartRequest;
import com.data.volley.toolbox.HttpHeaderParser;
import com.xson.common.bean.BaseBean;
import com.xson.common.utils.L;
import com.xson.common.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Milk <249828165@qq.com>
 */
public class BeanRequest<T extends BaseBean> extends MultiPartRequest<T> {

    private final SuccessListener<T> mSuccessListener;
    private Map<String, Object> mPostParamMap = new HashMap<>();
    private OnFinishListener mFinishListener;
    private Context context;
    private boolean multipart = false;

    public static interface OnFinishListener {
        public void onFinish();
    }

    /**
     * Callback interface for delivering parsed responses.
     */
    public static abstract class SuccessListener<T> implements Response.Listener<T> {
        private final Type type;
        public boolean isCacheResult = false;

        protected SuccessListener() {
            Type superClass = getClass().getGenericSuperclass();
            type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        }

        public Type getType() {
            return type;
        }

        /**
         * Called when a response is received.
         */
        public abstract void onResponse(T response);
    }

    /**
     * 用户操作逻辑错误
     */
    public static class UserLevelError extends VolleyError {
        private BaseBean baseBean;

        public UserLevelError(String exceptionMessage, BaseBean bean) {
            super(exceptionMessage);
            this.baseBean = bean;
        }

        public BaseBean getBaseBean() {
            return baseBean;
        }
    }

    public void asMultipart() {
        multipart = true;
    }

    public BeanRequest(Context context,int method, String url, Map<String, Object> postParamMap, SuccessListener<T> successListener, Response.ErrorListener errorListener) {
        super(method, url, null, errorListener);
        L.d("BeanRequest", "URL=" + url + " POST=" + postParamMap);
        this.context = context;
        mSuccessListener = successListener;
        if (postParamMap != null) {
            Object value;
            for (Map.Entry<String, Object> entry : postParamMap.entrySet()) {
                value = entry.getValue();
                if (value instanceof File) {
                    File file = (File) value;
                    addFile(entry.getKey(), file.getAbsolutePath());
                } else {
                    addMultipartParam(entry.getKey(), "text/plain", String.valueOf(value));
                    mPostParamMap.put(entry.getKey(), String.valueOf(value));
                }
            }
        }
    }

    @Override
    public boolean hasFileToUpload() {
        if (multipart)
            return true;
        return super.hasFileToUpload();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        if (mSuccessListener == null)
            return Response.success(null, entry);
        try {
            byte[] data = response.data;
            String encoding = response.headers.get("Content-Encoding");
            if (encoding != null && encoding.equals("gzip")) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(data));
                byte[] buffer = new byte[2048];
                int n;
                while ((n = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }
                in.close();
                data = out.toByteArray();
            }

            //当没有网络请求时间时，则当前结果是缓存的结果
            mSuccessListener.isCacheResult = response.networkTimeMs == 0;

            String jsonString = new String(data, "UTF-8");
            if (!mSuccessListener.isCacheResult)
                L.d("BeanRequest", "Response=" + jsonString);
            BaseBean baseBean = JSON.parseObject(jsonString, BaseBean.class); // 有时候返回成功格式和失败格式不一样，导致解析失败，所以现在这里解析一下先

            if (baseBean == null) {//
                baseBean = new BaseBean();
                baseBean.setMsg("无法获取想要的数据");
                return Response.error(new UserLevelError(baseBean.getMsg(), baseBean));
            }
            if (200 != baseBean.getCode()) {//
                return Response.error(new UserLevelError(baseBean.getMsg(), baseBean));
            }
            Type type = mSuccessListener.getType();
            T bean = JSON.parseObject(jsonString, type);
            //按我们的规则设置缓存，比如只缓存第一页
            if (shouldCache()) {
                entry.softTtl = 0; //小于当前时间时，先刷新本地数据，再请求网络，否则不请求网络
                entry.ttl = Long.MAX_VALUE;
            } else {
                entry.softTtl = 0;
                entry.ttl = 0;
            }
            return Response.success(bean, entry);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (Exception je) {
            je.printStackTrace();
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (mFinishListener != null)
            mFinishListener.onFinish();
        if (mSuccessListener != null)
            mSuccessListener.onResponse(response);
    }

    @Override
    protected Map<String, Object> getParams() throws AuthFailureError {
        return mPostParamMap;
    }

    @Override
    public void deliverError(VolleyError error) {
        if (mFinishListener != null)
            mFinishListener.onFinish();
        super.deliverError(error);
    }

    public void setFinishListener(OnFinishListener mFinishListener) {
        this.mFinishListener = mFinishListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept-Encoding", "gzip,deflate,sdch");
        headers.put("CacheData-Control", "no-cache");
        headers.put("Pragma", "no-cache");
        String access_token = SharedAccount.getInstance(context).getAccessToken();
        if (!StringUtils.isEmpty(access_token)){
            headers.put("x-access-uid", SharedAccount.getInstance(context).getAccessUid());
            headers.put("x-access-token", access_token);
            L.d("x-access-token","access_token != null:access_token:"+access_token+",access-uid:"+SharedAccount.getInstance(context).getAccessUid());
        }else {
            L.d("x-access-token","access_token == null");
        }
        return headers;
    }

    @Override
    public String getCacheKey() {
        String key = getUrl();
        Map<String, MultiPartParam> map = getMultipartParams();
        if (map != null && map.containsKey("params")) {
            String params = map.get("params").value;
            params = params.replaceFirst("\"head\":\\{[^\\}]+\\}", "");
            key += params;
        }
        return key;
    }
}
