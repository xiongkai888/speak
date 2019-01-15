package com.xson.common.bean;

import java.util.List;

/**
 * @author Milk
 * 不翻页
 */
public class NoPageListBean<T> extends AbsListBean {

    public List<T> body;

    @Override
    public List<T> getDataList() {
        return body;
    }
}