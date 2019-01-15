package com.xson.common.bean;

import java.util.List;

/**
 * @author Milk
 */
public class DataListBean<T> extends AbsListBean {

    public List<T> body;

    @Override
    public List<T> getDataList() {
        return body;
    }

}