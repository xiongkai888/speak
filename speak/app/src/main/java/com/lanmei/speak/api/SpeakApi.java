package com.lanmei.speak.api;

import com.xson.common.api.ApiV2;

/**
 * Created by xkai on 2018/1/8.
 */

public class SpeakApi extends ApiV2 {

    private String path;

    public SpeakApi(String path){
        this.path = path;
    }

    @Override
    protected String getPath() {
        return path;
    }
}
