package com.xson.common.api;

import com.xson.common.utils.L;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Milk <249828165@qq.com>
 */
public abstract class AbstractApi {

    private int curPage;
    public static String API_URL = "";
    public HashMap<String, Object> paramsHashMap = new HashMap<String, Object>();

    public static enum Method {
        GET,
        POST,
    }

    public static enum Enctype {
        TEXT_PLAIN,
        MULTIPART,
    }

    protected abstract String getPath();

    public Method requestMethod() {
        return Method.POST;
    }

    public Enctype requestEnctype() {
        return Enctype.TEXT_PLAIN;
    }

    public String getUrl() {
        return API_URL + getPath();
    }

    public void setPage(int page) {
        this.curPage = page;
    }

    public AbstractApi addParams(String key, Object value) {
        paramsHashMap.put(key, value);
        return this;
    }


    public Map<String, Object> getParams() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        for (Map.Entry<String, Object> item : paramsHashMap.entrySet()) {
            params.put(item.getKey(), item.getValue());
        }
        if (curPage > 0) {
            params.put(L.p, curPage);
        }
        return params;

    }

}
