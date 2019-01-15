package com.xson.common.helper;

import com.data.volley.Response;
import com.xson.common.api.AbstractApi;
import com.xson.common.bean.BaseBean;

/**
 * Created by xson on 2016/11/14.
 */

public interface IHttpClient {
    public void setUseCache(boolean useCache);
    public Response.ErrorListener getErrorListener( );
    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api);
    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api, BeanRequest.SuccessListener<T> successListener);
    public <T extends BaseBean> BeanRequest<T> request(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener);
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api);
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener);
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener);
    public <T extends BaseBean> BeanRequest<T> loadingRequest(AbstractApi api, BeanRequest.SuccessListener<T> successListener, Response.ErrorListener errorListener, String loadingText);

    public void cancelRequest(BeanRequest request);
}
