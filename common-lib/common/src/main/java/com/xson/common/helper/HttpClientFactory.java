package com.xson.common.helper;

import android.content.Context;

/**
 * Created by xson on 2016/11/15.
 */

public class HttpClientFactory {
    public  static IHttpClient newInstance(Context content) {
        return MockHttpClient.newInstance(content);
    }
}
