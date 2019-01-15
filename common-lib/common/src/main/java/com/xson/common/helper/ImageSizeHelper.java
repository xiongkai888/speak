package com.xson.common.helper;

import android.text.TextUtils;

/**
 * Created by chunlin on 2016/1/18.
 */
public class ImageSizeHelper {
    public static final String SMALL_LABEL = "_s";
    public static final String MIDDLL_LABEL = "_m";
    public static final String BIG_LABEL = "_b";
    public static String getSmallImage(String url){
        if(TextUtils.isEmpty(url) || !url.contains("."))
            return "";
        String firsturlName = url.substring(0, url.lastIndexOf("."));
        String lasturlName =  url.substring(url.lastIndexOf("."));
        return firsturlName + SMALL_LABEL + lasturlName;
    }

    public static String getMiddleImage(String url){
        if(TextUtils.isEmpty(url) || !url.contains("."))
            return "";
        String firsturlName = url.substring(0, url.lastIndexOf("."));
        String lasturlName =  url.substring(url.lastIndexOf("."));
        return firsturlName + MIDDLL_LABEL + lasturlName;
    }

    public static String getLargeImage(String url){
        if(TextUtils.isEmpty(url) || !url.contains("."))
            return "";
        String firsturlName = url.substring(0, url.lastIndexOf("."));
        String lasturlName =  url.substring(url.lastIndexOf("."));
        return firsturlName + BIG_LABEL + lasturlName;
    }

    public static String[] getArrayBigImage(String[] urls){
        if(urls == null || urls.length < 1)
            return urls;
        String[] newUrls = new String[urls.length];
        for(int i= 0; i<urls.length; i++){
            newUrls[i] = getLargeImage(urls[i]);
        }
        return newUrls;
    }
}
